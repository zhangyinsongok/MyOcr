package com.example.treecy.myocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treecy.myocr.Impl.CharacterDivide;
import com.example.treecy.myocr.Impl.PretreatmentBuilderImpl;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.beans.SauvolaSettingBean;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.Handler;

public class MainActivity extends Activity {

    @ViewInject(R.id.btnPhoto)
    private  Button btnPhoto;
    @ViewInject(R.id.btnCamera)
    private  Button btnCamera;
    @ViewInject(R.id.btnSearch)
    private  Button btnSearch;
    @ViewInject(R.id.imageView)
    private  ImageView imageView;
    @ViewInject(R.id.imagePre)
    private  ImageView imagePre;
    @ViewInject(R.id.textView)
    private  TextView textView;
    @ViewInject(R.id.textLanguage)
    private  TextView textLanguage;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    private static String IMG_PATH = getSDPath() + java.io.File.separator
            + "myocr";
    boolean[] languageChoice = {true,true,true};
    private static String[] ZikuEng = {"chi_sim","eng","font"};
    private static String[] Ziku = {"中文字库","英文字库","自定义字库"};
    private static  String LanguageShow = "正在使用中文字库，英文字库，自定义字库";
    private static  String Language = "chi_sim+eng+font";
    private String rs = "";
    private Handler handler;
    private static Bitmap bitmapSelected;
    private SauvolaSettingBean sauvolaSettingBean = new SauvolaSettingBean(10,0.25f,2,2);
    private int pageSegMode = 3;
    private ConfigurationBean configurationBean = new ConfigurationBean(constants.NO,constants.NO,constants.NO,constants.NO,constants.NO,sauvolaSettingBean);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE,Menu.FIRST+1,1,"文字识别").setIcon(
                android.R.drawable.ic_menu_compass
        );
        menu.add(Menu.NONE,Menu.FIRST+2,2,"预处理设置").setIcon(
                android.R.drawable.ic_menu_edit
        );
        menu.add(Menu.NONE,Menu.FIRST+3,3,"识别设置").setIcon(
                android.R.drawable.ic_menu_gallery);

        menu.add(Menu.NONE,Menu.FIRST+4,4,"歌曲查询").setIcon(
                android.R.drawable.ic_menu_search
        );
        menu.add(Menu.NONE,Menu.FIRST+5,5,"字库选择").setIcon(
                android.R.drawable.ic_menu_more
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        int id = item.getItemId();
        switch (id) {

            case Menu.FIRST + 1:
                onActivityResult(constants.DO_OCR,-1,null);
                break;

            case Menu.FIRST + 2:
                builder.setTitle("请选择预处理方案").setIcon(android.R.drawable.ic_dialog_info);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View pretreatView = inflater.inflate(R.layout.pretreat_menu, null);
                Button btnGray = (Button)pretreatView.findViewById(R.id.gray_btn);
                Button btnEnhance = (Button)pretreatView.findViewById(R.id.enhance_btn);
                Button btnBinarize = (Button)pretreatView.findViewById(R.id.binarize_btn);
                Button btnMorph = (Button)pretreatView.findViewById(R.id.morph_btn);
                Button btnSkew = (Button)pretreatView.findViewById(R.id.skew_btn);
                btnGray.setOnClickListener(new MyOnClickListener("gray",constants.GrayItems,configurationBean.getGrayType()+1));
                btnEnhance.setOnClickListener(new MyOnClickListener("enhance",constants.EnhanceItems,configurationBean.getEnhanceType()+1));
                btnBinarize.setOnClickListener(new MyOnClickListener("binarize",constants.BinarizeItems,configurationBean.getBinarizeType()+1));
                btnMorph.setOnClickListener(new MyOnClickListener("morph",constants.MorphItems,configurationBean.getMorphType()+1));
                btnSkew.setOnClickListener(new MyOnClickListener("skew",constants.SkewItems,configurationBean.getSkewType()+1));
                builder.setView(pretreatView);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bitmapSelected!=null){
                            onActivityResult(constants.DO_PRETREATMENT, -1, null);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
            case Menu.FIRST + 3:
                OcrModeSingleChoiceButtonOnClick ocrModeSingleChoiceButtonOnClick = new OcrModeSingleChoiceButtonOnClick(pageSegMode);
                builder.setTitle("识别设置").setIcon(android.R.drawable.ic_dialog_info).
                        setSingleChoiceItems(new String[] {"列文字识别","列文字块识别","行文字块识别","行文字识别","自定分割单字识别"}, pageSegMode,ocrModeSingleChoiceButtonOnClick)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", ocrModeSingleChoiceButtonOnClick);
                builder.show();
                break;

            case Menu.FIRST + 4:
                if(rs.equals("")){
                    Toast.makeText(this, "请进行识别后使用该功能", Toast.LENGTH_LONG).show();
                }
                else{
                    final EditText inputServer = new EditText(MainActivity.this);
                    inputServer.setText(rs,TextView.BufferType.EDITABLE);
                    builder.setTitle("请确认歌曲名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                            .setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String temp = inputServer.getText().toString().replaceAll(" ","_");
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,MusicActivity.class);
                            intent.putExtra("musicname",temp);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
                break;

            case Menu.FIRST + 5:
                boolean[] tempChoice = new boolean[3];
                int i = 0;
                for(boolean T:languageChoice){
                    tempChoice[i++] = T;
                }
                builder.setTitle("请选择字库").setIcon(android.R.drawable.ic_dialog_info).setMultiChoiceItems(
                        Ziku,
                        tempChoice,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        }
                );
                builder.setPositiveButton("Ok", new LanguageMultiChoiceButtonOnClickOK(tempChoice));
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED)
            return;

        if (requestCode == constants.DO_PRETREATMENT) {
            Uri uri = Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg"));
            ContentResolver cr = this.getContentResolver();
            try {
                bitmapSelected = BitmapFactory.decodeStream(cr.openInputStream(uri));
                imageView.setImageBitmap(bitmapSelected);
            }
            catch (Exception e){
                System.out.println("DO_PRETREATMENT:"+e);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                        Message msg1 = new Message();
                        msg1.what = constants.PRETREATMENT;
                        handler.sendMessage(msg1);
                //        bitmapSelected = Pretreatment.doPretreatment(bitmapSelected,pretreatChoice,binarizeChoice,sauvolaSettingBean);
                        PretreatmentDirector pretreatmentDirector = new PretreatmentDirector(new PretreatmentBuilderImpl(bitmapSelected,configurationBean));
                        bitmapSelected = pretreatmentDirector.build();
           //             saveBitmap(bitmapSelected,new File(IMG_PATH, "bitmapSelected.jpg"));
                        Message msg2 = new Message();
                        msg2.what = constants.PRETREATMENT_DONE;
                        handler.sendMessage(msg2);
                }
            }).start();
        }

        if (requestCode == constants.DO_OCR) {
            if(bitmapSelected == null){
                Toast.makeText(this,"没有需要处理的图片!",Toast.LENGTH_LONG).show();
            }
            else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg3 = new Message();
                        msg3.what = constants.PROCESSING;
                        handler.sendMessage(msg3);
                        rs = doOcr(bitmapSelected);
                        Message msg4 = new Message();
                        msg4.what = RESULT_OK;
                        handler.sendMessage(msg4);
                    }
                }).start();
        }

        if(requestCode == constants.FROM_GALLERY){
            startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
        }


        if(requestCode == constants.FROM_CAMERA){
            startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    public String doOcr(Bitmap bitmap) {
        String text = "";

        long T1 = System.currentTimeMillis();

        TessBaseAPI baseApi = new TessBaseAPI();

        baseApi.init(getSDPath(), Language);

        System.out.println(baseApi.getInitLanguagesAsString());

        if(pageSegMode == 4){
            List<Bitmap> CharSet = new CharacterDivide(bitmap).Divide();
            baseApi.setPageSegMode(10);
            int i = 0;
            for(Bitmap charset :CharSet){
                charset = charset.copy(Bitmap.Config.ARGB_8888, true);
                baseApi.setImage(charset);
                text += baseApi.getUTF8Text();
                saveBitmap(charset,new File(IMG_PATH, i+".jpg"));
                i++;
            }
        }
        else {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            baseApi.setImage(bitmap);

            baseApi.setPageSegMode(pageSegMode + 4);

            text = baseApi.getUTF8Text();
        }
       ResultIterator iterator = baseApi.getResultIterator();

        List<Pair<String, Double>> choicesAndConfidences;
        iterator.begin();
        do {
            choicesAndConfidences = iterator.getChoicesAndConfidence(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);

            for (Pair<String, Double> choiceAndConfidence : choicesAndConfidences) {
                String choice = choiceAndConfidence.first;
                Double conf = choiceAndConfidence.second;
                System.out.println(choice + " 识别率:" + conf+"%");
            }
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL));

        if(text == ""){
            Toast.makeText(this,"预处理未达到要求！",Toast.LENGTH_LONG).show();
        }

        baseApi.clear();
        baseApi.end();

        long T2 = System.currentTimeMillis();
        System.out.println("识别时间："+ (T2-T1));
        return text;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    public void startPhotoCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent,constants.DO_PRETREATMENT);
    }

    public static void saveBitmap(Bitmap bitmap,File f) {
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btnPhoto)
    public void btnPhotoClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
        intent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, constants.DO_PRETREATMENT);
    }

    @OnClick(R.id.btnSearch)
    public void btnSearchClick(View v) {
        final EditText inputServer = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请输入歌曲名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String temp = inputServer.getText().toString().replaceAll(" ","_");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,MusicActivity.class);
                intent.putExtra("musicname",temp);
                startActivity(intent);
            }
        });
        builder.show();
    }

    @OnClick(R.id.btnCamera)
    public void btnCameraClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
        startActivityForResult(intent, constants.FROM_CAMERA);
    }

    public void init(){
        progressBar.setVisibility(View.INVISIBLE);
        File path = new File(IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        textLanguage.setText(LanguageShow);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case constants.PRETREATMENT:
                        progressBar.setVisibility(View.VISIBLE);
                        textView.setText("识别结果："+"正在预处理.....");
                        break;
                    case constants.PRETREATMENT_DONE:
                        imagePre.setImageBitmap(bitmapSelected);
                        textView.setText("识别结果："+"预处理完毕");
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case constants.PROCESSING:
                        progressBar.setVisibility(View.VISIBLE);
                        textView.setText("识别结果："+"正在识别....");
                        break;
                    case RESULT_OK:
                        textView.setText("识别结果："+rs);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
    }


    public class MyOnClickListener implements View.OnClickListener {
        private String[] item;
        private int index;
        private String type;

        public MyOnClickListener(String type,String[] item,int index) {
            this.item = item;
            this.index = index;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(type+"设置").setIcon(android.R.drawable.ic_dialog_info).
                    setSingleChoiceItems(item,index,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                    index = which;
                        }
                    })
                    .setNegativeButton("Cancel", null);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (type){
                        case "gray":configurationBean.setGrayType(index-1);break;
                        case "enhance":configurationBean.setEnhanceType(index-1);break;
                        case "binarize":configurationBean.setBinarizeType(index-1);if(index-1==constants.SAUVLOLA)SauvolaMenu();break;
                        case "morph":configurationBean.setMorphType(index-1);break;
                        case "skew":configurationBean.setSkewType(index-1);break;
                    }
                }
            });
            builder.show();
        }
    }

    private void SauvolaMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View sauvolaSetting = inflater.inflate(R.layout.sauvola, null);

        TextView whsizeText = (TextView)sauvolaSetting.findViewById(R.id.whsizeText);
        TextView factorText = (TextView)sauvolaSetting.findViewById(R.id.factorText);
        TextView nxText = (TextView)sauvolaSetting.findViewById(R.id.nxText);
        TextView nyText = (TextView)sauvolaSetting.findViewById(R.id.nyText);
        SeekBar sb1 = (SeekBar)sauvolaSetting.findViewById(R.id.seekBar);
        SeekBar sb2 = (SeekBar)sauvolaSetting.findViewById(R.id.seekBar2);
        SeekBar sb3 = (SeekBar)sauvolaSetting.findViewById(R.id.seekBar3);
        SeekBar sb4 = (SeekBar)sauvolaSetting.findViewById(R.id.seekBar4);

        whsizeText.setText("whsize:"+sauvolaSettingBean.getWhsize());
        factorText.setText("factor:"+sauvolaSettingBean.getFactor());
        nxText.setText("nx:"+sauvolaSettingBean.getNx());
        nyText.setText("nx:"+sauvolaSettingBean.getNy());
        sb1.setProgress(sauvolaSettingBean.getWhsize()-7);
        sb2.setProgress((int)((sauvolaSettingBean.getFactor()-0.1f)*20));
        sb3.setProgress(sauvolaSettingBean.getNx()-1);
        sb4.setProgress(sauvolaSettingBean.getNy()-1);

        sb1.setOnSeekBarChangeListener(new SeekbarListener(whsizeText));
        sb2.setOnSeekBarChangeListener(new SeekbarListener(factorText));
        sb3.setOnSeekBarChangeListener(new SeekbarListener(nxText));
        sb4.setOnSeekBarChangeListener(new SeekbarListener(nyText));

        builder.setView(sauvolaSetting);
        builder.setTitle("Sauvola算法参数选择").setIcon(android.R.drawable.ic_dialog_info);
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    class SeekbarListener implements SeekBar.OnSeekBarChangeListener{

        TextView textView;

        public SeekbarListener(TextView textView){
            this.textView = textView;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar.getId() == R.id.seekBar){
                sauvolaSettingBean.setWhsize(7+progress);
                textView.setText("whsize:"+sauvolaSettingBean.getWhsize());
            }
            else if(seekBar.getId() == R.id.seekBar2){
                sauvolaSettingBean.setFactor(0.1f + (float) progress / 20);
                textView.setText("factor:"+sauvolaSettingBean.getFactor());
            }
            else if(seekBar.getId() == R.id.seekBar3){
                sauvolaSettingBean.setNx(1+progress);
                textView.setText("nx:"+sauvolaSettingBean.getNx());
            }
            else {
                sauvolaSettingBean.setNy(1 + progress);
                textView.setText("ny:"+sauvolaSettingBean.getNy());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class OcrModeSingleChoiceButtonOnClick implements DialogInterface.OnClickListener {
        int index;

        public OcrModeSingleChoiceButtonOnClick(int index){
            this.index = index ;
        }

        public void onClick(DialogInterface dialog, int which){
            if (which >= 0){
                index = which;
            }
            else {
                pageSegMode = index;
            }
        }
    }

    private class LanguageMultiChoiceButtonOnClickOK implements DialogInterface.OnClickListener {
        private boolean[] tempChoice;

        LanguageMultiChoiceButtonOnClickOK(boolean[] tempChoice) {
            this.tempChoice = tempChoice;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            languageChoice = tempChoice;
            boolean first = true;
            for(int i = 0;i < 3; i++){
                if(languageChoice[i]){
                    if(first){
                        first = false;
                        Language =  ZikuEng[i];
                        LanguageShow = "正在使用"+Ziku[i];
                    }
                    else {
                        Language += "+"+ZikuEng[i];
                        LanguageShow += ","+Ziku[i];
                    }
                }
            }
            if(!languageChoice[1]&&languageChoice[0]){
                if(languageChoice[2]){
                    Language = "chi_sim+~eng+font";
                }
                else
                    Language = "chi_sim+~eng";
            }
            if(first)
                textLanguage.setText("您没有选择任何字库！");
            else
                textLanguage.setText(LanguageShow);
        }
    }
}

package com.example.treecy.myocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treecy.myocr.beans.SauvolaSettingBean;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    public static Button btnPhoto;
    private static Button btnCamera;
    private static Button btnSearch;
    public static ImageView imageView;
    public static ImageView imagePre;
    public static TextView textView;
    public static TextView textLanguage;
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
    private int binarizeChoice = constants.OTSUADAPTIVE;
    private String[] pretreatItem = {"滤波","边缘检测","二值化"};
    private boolean[] pretreatChoice = {false,false,false};
    private SauvolaSettingBean sauvolaSettingBean = new SauvolaSettingBean(10,0.25f,2,2);
    private int pageSegMode =3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        File path = new File(IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        btnPhoto =(Button)findViewById(R.id.btnPhoto);
        btnCamera =(Button)findViewById(R.id.btnCamera);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        imageView = (ImageView) findViewById(R.id.imageView);
        imagePre = (ImageView) findViewById(R.id.imagePre);
        textView = (TextView)findViewById(R.id.textView);
        textLanguage =  (TextView)findViewById(R.id.textLanguage);
        textLanguage.setText(LanguageShow);
        btnPhoto.setOnClickListener(new onBtnPhotoListener());
        btnCamera.setOnClickListener(new onBtnCameraListener());
        btnSearch.setOnClickListener(new onBtnSearchListener());

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case constants.PRETREATMENT:
                        textView.setText("识别结果："+"正在预处理.....");
                        break;
                    case constants.PRETREATMENT_DONE:
                        imagePre.setImageBitmap(bitmapSelected);
                        textView.setText("识别结果："+"预处理完毕");
                        break;
                    case constants.PROCESSING:
                        textView.setText("识别结果："+"正在识别....");
                        break;
                    case RESULT_OK:
                        textView.setText("识别结果："+rs);
                        break;
                }
            }
        };
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
                boolean[] tempChoice = new boolean[3];
                int i = 0;
                for(boolean T:pretreatChoice){
                    tempChoice[i++] = T;
                }
                builder.setTitle("请选择预处理方案").setIcon(android.R.drawable.ic_dialog_info).setMultiChoiceItems(
                        pretreatItem,
                        tempChoice,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        }
                );
                builder.setPositiveButton("Ok", new PretreatMultiChoiceButtonOnClickOK(tempChoice));
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
            case Menu.FIRST + 3:
                OcrModeSingleChoiceButtonOnClick ocrModeSingleChoiceButtonOnClick = new OcrModeSingleChoiceButtonOnClick(pageSegMode);
                builder.setTitle("识别设置").setIcon(android.R.drawable.ic_dialog_info).
                        setSingleChoiceItems(new String[] {"列文字识别","列文字块识别","行文字块识别","行文字识别"}, pageSegMode,ocrModeSingleChoiceButtonOnClick)
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
                tempChoice = new boolean[3];
                i = 0;
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

    private class PretreatMultiChoiceButtonOnClickOK implements DialogInterface.OnClickListener{
        private boolean[] tempChoice;

        PretreatMultiChoiceButtonOnClickOK(boolean[] tempChoice){
            this.tempChoice = tempChoice;
        }
        @Override
        public void onClick(DialogInterface dialog, int which){
                pretreatChoice = tempChoice;
                if(tempChoice[2] == true){
                    SingleChoiceButtonOnClick singleChoiceButtonOnClick = new SingleChoiceButtonOnClick(binarizeChoice);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("请选择二值化方案").setIcon(android.R.drawable.ic_dialog_info).
                            setSingleChoiceItems(new String[] {"自适应大津法","sauvola算法","改进NiBlack算法","迭代法"}, binarizeChoice,singleChoiceButtonOnClick)
                            .setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Ok", singleChoiceButtonOnClick);
                    builder.show();
                }
               else {
                    if(bitmapSelected != null){
                        onActivityResult(constants.DO_PRETREATMENT,-1,null);
                    }
                }
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

    private class SingleChoiceButtonOnClick implements DialogInterface.OnClickListener {
        int index;

        public SingleChoiceButtonOnClick(int index){
            this.index = index ;
        }

        public void onClick(DialogInterface dialog, int which){
            if (which >= 0){
                index = which;
            }
            else{
                if (which == DialogInterface.BUTTON_POSITIVE){
                    binarizeChoice = index;
                    if(binarizeChoice == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        View sauvolaSetting = inflater.inflate(R.layout.pretreat_menu, null);

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
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(bitmapSelected != null){
                                    onActivityResult(constants.DO_PRETREATMENT,-1,null);
                                }
                            }
                        });
                        builder.show();
                    }
                    else {
                        if (bitmapSelected != null) {
                            onActivityResult(constants.DO_PRETREATMENT, -1, null);
                        }
                    }
                }
            }
        }
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
                        bitmapSelected = Pretreatment.doPretreatment(bitmapSelected,pretreatChoice,binarizeChoice,sauvolaSettingBean);
                 //       saveBitmap(bitmapSelected,new File(IMG_PATH, "bitmapSelected.jpg"));
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
   class onBtnPhotoListener implements View.OnClickListener {

       @Override
       public void onClick(View v) {
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
   }

    class onBtnSearchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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
    }

    class onBtnCameraListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
            startActivityForResult(intent, constants.FROM_CAMERA);
        }
    }


    public String doOcr(Bitmap bitmap) {

        TessBaseAPI baseApi = new TessBaseAPI();

        baseApi.init(getSDPath(), Language);

        System.out.println(baseApi.getInitLanguagesAsString());

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        baseApi.setImage(bitmap);

        baseApi.setPageSegMode(pageSegMode + 4);

        String text = baseApi.getUTF8Text();

   /*     ResultIterator iterator = baseApi.getResultIterator();

        List<Pair<String, Double>> choicesAndConfidences;
        iterator.begin();
        do {
            choicesAndConfidences = iterator.getChoicesAndConfidence(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);

            for (Pair<String, Double> choiceAndConfidence : choicesAndConfidences) {
                String choice = choiceAndConfidence.first;
                Double conf = choiceAndConfidence.second;
                System.out.println(choice + " 识别率:" + conf+"%");
            }
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL)); */

        if(text == ""){
            Toast.makeText(this,"预处理未达到要求！",Toast.LENGTH_LONG).show();
        }

        baseApi.clear();
        baseApi.end();

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
}

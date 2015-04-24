package com.example.treecy.myocr;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import com.example.treecy.myocr.beans.MusicBean;

/**
 * Created by TreecY on 2015/3/28.
 */
public class MusicActivity extends Activity {
    private final int NETWORKSEARCH_COMPLETE = 0;
    private final int LOAD_PICTURE = 1;
    private  ListView networkMusicListView;
    private ListView localMusicListView;
    private  ContentResolver contentResolver;
    private String musicName = "";
    private Handler handler;
    private List<MusicBean> localMusicBeanList = new ArrayList<MusicBean>();
    private List<MusicBean> networkMusicBeanList;

    private final String[] from = { "songName", "artistName","albumName","picUrl"};
    private final int[] to = { R.id.TITLE, R.id.ARTIST,R.id.AlBUM,R.id.BK};
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);
        networkMusicListView = (ListView)findViewById(R.id.networkListView);
        localMusicListView = (ListView)findViewById(R.id.localListView);
        contentResolver = this.getContentResolver();
        musicName = getIntent().getStringExtra("musicname");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case NETWORKSEARCH_COMPLETE:
                        networkMusicBeanList = ParseJson((String)msg.obj);
                        showWebResult(networkMusicBeanList);
                        break;
                    case LOAD_PICTURE:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        networkMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusicByUrl(networkMusicBeanList.get(position).getAudioUrl());
            }
        });

        localMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(localMusicBeanList.get(position).getAudioUrl());
                playMusicByUrl(localMusicBeanList.get(position).getAudioUrl());
            }
        });

        ListViewInit();
        localSearch();
        if(checkNetworkAvailable(this))
        networkSearch();
        else
            Toast.makeText(MusicActivity.this,"当前网络不可用",Toast.LENGTH_LONG).show();
    }


    private void ListViewInit(){
        adapter = new SimpleAdapter(MusicActivity.this, list, R.layout.listview_layout, from, to);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Drawable){
                    ImageView iv = (ImageView)view;
                    iv.setImageDrawable((Drawable)data);
                    return true;

                }else{
                    return false;
                }
            }
        });
        networkMusicListView.setAdapter(adapter);
    }

    private List<MusicBean> ParseJson(String json){
        List<MusicBean> musicBeanList = new ArrayList<MusicBean>();
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("songs");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject temp = jsonArray.getJSONObject(i);
                JSONArray artists = temp.getJSONArray("artists");
                JSONObject album = temp.getJSONObject("album");
                String songId = temp.getString("id");
                String songName = temp.getString("name");
                String audioUrl = temp.getString("audio");
                String artistName = artists.getJSONObject(0).getString("name");
                String albumName = album.getString("name");
                String picUrl = album.getString("picUrl");
                MusicBean musicBean = new MusicBean(songId,songName,artistName,albumName,picUrl,audioUrl);
                musicBeanList.add(musicBean);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return musicBeanList;
    }

    private void networkSearch(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://s.music.163.com/search/get?s="+musicName+"&type=1&limit=5&offset=0");
                try{
                    HttpResponse httpResponse =  httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String response = EntityUtils.toString(httpEntity);
                        //回调handler
                        Message message = new Message();
                        message.what = NETWORKSEARCH_COMPLETE;
                        message.obj = response;
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
        }).start();
    }

    private void localSearch(){
        String[] projection = new String[]{constants._ID,constants.ARTIST,constants.TITLE,constants.ALBUM,constants.DATA};
        Cursor cursor = contentResolver.query(constants.MUSIC_URI,
                projection,
                constants.TITLE + " LIKE"+" '%"+ musicName +"%'",
                null,
                null);
        if(cursor!=null) {
            int musicColumnIndex;
            cursor.moveToFirst();
            if(cursor.isAfterLast())
             Toast.makeText(this,"在本地没有找到相关音乐",Toast.LENGTH_LONG).show();
            else {
                for (;!cursor.isAfterLast(); cursor.moveToNext()) {
                    musicColumnIndex = cursor.getColumnIndex(constants._ID);
                    String songId = cursor.getString(musicColumnIndex);
                    musicColumnIndex = cursor.getColumnIndex(constants.TITLE);
                    String songName = cursor.getString(musicColumnIndex);
                    musicColumnIndex = cursor.getColumnIndex(constants.ALBUM);
                    String albumName = cursor.getString(musicColumnIndex);
                    musicColumnIndex = cursor.getColumnIndex(constants.ARTIST);
                    String artistName = cursor.getString(musicColumnIndex);
                    musicColumnIndex = cursor.getColumnIndex(constants.DATA);
                    String audioUrl = cursor.getString(musicColumnIndex);
                    localMusicBeanList.add(new MusicBean(songId, songName, artistName, albumName, null, audioUrl));
                }
            }
        }
            showResult(localMusicBeanList, localMusicListView);

    }

    private void showResult(List<MusicBean> musicBeanList,ListView listView){
        String[] from = { "songName", "artistName","albumName","picUrl"};
        int[] to = { R.id.TITLE, R.id.ARTIST,R.id.AlBUM,R.id.BK};
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (MusicBean musicBean:musicBeanList) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("songName", "歌曲名："+musicBean.getSongName());
            m.put("artistName","歌手名："+ musicBean.getArtistName());
            m.put("albumName","专辑名："+musicBean.getAlbumName());
            m.put("picUrl",musicBean.getPicUrl());
            list.add(m);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.listview_layout, from, to);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Drawable){
                    ImageView iv = (ImageView)view;
                    iv.setImageDrawable((Drawable)data);
                    return true;

                }else{
                    return false;
                }
            }
            });
        listView.setAdapter(adapter);
    }


    private void showWebResult(List<MusicBean> musicBeanList) {
        for (MusicBean musicBean:musicBeanList) {
            new GetPicThread(musicBean).start();
        }
    }

    private void playMusicByUrl(String url){
        Intent intent = new Intent();
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, "audio/*");
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    class GetPicThread extends Thread{
        private MusicBean musicBean;
        public GetPicThread(MusicBean musicBean){
            this.musicBean = musicBean;
        }
        @Override
        public void run() {
            try
            {
                InputStream is = (InputStream) new URL(musicBean.getPicUrl()).getContent();
                Drawable d = Drawable.createFromStream(is, null);

                Map<String, Object> m = new HashMap<String, Object>();
                m.put("songName", "歌曲名："+musicBean.getSongName());
                m.put("artistName","歌手名："+ musicBean.getArtistName());
                m.put("albumName","专辑名："+musicBean.getAlbumName());
                m.put("picUrl",d);
                list.add(m);

                Message msg = new Message();
                msg.what = LOAD_PICTURE;
                handler.sendMessage(msg);
            }catch (Exception e) {
                System.out.println("GetPicThread Throws an Exception"+e);
            }
        }
    }

    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}

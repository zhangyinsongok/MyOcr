package com.example.treecy.myocr;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by TreecY on 2015/4/20.
 */
public class constants {

    public static final  int FROM_GALLERY = 0;
    public static final  int FROM_CAMERA = 1;
    public static final  int DO_PRETREATMENT = 2;
    public static final  int DO_OCR = 3;

    public static final  int PRETREATMENT = 1;
    public static final  int PRETREATMENT_DONE = 2;
    public static final  int PROCESSING = 3;

    public static final  int  OTSUADAPTIVE = 0;
    public static final  int SAUVLOLA = 1;
    public static final  int IMPROVEDNiBlack = 2;
    public static final  int ITERATION = 3;

    //MusicActivity

    public final static Uri MUSIC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public final static String _ID = MediaStore.Audio.Media._ID;
    public final static String ARTIST = MediaStore.Audio.Media.ARTIST;
    public final static String TITLE = MediaStore.Audio.Media.TITLE;
    public final static String ALBUM = MediaStore.Audio.Media.ALBUM;
    public final static String DATA = MediaStore.Audio.Media.DATA;

}

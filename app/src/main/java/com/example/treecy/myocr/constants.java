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
    public static final  int OTSU = 4;
    public static final  int NIBLACK = 5;

    //MusicActivity
    public final static int NETWORKSEARCH_COMPLETE = 0;
    public final static int LOAD_PICTURE = 1;

    public final static Uri MUSIC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public final static String _ID = MediaStore.Audio.Media._ID;
    public final static String ARTIST = MediaStore.Audio.Media.ARTIST;
    public final static String TITLE = MediaStore.Audio.Media.TITLE;
    public final static String ALBUM = MediaStore.Audio.Media.ALBUM;
    public final static String ALBUMID = MediaStore.Audio.Media.ALBUM_ID;
    public final static String DATA = MediaStore.Audio.Media.DATA;

    public final static int ENHANCEMENT = 0;
    public final static int GREYSTRECHING = 1;
    public final static int SMOOTH = 2;
    public final static int SHARP = 3;

    public final static int RUST = 0;
    public final static int EXPEND = 1;

    public final static int NO = -1;

    public final static int HOUGH = 0;

    public final static int GRAY = 0;

    public final static String[] GrayItems = {"不使用灰度化","灰度化"};
    public final static String[] EnhanceItems = {"不使用增强","灰度增强", "灰度拉伸", "平滑","锐化"};
    public final static String[] BinarizeItems = {"不使用二值化","自适应大津法", "sauvola算法", "改进NiBlack算法", "迭代法","大津法","NiBlack算法"};
    public final static String[] MorphItems = {"不使用形态学处理","腐蚀", "膨胀"};
    public final static String[] SkewItems = {"不使用倾斜矫正","倾斜矫正"};

}

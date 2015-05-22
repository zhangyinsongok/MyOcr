package com.example.treecy.myocr.InterfaceAndSuperClass;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by TreecY on 2015/5/6.
 */
public class InitSuper {
    protected int HEIGHT;
    protected int WIDTH;
    protected int[] pixels;
    protected int[] greyArr;
    protected int[] greyHistogram = new int[256];
    protected int MinGreyValue;
    protected int MaxGreyValue;

    protected void init(Bitmap bitmap){
        getInfo(bitmap);
        bitmap = doGrey(bitmap);
        getMinAndMaxGreyValue();

    }
    protected void getInfo(Bitmap bitmap){
        HEIGHT = bitmap.getHeight();
        WIDTH = bitmap.getWidth();
        pixels = new int[HEIGHT * WIDTH];
        greyArr= new int[HEIGHT * WIDTH];
        bitmap.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
    }

    protected Bitmap doGrey(Bitmap bitmap){
        for(int i = 0;i < HEIGHT;i++)
            for(int j = 0;j < WIDTH;j++){
                int color =  pixels[i * WIDTH + j];

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int grey =( red * 77 + green * 150 + blue * 29 ) >> 8;
                 //避免浮点数运算
                pixels[i * WIDTH + j] = Color.rgb(grey,grey,grey);              //像素矩阵用来生成图片
                greyArr[i * WIDTH + j] = grey;                                   //灰度矩阵用来进行二值化
                greyHistogram[grey]++;                                          //灰度直方图
            }
        Bitmap result = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,WIDTH,0,0,WIDTH,HEIGHT);
        return result;
    }

    protected void getMinAndMaxGreyValue() {                          //获取一张图片中的最大灰度值和最小灰度值
        int i = 0;
        while(greyHistogram[i]==0)
            i++;
        MinGreyValue = i;
        i=255;
        while(greyHistogram[i]==0)
            i--;
        MaxGreyValue = i;
    }
}

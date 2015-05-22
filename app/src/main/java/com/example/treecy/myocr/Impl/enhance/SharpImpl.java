package com.example.treecy.myocr.Impl.enhance;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/8.
 */
public class SharpImpl extends InitSuper implements PretreatmentMethod {
    int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            init(bitmap);
            return Sharp(bitmap);
        }

    private Bitmap Sharp(Bitmap bitmap){
        int grey;
        for(int i = 0;i<HEIGHT;i++){
            for(int j = 0;j<WIDTH;j++){
                grey =  Convolution(i,j);
                pixels[i * WIDTH + j] = Color.rgb(grey, grey, grey);
            }
        }
        Bitmap result = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,WIDTH,0,0,WIDTH,HEIGHT);
        return result;
    }

    private int Convolution(int row,int col){
        int temp = 0;
        int index = 0;
        for(int i = row -1 ;i<=row+1;i++){
            for(int j = col-1 ;j<=col +1;j++ ){
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                       temp += greyArr[i * WIDTH + j] * laplacian[index];
                }
                index++;
            }
        }
        if(temp>255)
            temp = 255;
        if (temp<0)
            temp = 0;
        return temp;
    }
}

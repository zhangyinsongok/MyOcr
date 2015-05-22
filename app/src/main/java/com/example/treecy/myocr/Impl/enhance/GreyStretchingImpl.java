package com.example.treecy.myocr.Impl.enhance;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/8.
 */
public class GreyStretchingImpl extends InitSuper implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            init(bitmap);
            return greyStretching();
 }

    private Bitmap greyStretching(){
        int sum = 0;
        int T = MaxGreyValue;
        while(sum < WIDTH * HEIGHT *0.1){
            sum += greyHistogram[T];
            T--;
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(greyArr[i * WIDTH + j] > T) {
                    greyArr[i * WIDTH + j] = 255;
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                }
                else {
                    int num = 0;double temp = 0;
                    for(int k = i-1 ;k <= i+1;k++){
                        if(k>=0&&k<HEIGHT){
                            temp += greyArr[k * WIDTH + j];
                            num++;
                        }
                    }
                    double avg = temp/num;
                    if(avg >= T)
                        greyArr[i * WIDTH + j] = 255;
                    else {
                        greyArr[i * WIDTH + j] = (int) (((avg - MinGreyValue) / (T - MinGreyValue)) * 255);
                        if(greyArr[i * WIDTH + j]<0){
                            greyArr[i * WIDTH + j] = 0;
                        }
                    }
                    pixels[i * WIDTH + j]  = Color.rgb(greyArr[i * WIDTH + j],greyArr[i * WIDTH + j],greyArr[i * WIDTH + j]);
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }
}

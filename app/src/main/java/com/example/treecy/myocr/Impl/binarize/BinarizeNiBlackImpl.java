package com.example.treecy.myocr.Impl.binarize;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/8.
 */
public class BinarizeNiBlackImpl extends InitSuper implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            init(bitmap);
            long t1=System.currentTimeMillis();
            Bitmap temp =  Niblack();
            long t2=System.currentTimeMillis();
            System.out.println("NiBlack运行时间"+(t2-t1));
            return temp;
    }

    private Bitmap Niblack(){
        int avg = 0;
        double deviation = 0;
        double T;
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WIDTH;j++){
                avg = getAverageGreyByNiBlack(i,j);
                deviation = getDeviationByNiBlack(avg,i,j);
                T = avg - 0.1*deviation;
                if(greyArr[i * WIDTH + j]>=T) {
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                }
                else {
                    pixels[i * WIDTH + j] = Color.rgb(0, 0, 0);
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private int getAverageGreyByNiBlack(int row,int col){
        double sum = 0;
        double num = 0;
        for(int i=row-7;i<=row+7;i++) {
            for (int j = col - 7; j <= col + 7; j++) {
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                    sum += greyArr[i * WIDTH + j];
                    num++;
                }
            }
        }
        return (int)(sum/num);
    }

    private int getDeviationByNiBlack(int avg,int row,int col){
        int sum = 0;
        double num = 0;
        for(int i=row-7;i<=row+7;i++) {
            for (int j = col - 7; j <= col + 7; j++) {
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                    sum += Math.pow((greyArr[i * WIDTH + j]-avg),2);
                    num++;
                }
            }
        }
        return (int)Math.sqrt(sum/num);
    }
}

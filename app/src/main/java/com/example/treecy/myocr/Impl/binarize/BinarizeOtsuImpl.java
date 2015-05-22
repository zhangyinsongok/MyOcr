package com.example.treecy.myocr.Impl.binarize;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/8.
 */
public class BinarizeOtsuImpl extends InitSuper implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            init(bitmap);
            long t1=System.currentTimeMillis();
            int T = getThresholdByOtsu(greyArr,MinGreyValue,MaxGreyValue);
            long t2=System.currentTimeMillis();
            System.out.println("大津法运行时间"+(t2-t1));
            return doBinarization(T);
  }

    private int getThresholdByOtsu(int[] greyArr,int MinGreyValue,int MaxGreyValue){                   //大津法求阈值
        int T=0;
        double AvgB=0,AvgF=0,AvgAll=0,MaxD=0;
        for(int i=MinGreyValue;i<MaxGreyValue;i++){
            double numF=0,numB=0,SumF=0,SumB=0;
            for(int grey:greyArr){
                if(grey>i){
                    numB++;
                    SumB+=grey;
                }
                else{
                    numF++;
                    SumF+=grey;
                }
            }
            AvgB = SumB/numB;
            AvgF = SumF/numF;
            AvgAll = (SumB+SumF)/(numB+numF);
            double D =(numB/(numB+numF))*(AvgB-AvgAll)*(AvgB-AvgAll) + (numF/(numB+numF))*(AvgF-AvgAll)*(AvgF-AvgAll);
            if(D>MaxD){
                MaxD = D;
                T = i;
            }
        }
        return T;
    }

    private Bitmap doBinarization(int T) {                          //通过阈值T进行二值化
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(greyArr[i * WIDTH + j]>T){
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                }
                else
                    pixels[i * WIDTH + j] = Color.rgb(0,0,0);
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }
}

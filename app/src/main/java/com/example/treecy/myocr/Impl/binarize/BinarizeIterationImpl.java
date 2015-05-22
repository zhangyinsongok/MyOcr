package com.example.treecy.myocr.Impl.binarize;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/5.
 */
public class BinarizeIterationImpl extends InitSuper implements PretreatmentMethod {

    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            init(bitmap);
            long t1=System.currentTimeMillis();
            int T = getThresholdByIteration(MinGreyValue, MaxGreyValue, greyArr, 0.2);
            long t2=System.currentTimeMillis();
            System.out.println("迭代法运行时间"+(t2-t1));
            return doBinarization(T);
    }

    private int getThresholdByIteration(int MinGreyValue,int MaxGreyValue,int[] greyArr,double Accuracy) {             //迭代法求阈值
        double T;
        double temp = (MinGreyValue + MaxGreyValue) / 2;
        do {
            T = temp;
            double numF=0,numB=0,SumF=0,SumB=0,AvgB=0,AvgF=0;
            for (int grey:greyArr){
                if (grey < T ) {
                    SumB += grey;
                    numB++;
                }
                if (grey > T) {
                    SumF += grey;
                    numF++;
                }
            }
            AvgB = SumB / numB;
            AvgF = SumF / numF;
            temp = (AvgB + AvgF) / 2;
        } while (Math.abs(T-temp)>Accuracy);
        return (int)T;
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

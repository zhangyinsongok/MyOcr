package com.example.treecy.myocr.Impl;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TreecY on 2015/5/9.
 */
public class CharacterDivide extends InitSuper{
    Bitmap bitmap;

    public CharacterDivide(Bitmap bitmap){
        this.bitmap = bitmap;
        init(bitmap);

    }

    public List<Bitmap> Divide(){
        List<Bitmap> Charactors = new ArrayList<Bitmap>();
        int[] black = new int[WIDTH];
        boolean start = false;
        int startIndex = 0;
        int endIndex = 0;
        for(int j=0;j<WIDTH;j++){
            int sum = 0;
            for(int i=0;i<HEIGHT;i++){
                if(greyArr[i * WIDTH + j] == 0){
                        sum++;
                }
            }
            black[j] = sum;
        }
        for(int i = 0 ;i<WIDTH ;i++){
            if(black[i]>0&&!start){
                start = true;
                startIndex = i;
            }
            if(start&&black[i]==0){
                endIndex = i;
                if(endIndex-startIndex>30) {
                    Charactors.add(Crop(startIndex, endIndex));
                    start = false;
                }
            }
        }
        return Charactors;
    }

    private Bitmap Crop(int x1,int x2){
        int widthCrop = x2-x1+1;
        int[] temp = new int[widthCrop * HEIGHT];
        for(int i = 0;i < HEIGHT;i++){
            for(int j = x1;j <= x2;j++){
                temp[i * widthCrop + j-x1] = pixels[i * WIDTH + j];
            }
        }
        System.out.println("字符宽度"+WIDTH);
        Bitmap result = Bitmap .createBitmap(widthCrop, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(temp, 0, widthCrop, 0, 0, widthCrop, HEIGHT);
        return result;
    }
}

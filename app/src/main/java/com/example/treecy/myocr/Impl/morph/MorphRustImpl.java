package com.example.treecy.myocr.Impl.morph;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/16.
 */
public class MorphRustImpl extends InitSuper implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            super.init(bitmap);
            int[][] shape = new int[][]{
                    {1, 0,0},
                    {1, 1,0},
                    {1, 1,1},
            };
            return Rust(shape);
    }

    private Bitmap Rust(int[][] shape){
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                  if(isRust(i,j,shape)){
                      pixels[i*WIDTH+j] = Color.rgb(0,0,0);
                  }
                else
                      pixels[i*WIDTH+j] = Color.rgb(255,255,255);
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private boolean isRust(int row,int col, int[][] shape){
        int shapeH = shape.length;
        int shapeW = shape[0].length;
        for(int i=row;i<row+shapeH;i++){
            for(int j=col;j<col+shapeW;j++){
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                    if(shape[i-row][j-col]==1&&greyArr[i*WIDTH+j]==255){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

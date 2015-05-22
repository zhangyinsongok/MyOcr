package com.example.treecy.myocr.Impl.skew;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by TreecY on 2015/5/15.
 */
public class SkewByHough extends InitSuper implements PretreatmentMethod {

    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            super.init(bitmap);
            float angle = HoughTransform();
            System.out.println(angle);
            Pix pix = ReadFile.readBitmap(bitmap);
            pix = Rotate.rotate(pix,angle);
            return WriteFile.writeBitmap(pix);
    }

    private float HoughTransform(){

        int iRMax = (int)Math.sqrt(HEIGHT * HEIGHT + WIDTH * WIDTH) + 1;
        int iThMax = 361;
        int iMax = -1;
        int pRMax = -1;
        int pThMax = -1;
        float fRate = (float)(Math.PI/180);
        int[]  pArray = new int[iRMax * iThMax];

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if(greyArr[y *WIDTH + x]==0){
                    for(int iTh = 0; iTh < iThMax; iTh++)
                    {
                        int iR = (int)(x * Math.cos(iTh * fRate) + y * Math.sin(iTh * fRate));
                        if(iR > 0)
                        {
                            pArray[iR * iThMax + iTh]++;
                        }
                    }
                }
            }
        }
        for(int iR = 0; iR < iRMax; iR++)
        {
            for(int iTh = 0; iTh < iThMax; iTh++)
            {
                int iCount = pArray[iR * iThMax + iTh];
                if(iCount > iMax)
                {
                    iMax = iCount;
                    pRMax = iR;
                    pThMax = iTh;
                }
            }
        }
        return 90-pThMax;
    }

}

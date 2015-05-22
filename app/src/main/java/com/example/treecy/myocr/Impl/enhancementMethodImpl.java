package com.example.treecy.myocr.Impl;

import android.graphics.Bitmap;

import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.pretreatmentMethod;
import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by TreecY on 2015/5/5.
 */
public class enhancementMethodImpl implements pretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap,ConfigurationBean configurationBean) {
        if(configurationBean.isEnhancement()){
            Pix pix = ReadFile.readBitmap(bitmap);
            pix = AdaptiveMap.pixContrastNorm(pix, 25, 25, 60, 2, 2);
            return  WriteFile.writeBitmap(pix);
        }
        else {
            return bitmap;
        }
    }
}

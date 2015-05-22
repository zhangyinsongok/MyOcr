package com.example.treecy.myocr.Impl.binarize;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by TreecY on 2015/5/5.
 */
public class BinarizeOtsuAdpImpl implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            Pix pix = ReadFile.readBitmap(bitmap);
            long t1 = System.currentTimeMillis();
            pix = Binarize.otsuAdaptiveThreshold(pix);
            long t2 = System.currentTimeMillis();
            System.out.println("自适应大津法："+(t2-t1));
            return  WriteFile.writeBitmap(pix);
    }
}

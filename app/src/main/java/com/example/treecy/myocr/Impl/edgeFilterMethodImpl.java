package com.example.treecy.myocr.Impl;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Edge;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by TreecY on 2015/5/5.
 */
public class EdgeFilterMethodImpl implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            Pix pix = ReadFile.readBitmap(bitmap);
            long t1 = System.currentTimeMillis();
            pix = Edge.pixSobelEdgeFilter(pix, 2);
            long t2 = System.currentTimeMillis();
            System.out.println("边缘检测："+(t2-t1));
            return  WriteFile.writeBitmap(pix);
        }
}

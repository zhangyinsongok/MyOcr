package com.example.treecy.myocr.Impl.gray;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.InitSuper;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/8.
 */
public class GrayImpl extends InitSuper implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
        long T1 = System.currentTimeMillis();
        getInfo(bitmap);
        long T2 = System.currentTimeMillis();
        System.out.println("灰度化："+(T2-T1));
        return doGrey(bitmap);

    }
}

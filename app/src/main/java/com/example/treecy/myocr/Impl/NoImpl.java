package com.example.treecy.myocr.Impl;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/17.
 */
public class NoImpl implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
        return bitmap;
    }
}

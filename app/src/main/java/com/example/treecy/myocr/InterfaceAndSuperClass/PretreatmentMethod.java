package com.example.treecy.myocr.InterfaceAndSuperClass;

import android.graphics.Bitmap;

import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/5.
 */
public interface PretreatmentMethod {
    public Bitmap pretreat(Bitmap bitmap,ConfigurationBean configurationBean);
}

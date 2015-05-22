package com.example.treecy.myocr.InterfaceAndSuperClass;

import android.graphics.Bitmap;

import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/5.
 */
public abstract class PretreatmentBuilder {
    protected ConfigurationBean configurationBean = null;
    protected Bitmap bitmap = null;

    protected PretreatmentBuilder(Bitmap bitmap,ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
        this.bitmap = bitmap;
    }

    public Bitmap build(){
        return bitmap;
    }

    public abstract void doGray();

    public abstract void doEnhancement();

    public abstract void doBinarize();

    public abstract void doMorph();

    public abstract void doSkew();

}

package com.example.treecy.myocr;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentBuilder;

/**
 * Created by TreecY on 2015/5/5.
 */
public class PretreatmentDirector {
    PretreatmentBuilder pretreatmentBuilder = null;

    public PretreatmentDirector(PretreatmentBuilder pretreatmentBuilder) {
        this.pretreatmentBuilder = pretreatmentBuilder;
    }

    public Bitmap build(){
        pretreatmentBuilder.doGray();
        pretreatmentBuilder.doEnhancement();
        pretreatmentBuilder.doBinarize();
        pretreatmentBuilder.doMorph();
        pretreatmentBuilder.doSkew();
        return pretreatmentBuilder.build();
    }
}

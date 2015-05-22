package com.example.treecy.myocr.Impl;

import android.graphics.Bitmap;

import com.example.treecy.myocr.Impl.factory.BinarizeMethodFactory;
import com.example.treecy.myocr.Impl.factory.EnhanceMethodFactory;
import com.example.treecy.myocr.Impl.factory.GrayMethodFactory;
import com.example.treecy.myocr.Impl.factory.MorphMethodFactory;
import com.example.treecy.myocr.Impl.factory.SkewMethodFactory;
import com.example.treecy.myocr.Impl.gray.GrayImpl;
import com.example.treecy.myocr.Impl.skew.SkewByHough;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentBuilder;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/5.
 */
public class PretreatmentBuilderImpl extends PretreatmentBuilder {

    public PretreatmentBuilderImpl(Bitmap bitmap,ConfigurationBean configurationBean) {
        super(bitmap,configurationBean);
    }

    @Override
    public void doGray() {
        MethodFactory methodFactory = new GrayMethodFactory();
        PretreatmentMethod pretreatmentMethod = methodFactory.CreateMethod(configurationBean);
        bitmap = pretreatmentMethod.pretreat(bitmap,configurationBean);
    }

    @Override
    public void doEnhancement() {
        MethodFactory methodFactory = new EnhanceMethodFactory();
        PretreatmentMethod pretreatmentMethod = methodFactory.CreateMethod(configurationBean);
        bitmap =  pretreatmentMethod.pretreat(bitmap, configurationBean);
    }


    @Override
    public void doBinarize() {
        MethodFactory methodFactory = new BinarizeMethodFactory();
        PretreatmentMethod pretreatmentMethod = methodFactory.CreateMethod(configurationBean);
        bitmap = pretreatmentMethod.pretreat(bitmap,configurationBean);
    }

    @Override
    public void doSkew() {
        MethodFactory methodFactory = new SkewMethodFactory();
        PretreatmentMethod pretreatmentMethod = methodFactory.CreateMethod(configurationBean);
        bitmap = pretreatmentMethod.pretreat(bitmap,configurationBean);
    }

    @Override
    public void doMorph() {
        MethodFactory methodFactory = new MorphMethodFactory();
        PretreatmentMethod pretreatmentMethod = methodFactory.CreateMethod(configurationBean);
        bitmap = pretreatmentMethod.pretreat(bitmap,configurationBean);
    }

}

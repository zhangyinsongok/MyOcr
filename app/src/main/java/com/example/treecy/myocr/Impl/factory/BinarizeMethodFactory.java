package com.example.treecy.myocr.Impl.factory;

import com.example.treecy.myocr.Impl.binarize.BinarizeImprovedNiBlackImpl;
import com.example.treecy.myocr.Impl.binarize.BinarizeIterationImpl;
import com.example.treecy.myocr.Impl.binarize.BinarizeNiBlackImpl;
import com.example.treecy.myocr.Impl.binarize.BinarizeOtsuAdpImpl;
import com.example.treecy.myocr.Impl.binarize.BinarizeOtsuImpl;
import com.example.treecy.myocr.Impl.binarize.BinarizeSauvolaImpl;
import com.example.treecy.myocr.Impl.NoImpl;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.constants;

/**
 * Created by TreecY on 2015/5/5.
 */
public class BinarizeMethodFactory implements MethodFactory{
    PretreatmentMethod pretreatmentMethod = null;
    @Override
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean) {
        switch (configurationBean.getBinarizeType()){
            case constants.NO:pretreatmentMethod = new NoImpl();break;
            case constants.OTSUADAPTIVE:pretreatmentMethod = new BinarizeOtsuAdpImpl();break;
            case constants.SAUVLOLA:pretreatmentMethod = new BinarizeSauvolaImpl();break;
            case constants.IMPROVEDNiBlack:pretreatmentMethod = new BinarizeImprovedNiBlackImpl();break;
            case constants.ITERATION:pretreatmentMethod = new BinarizeIterationImpl();break;
            case constants.OTSU:pretreatmentMethod = new BinarizeOtsuImpl();break;
            case constants.NIBLACK:pretreatmentMethod = new BinarizeNiBlackImpl();break;
        }
        return pretreatmentMethod;
    }
}

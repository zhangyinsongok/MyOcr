package com.example.treecy.myocr.Impl.factory;

import com.example.treecy.myocr.Impl.enhance.EnhancementMethodImpl;
import com.example.treecy.myocr.Impl.enhance.GreyStretchingImpl;
import com.example.treecy.myocr.Impl.NoImpl;
import com.example.treecy.myocr.Impl.enhance.SharpImpl;
import com.example.treecy.myocr.Impl.enhance.SmoothImpl;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.constants;

/**
 * Created by TreecY on 2015/5/5.
 */
public class EnhanceMethodFactory implements MethodFactory {
    @Override
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean) {
        PretreatmentMethod pretreatmentMethod = null;
        switch (configurationBean.getEnhanceType()){
            case constants.NO:pretreatmentMethod = new NoImpl();break;
            case constants.ENHANCEMENT: pretreatmentMethod = new EnhancementMethodImpl();break;
            case constants.GREYSTRECHING:pretreatmentMethod = new GreyStretchingImpl();break;
            case constants.SMOOTH:pretreatmentMethod = new SmoothImpl();break;
            case constants.SHARP:pretreatmentMethod = new SharpImpl();break;
        }
        return  pretreatmentMethod;
    }
}

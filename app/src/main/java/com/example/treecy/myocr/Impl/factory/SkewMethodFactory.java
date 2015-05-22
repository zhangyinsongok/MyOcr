package com.example.treecy.myocr.Impl.factory;

import com.example.treecy.myocr.Impl.NoImpl;
import com.example.treecy.myocr.Impl.skew.SkewByHough;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.constants;

/**
 * Created by TreecY on 2015/5/17.
 */
public class SkewMethodFactory implements MethodFactory {
    @Override
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean) {
        PretreatmentMethod pretreatmentMethod = null;
        switch (configurationBean.getSkewType()){
            case constants.NO:pretreatmentMethod = new NoImpl();break;
            case constants.HOUGH:pretreatmentMethod = new SkewByHough();break;
        }
        return pretreatmentMethod;
    }
}

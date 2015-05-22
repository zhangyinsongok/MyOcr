package com.example.treecy.myocr.Impl.factory;

import com.example.treecy.myocr.Impl.NoImpl;
import com.example.treecy.myocr.Impl.gray.GrayImpl;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.constants;

/**
 * Created by TreecY on 2015/5/17.
 */
public class GrayMethodFactory implements MethodFactory {
    PretreatmentMethod pretreatment = null;
    @Override
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean) {
        switch (configurationBean.getGrayType()){
            case constants.NO:pretreatment = new NoImpl();break;
            case constants.GRAY:pretreatment = new GrayImpl();break;
        }
        return pretreatment;
    }
}

package com.example.treecy.myocr.Impl.factory;

import com.example.treecy.myocr.Impl.NoImpl;
import com.example.treecy.myocr.Impl.morph.MorphExpendImpl;
import com.example.treecy.myocr.Impl.morph.MorphRustImpl;
import com.example.treecy.myocr.InterfaceAndSuperClass.MethodFactory;
import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.constants;

/**
 * Created by TreecY on 2015/5/16.
 */
public class MorphMethodFactory implements MethodFactory {
    @Override
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean) {
        PretreatmentMethod pretreatmentMethod = null;
        switch (configurationBean.getMorphType()){
            case constants.NO:pretreatmentMethod = new NoImpl();break;
            case constants.RUST:pretreatmentMethod = new MorphRustImpl();break;
            case constants.EXPEND:pretreatmentMethod = new MorphExpendImpl();break;
        }
        return pretreatmentMethod;
    }
}

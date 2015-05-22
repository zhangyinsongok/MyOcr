package com.example.treecy.myocr.InterfaceAndSuperClass;

import com.example.treecy.myocr.beans.ConfigurationBean;

/**
 * Created by TreecY on 2015/5/5.
 */
public interface MethodFactory {
    public PretreatmentMethod CreateMethod(ConfigurationBean configurationBean);
}

package com.example.treecy.myocr.beans;

/**
 * Created by TreecY on 2015/5/5.
 */
public class ConfigurationBean {

    private int grayType;

    private int binarizeType;

    private int enhanceType;

    private int morphType;

    private int skewType;

    private SauvolaSettingBean sauvolaSettingBean;

    public ConfigurationBean(int grayType,int binarizeType,int enhanceType,int morphType,int skewType,SauvolaSettingBean sauvolaSettingBean){
        this.grayType = grayType;
        this.binarizeType = binarizeType;
        this.enhanceType = enhanceType;
        this.morphType = morphType;
        this.skewType = skewType;
        this.sauvolaSettingBean = sauvolaSettingBean;
    }

    public int getGrayType() {
        return grayType;
    }

    public void setGrayType(int grayType) {
        this.grayType = grayType;
    }

    public int getBinarizeType() {
        return binarizeType;
    }

    public void setBinarizeType(int binarizeType) {
        this.binarizeType = binarizeType;
    }

    public int getEnhanceType() {
        return enhanceType;
    }

    public void setEnhanceType(int enhanceType) {
        this.enhanceType = enhanceType;
    }

    public int getMorphType() {
        return morphType;
    }

    public void setMorphType(int morphType) {
        this.morphType = morphType;
    }

    public int getSkewType() {
        return skewType;
    }

    public void setSkewType(int skewType) {
        this.skewType = skewType;
    }

    public SauvolaSettingBean getSauvolaSettingBean() {
        return sauvolaSettingBean;
    }

    public void setSauvolaSettingBean(SauvolaSettingBean sauvolaSettingBean) {
        this.sauvolaSettingBean = sauvolaSettingBean;
    }
}

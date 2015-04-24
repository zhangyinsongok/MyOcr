package com.example.treecy.myocr.beans;

/**
 * Created by TreecY on 2015/4/21.
 */
public class SauvolaSettingBean {

    private int whsize;

    private float factor;

    private int nx;

    private int ny;

    public SauvolaSettingBean(int whsize,float factor,int nx,int ny){
        this.whsize = whsize;
        this.factor = factor;
        this.nx = nx;
        this.ny = ny;
    }

    public int getWhsize() {
        return whsize;
    }

    public void setWhsize(int whsize) {
        this.whsize = whsize;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

}

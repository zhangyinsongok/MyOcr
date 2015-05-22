package com.example.treecy.myocr.Impl.binarize;

import android.graphics.Bitmap;

import com.example.treecy.myocr.InterfaceAndSuperClass.PretreatmentMethod;
import com.example.treecy.myocr.beans.ConfigurationBean;
import com.example.treecy.myocr.beans.SauvolaSettingBean;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

/**
 * Created by TreecY on 2015/5/5.
 */
public class BinarizeSauvolaImpl implements PretreatmentMethod {
    @Override
    public Bitmap pretreat(Bitmap bitmap, ConfigurationBean configurationBean) {
            SauvolaSettingBean sauvolaSettingBean = configurationBean.getSauvolaSettingBean();
            Pix pix = ReadFile.readBitmap(bitmap);
            long t1=System.currentTimeMillis();
            pix = Binarize.sauvolaBinarizeTiled(pix, sauvolaSettingBean.getWhsize(), sauvolaSettingBean.getFactor(), sauvolaSettingBean.getNx(), sauvolaSettingBean.getNy());
            long t2=System.currentTimeMillis();
            System.out.println("Sauvola运行时间"+(t2-t1));
            return  WriteFile.writeBitmap(pix);
        }
}

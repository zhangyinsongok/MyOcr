package com.example.treecy.myocr;

        import android.graphics.Bitmap;
        import android.graphics.Color;

        import com.example.treecy.myocr.beans.SauvolaSettingBean;
        import com.googlecode.leptonica.android.AdaptiveMap;
        import com.googlecode.leptonica.android.Binarize;
        import com.googlecode.leptonica.android.Convert;
        import com.googlecode.leptonica.android.Edge;
        import com.googlecode.leptonica.android.Enhance;
        import com.googlecode.leptonica.android.GrayQuant;
        import com.googlecode.leptonica.android.MorphApp;
        import com.googlecode.leptonica.android.Pix;
        import com.googlecode.leptonica.android.ReadFile;
        import com.googlecode.leptonica.android.Rotate;
        import com.googlecode.leptonica.android.Skew;
        import com.googlecode.leptonica.android.WriteFile;

        import java.io.File;

/**
 * Created by TreecY on 2015/3/22.
 */
public class Pretreatment {

    private static int HEIGHT;
    private static int WIDTH;
    private static int[] pixels;
    private static int[] greyArr;
    private static boolean[] afterNiBlack;
    private static int MinGreyValue;
    private static int MaxGreyValue;
    private static int[] greyHistogram = new int[256];

    public static Bitmap doPretreatment(Bitmap bitmap,boolean[] pretreatChoice,int binarizeType,SauvolaSettingBean sauvolaSettingBean){
        Pix pix = ReadFile.readBitmap(bitmap);
     //   pix = Convert.convertTo8(pix);
        if(pretreatChoice[0])
        pix = AdaptiveMap.pixContrastNorm(pix,25,25,60,2,2);
        if(pretreatChoice[1])
            pix = Edge.pixSobelEdgeFilter(pix, 2);
        if(pretreatChoice[2])
        switch (binarizeType) {
            case constants.SAUVLOLA:
                pix = Binarize.sauvolaBinarizeTiled(pix, sauvolaSettingBean.getWhsize(), sauvolaSettingBean.getFactor(), sauvolaSettingBean.getNx(), sauvolaSettingBean.getNy());
                break;
            case constants.OTSUADAPTIVE:
                pix = Binarize.otsuAdaptiveThreshold(pix);
                break;
            case constants.IMPROVEDNiBlack:
                getInfo(bitmap);
                doGrey(bitmap);
                getMinAndMaxGreyValue(greyHistogram);
                int T = getThresholdByIteration(MinGreyValue, MaxGreyValue, greyArr, 0.2);
                bitmap = improvedNiBlack(T);
                pix = ReadFile.readBitmap(bitmap);
                break;
            case constants.ITERATION:
                bitmap = WriteFile.writeBitmap(pix);
                getInfo(bitmap);
                doGrey(bitmap);
                getMinAndMaxGreyValue(greyHistogram);
                T = getThresholdByIteration(MinGreyValue, MaxGreyValue, greyArr, 0.2);
                bitmap = doBinarization(T);
                pix = ReadFile.readBitmap(bitmap);
                break;
        }

 //      pix = AdaptiveMap.backgroundNormMorph(pix);
        return WriteFile.writeBitmap(pix);
    }

    private static void getInfo(Bitmap bitmap){                         //获得图片的基本信息
        HEIGHT = bitmap.getHeight();
        WIDTH = bitmap.getWidth();
        pixels = new int[HEIGHT * WIDTH];
        greyArr= new int[HEIGHT * WIDTH];
        afterNiBlack = new boolean[HEIGHT * WIDTH];
        bitmap.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
    }

    private static Bitmap doGrey(Bitmap bitmap){                           //进行灰度化
        for(int i = 0;i < HEIGHT;i++)
            for(int j = 0;j < WIDTH;j++){
                int color =  pixels[i * WIDTH + j];

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                int grey =( red * 77 + green * 150 + blue * 29 ) >> 8;          //避免浮点数运算
                pixels[i * WIDTH + j] = Color.rgb(grey,grey,grey);              //像素矩阵用来生成图片
                greyArr[i * WIDTH + j] = grey;                                   //灰度矩阵用来进行二值化
                greyHistogram[grey]++;                                          //灰度直方图
            }
        Bitmap result = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,WIDTH,0,0,WIDTH,HEIGHT);
        return result;
    }

    private static int getThresholdByOtsu(int[] greyArr,int MinGreyValue,int MaxGreyValue){                   //大津法求阈值
        int T=0;
        double AvgB=0,AvgF=0,AvgAll=0,MaxD=0;
        for(int i=MinGreyValue;i<MaxGreyValue;i++){
            double numF=0,numB=0,SumF=0,SumB=0;
                for(int grey:greyArr){
                    if(grey>i){
                        numB++;
                        SumB+=grey;
                    }
                    else{
                        numF++;
                        SumF+=grey;
                    }
                }
            AvgB = SumB/numB;
            AvgF = SumF/numF;
            AvgAll = (SumB+SumF)/(numB+numF);
            double D =(numB/(numB+numF))*(AvgB-AvgAll)*(AvgB-AvgAll) + (numF/(numB+numF))*(AvgF-AvgAll)*(AvgF-AvgAll);
            if(D>MaxD){
                MaxD = D;
                T = i;
            }
        }
        return T;
    }

    private static int getThresholdByIteration(int MinGreyValue,int MaxGreyValue,int[] greyArr,double Accuracy) {             //迭代法求阈值
        double T;
        double temp = (MinGreyValue + MaxGreyValue) / 2;
        do {
            T = temp;
            double numF=0,numB=0,SumF=0,SumB=0,AvgB=0,AvgF=0;
                for (int grey:greyArr){
                    if (grey < T ) {
                        SumB += grey;
                        numB++;
                    }
                    if (grey > T) {
                        SumF += grey;
                        numF++;
                    }
            }
            AvgB = SumB / numB;
            AvgF = SumF / numF;
            temp = (AvgB + AvgF) / 2;
        } while (Math.abs(T-temp)>Accuracy);
        return (int)T;
    }


    private static Bitmap doBinarizaionByColumnIteration(){
        int[] column = new int[HEIGHT];
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++)
                column[j] = greyArr[j * WIDTH + i];
            getMinAndMaxGreyValueForColumn(column);
            int T = getThresholdByIteration(MinGreyValue,MaxGreyValue,column,0.5)-10;
            for(int j=0;j<HEIGHT;j++) {
                if (greyArr[j * WIDTH + i] > T) {
                    pixels[j * WIDTH + i] = Color.rgb(255, 255, 255);
                } else
                    pixels[j * WIDTH + i] = Color.rgb(0, 0, 0);
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private static Bitmap Niblack(){
        int avg = 0;
        double deviation = 0;
        double T;
        for(int i=0;i<HEIGHT;i++){
            for(int j=0;j<WIDTH;j++){
                avg = getAverageGreyByNiBlack(i,j);
                deviation = getDeviationByNiBlack(avg,i,j);
                T = avg - 0.1*deviation;
                if(greyArr[i * WIDTH + j]>=T) {
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                    afterNiBlack[i * WIDTH + j] = false;
                }
                else {
                    pixels[i * WIDTH + j] = Color.rgb(0, 0, 0);
                    afterNiBlack[i * WIDTH + j] = true;
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }


    private static int getAverageGreyByNiBlack(int row,int col){
           double sum = 0;
           double num = 0;
           for(int i=row-7;i<=row+7;i++) {
               for (int j = col - 7; j <= col + 7; j++) {
                        if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                            sum += greyArr[i * WIDTH + j];
                            num++;
                        }
               }
           }
        return (int)(sum/num);
    }

    private static int getDeviationByNiBlack(int avg,int row,int col){
        int sum = 0;
        double num = 0;
        for(int i=row-7;i<=row+7;i++) {
            for (int j = col - 7; j <= col + 7; j++) {
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                    sum += Math.pow((greyArr[i * WIDTH + j]-avg),2);
                    num++;
                }
            }
        }
        return (int)Math.sqrt(sum/num);
    }

  /*  private static Bitmap corrosion(boolean[] afterNiBlack) {
        for(int i=0;i<HEIGHT;i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(doCorrosion(i,j)){
                    afterNiBlack[i * WIDTH + j] = true;
                    pixels[i * WIDTH + j] = Color.rgb(0, 0, 0);
                }
                else {
                    afterNiBlack[i * WIDTH + j] = false;
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }*/

    private static Bitmap corrosion(Bitmap bitmap) {
        bitmap.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        for(int i=0;i<HEIGHT;i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(doCorrosion(i,j)){
                    pixels[i * WIDTH + j] = Color.rgb(0, 0, 0);
                }
                else {
                    pixels[i * WIDTH + j] = Color.rgb(255, 255, 255);
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }


   /* private static boolean doCorrosion(int row,int col){
            int i = row;
            for (int j = col - 1; j <= col + 1; j++) {
                if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                        if(!afterNiBlack[i * WIDTH + j])
                            return  false;
                    }
               }
            int j = col;
            for (i = row - 1; i <= row + 1; i++) {
            if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                if(!afterNiBlack[i * WIDTH + j])
                    return  false;
            }
        }
        return  true;
    }*/

    private static boolean doCorrosion(int row,int col){
        int i = row;
        for (int j = col - 1; j <= col + 1; j++) {
            if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                int color =  pixels[i * WIDTH + j];
                if(color == Color.rgb(255, 255, 255))
                    return false;
            }
        }
        int j = col;
        for (i = row - 1; i <= row + 1; i++) {
            if(i>0&&i<HEIGHT&&j>0&&j<WIDTH) {
                int color =  pixels[i * WIDTH + j];
                int red = Color.red(color);
                if(color == Color.rgb(255, 255, 255))
                    return false;
            }
        }
        return  true;
    }

    private static Bitmap doBinarization(int T) {                          //通过阈值T进行二值化
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(greyArr[i * WIDTH + j]>T){
                    pixels[i * WIDTH + j] = Color.rgb(255,255,255);
                }
                else
                    pixels[i * WIDTH + j] = Color.rgb(0,0,0);
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private static void getMinAndMaxGreyValue(int[] greyHistogram) {                          //获取一张图片中的最大灰度值和最小灰度值
        int i = 0;
        while(greyHistogram[i]==0)
            i++;
        MinGreyValue = i;
        i=255;
        while(greyHistogram[i]==0)
            i--;
        MaxGreyValue = i;
    }

    private static void getMinAndMaxGreyValueForColumn(int[] column) {                          //获取一张图片中的最大灰度值和最小灰度值
        int min = 255,max = 0;
        for (int i = 0; i < HEIGHT; i++) {
                if(column[i]<min){
                    min = column[i];
                }
                if(column[i]>max){
                    max = column[i];
                }
        }
        MinGreyValue = min;
        MaxGreyValue = max;
    }

    private static Bitmap greyStretching(){
        int sum = 0;
        int T = MaxGreyValue;
        while(sum < WIDTH * HEIGHT *0.1){
            sum += greyHistogram[T];
            T--;
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(greyArr[i * WIDTH + j] > T) {
                    greyArr[i * WIDTH + j] = 255;
                    pixels[i * WIDTH + j] = Color.rgb(255,255,255);
                }
                else {
                    int num = 0;double temp = 0;
                    for(int k = i-1 ;k <= i+1;k++){
                        if(k>=0&&k<HEIGHT){
                            temp += greyArr[k * WIDTH + j];
                            num++;
                        }
                    }
                    double avg = temp/num;
                    if(avg >= T)
                        greyArr[i * WIDTH + j] = 255;
                    else
                        greyArr[i * WIDTH + j] = (int) (((avg - MinGreyValue) / (T - MinGreyValue)) * 255);
                    pixels[i * WIDTH + j]  = Color.rgb(greyArr[i * WIDTH + j],greyArr[i * WIDTH + j],greyArr[i * WIDTH + j]);
                }
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private static Bitmap improvedNiBlack(int Threshold){
        int T = 0;
        for(int j= 0 ;j< WIDTH ;j++){
            T = (getRowThresholdByNiBlack(j)+ Threshold)/2;
            for(int i = 0 ; i < HEIGHT ; i++){
                if(greyArr[i * WIDTH + j] > T )
                    pixels[i * WIDTH + j] = Color.rgb(255,255,255);
                else
                    pixels[i * WIDTH + j] = Color.rgb(0,0,0);
            }
        }
        Bitmap result = Bitmap .createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }

    private static int getRowThresholdByNiBlack(int colNum){
        int sum = 0;
        double avg;
        double deviation = 0;
        double T;
        for(int i=0;i<HEIGHT;i++)
                sum += greyArr[i * WIDTH + colNum];
        avg = sum/WIDTH;
        sum = 0;
        for(int i=0;i<HEIGHT;i++){
            sum += Math.pow(greyArr[i * WIDTH + colNum] - avg,2);
        }
        deviation = Math.sqrt(sum/WIDTH);
        T = avg +0.08 *deviation;
        return (int)T;
    }
}

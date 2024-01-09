package com.aaron.jboxprueba;

import android.graphics.Paint;
import android.util.Log;

public class  Utils {

    public static Paint paint(int color){
        Paint p=new Paint();
        p.setColor(color);
        return p;
    }

    public static Paint paint(int color, int textSize){
        Paint p=paint(color);
        p.setTextSize(textSize);
        return p;
    }

    public static Paint paint(int color, Paint.Style style, int ancho){
        Paint p=paint(color);
        p.setStyle(style);
        p.setStrokeWidth(ancho);
        return p;
    }

    public static Paint painth(int color, int textSize, int ancho){
        Paint p=paint(color,textSize);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(ancho);
        return p;
    }

    public static int getRamdom(int limiteInferior, int limiteSuperior){
        return (int) ((Math.random() * (limiteSuperior - limiteInferior)) + limiteInferior);
    }


    public static float getRamdom(float limiteInferior, float limiteSuperior){
        return (float) ((Math.random() * (limiteSuperior - limiteInferior)) + limiteInferior);
    }


    public static float getFuerza(float fuerza){
        int limite=getRamdom(1,3);
        return (float) (limite==1?fuerza:fuerza*-1);
    }



}

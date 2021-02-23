package com.john.waveview;


import android.graphics.drawable.Drawable;

/**
 * Data class to store solid attributes*/
public class SolidAttributes {

    private int mWaveColor;
    private float mWaveAlpha;
    private Drawable mBackgroundDrawable;

    SolidAttributes(int waveColor, float waveAlpha, Drawable drawable){
        mWaveColor = waveColor;
        mWaveAlpha = waveAlpha;
        mBackgroundDrawable = drawable;
    }

    public int getWaveColor() { return mWaveColor; }

    public float getWaveAlpha() { return mWaveAlpha; }

    public Drawable getBackgroundDrawable() { return mBackgroundDrawable; }
}

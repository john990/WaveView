package com.john.waveview;

/**
 * Data class to store wave attributes*/
public class WaveAttributes {

    private int mAboveWaveColor;
    private float mAboveWaveAlpha;
    private int mBlowWaveColor;
    private float mBlowWaveAlpha;
    private int mWaveLength;
    private int mWaveHeight;
    private int mWaveHz;

    WaveAttributes(
            int aboveWaveColor,
            float aboveWaveAlpha,
            int blowWaveColor,
            float blowWaveAlpha,
            int waveLength,
            int waveHz,
            int waveHeight){
        mAboveWaveAlpha = aboveWaveAlpha;
        mAboveWaveColor = aboveWaveColor;
        mBlowWaveAlpha = blowWaveAlpha;
        mBlowWaveColor = blowWaveColor;
        mWaveLength = waveLength;
        mWaveHz = waveHz;
        mWaveHeight = waveHeight;
    }

    public int getAboveWaveColor() { return mAboveWaveColor; }

    public int getBlowWaveColor() { return mBlowWaveColor; }

    public float getBlowWaveAlpha() { return mBlowWaveAlpha; }

    public float getAboveWaveAlpha() { return mAboveWaveAlpha; }

    public int getWaveLength() { return mWaveLength; }

    public int getWaveHeight() { return mWaveHeight; }

    public int getWaveHz() { return mWaveHz; }

}

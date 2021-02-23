package com.john.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by John on 2014/10/15.
 */
public class WaveView extends LinearLayout {
    protected static final int LARGE = 1;
    protected static final int MIDDLE = 2;
    protected static final int LITTLE = 3;

    private int mProgress;
    private Wave mWave;
    private Solid mSolid;
    private int mWaveToTop;

    public final int DEFAULT_COLOR = Color.WHITE;
    public final int DEFAULT_PROGRESS = 80;
    public final float DEFAULT_ALPHA = 0.65f;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        // Load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, R.attr.waveViewStyle, 0);

        mProgress = attributes.getInt(R.styleable.WaveView_progress, DEFAULT_PROGRESS);

        SolidAttributes solidAttributes = getSolidAttributes(attributes);
        WaveAttributes waveAttributes = getWaveAttributes(attributes,solidAttributes);
        attributes.recycle();

        // Not passing attrs and retrieving attrs there as cannot access attrs from this parent view 's attrs - see https://stackoverflow.com/a/50865837/11200630
        mWave = new Wave(context, waveAttributes);
        mSolid = new Solid(context, solidAttributes);

        addView(mWave);
        addView(mSolid);

        setProgress(mProgress);
    }

    private SolidAttributes getSolidAttributes(TypedArray attributes) {
        int waveColor = attributes.getColor(R.styleable.WaveView_wave_color,DEFAULT_COLOR);
        float waveAlpha = attributes.getFloat(R.styleable.WaveView_wave_alpha,DEFAULT_ALPHA);
        Drawable drawable = attributes.getDrawable(R.styleable.WaveView_background);
        return new SolidAttributes(
                waveColor,
                waveAlpha,
                drawable);
    }

    private WaveAttributes getWaveAttributes(TypedArray attributes, SolidAttributes solidAttributes) {
        int aboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, solidAttributes.getWaveColor());
        int blowWaveColor  = attributes.getColor(R.styleable.WaveView_blow_wave_color, solidAttributes.getWaveColor());
        float aboveWaveAlpha = attributes.getFloat(R.styleable.WaveView_above_wave_color_alpha,DEFAULT_ALPHA);
        float blowWaveAlpha = attributes.getFloat(R.styleable.WaveView_blow_wave_color_alpha, DEFAULT_ALPHA);
        int waveLength = attributes.getInt(R.styleable.WaveView_wave_length, LARGE);
        int waveHz = attributes.getInt(R.styleable.WaveView_wave_hz, MIDDLE);
        int waveHeight = attributes.getInt(R.styleable.WaveView_wave_height, MIDDLE);
        return new WaveAttributes(
                aboveWaveColor,
                aboveWaveAlpha,
                blowWaveColor,
                blowWaveAlpha,
                waveLength,
                waveHeight,
                waveHz);
    }

    public int getProgress() { return mProgress; }

    public void setWaveAlpha(float alpha) { mSolid.setWaveAlpha(alpha); }

    public float getWaveAlpha(){ return mSolid.getWaveAlpha();}

    public void setAboveWaveColor(int aboveWaveColor) { mWave.setAboveWaveColor(aboveWaveColor);}

    private int getAboveWaveColor(){ return mWave.getAboveWaveColor();}

    public void setAboveWaveColorAlpha(float aboveWaveColorAlpha) { mWave.setAboveWaveAlpha(aboveWaveColorAlpha);}

    public float getAboveWaveColorAlpha() { return mWave.getAboveColorAlpha(); }

    public void setWaveColor(int waveColor) { mSolid.setWaveColor(waveColor); }

    public int getWaveColor(){ return mSolid.getWaveColor();}

    public void setBlowWaveColor(int blowWaveColor) { mWave.setBlowWaveColor(blowWaveColor); }

    public int getBlowWaveColor(){ return mWave.getBlowWaveColor();}

    public void setBlowWaveColorAlpha(float alpha) { mSolid.setWaveAlpha(alpha); }

    public float getBlowWaveColorAlpha() { return mWave.getBlowWaveColorAlpha(); }

    public void setWaveHz(int waveHz) { mWave.setBlowWaveColor(waveHz); }

    public float getWaveHz() { return mWave.getWaveHz(); }

    public Drawable getWaveBackground(){ return mSolid.getBackground();}

    public void setWaveBackgroundDrawable(Drawable drawable){  mSolid.setBackgroundDrawable(drawable);}

    private void computeWaveToTop() {
        mWaveToTop = (int) (getHeight() * (1f - mProgress / 100f));
        ViewGroup.LayoutParams params = mWave.getLayoutParams();
        if (params != null) {
            ((LayoutParams) params).topMargin = mWaveToTop;
        }
        mWave.setLayoutParams(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            computeWaveToTop();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = mProgress;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(ss.progress);
    }

    public void setProgress(int progress) {
        this.mProgress = progress > 100 ? 100 : progress;
        computeWaveToTop();
    }

    public static int alphaPercentToInt(float alphaPercent){
        if(alphaPercent>1) return 255;
        else if(alphaPercent<0) return 0;
        return (int) (alphaPercent*255);
    }

    private static class SavedState extends BaseSavedState {
        int progress;

        /**
         * Constructor called from {@link android.widget.ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

package com.john.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by John on 2014/10/15.
 */
public class WaveView extends LinearLayout {
    private int aboveWaveColor;
    private int blowWaveColor;
    private int progress;

    private int waveToTop;

    private Wave wave;
    private Solid solid;

    private final int default_above_wave_alpha = 50;
    private final int default_blow_wave_alpha = 30;
    private final int default_above_wave_color = Color.WHITE;
    private final int default_blow_wave_color = Color.WHITE;
    private final int default_progress = 80;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, R.attr.waveViewStyle, 0);

        aboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, default_above_wave_color);
        blowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, default_blow_wave_color);
        progress = attributes.getInt(R.styleable.WaveView_progress, default_progress);

        wave = new Wave(context, null);
        wave.setAboveWaveColor(aboveWaveColor);
        wave.setBlowWaveColor(blowWaveColor);
        wave.initializePainters();

        solid = new Solid(context, null);
        solid.setAboveWavePaint(wave.getAboveWavePaint());
        solid.setBlowWavePaint(wave.getBlowWavePaint());

        addView(wave);
        addView(solid);

        setProgress(progress);
    }

    public void setProgress(int progress) {
        this.progress = progress > 100 ? 100 : progress;
        computeWaveToTop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            computeWaveToTop();
        }
    }

    private void computeWaveToTop() {
        waveToTop = (int) (getHeight() * (1f - progress / 100f));
        ViewGroup.LayoutParams params = wave.getLayoutParams();
        if (params != null) {
            ((LayoutParams) params).topMargin = waveToTop;
        }
        wave.setLayoutParams(params);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.progress = progress;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setProgress(ss.progress);
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

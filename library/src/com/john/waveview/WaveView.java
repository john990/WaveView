package com.john.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by kai.wang on 6/17/14.
 */
public class WaveView extends View {

    private Path aboveWavePath = new Path();
    private Path blowWavePath = new Path();

    private Paint aboveWavePaint = new Paint();
    private Paint blowWavePaint = new Paint();

    private final int default_above_wave_alpha = 50;
    private final int default_blow_wave_alpha = 30;
    private final int default_above_wave_color = Color.WHITE;
    private final int default_blow_wave_color = Color.WHITE;
    private final int default_progress = 80;

    private int waveToTop;
    private int aboveWaveColor;
    private int blowWaveColor;
    private int progress;

    private int offsetIndex = 0;

    /** wave length */
    private final int x_zoom = 150;

    /** wave crest */
    private final int y_zoom = 6;
    /** offset of X */
    private final float offset = 0.5f;
    private final float max_right = x_zoom * offset;

    // wave animation
    private float aboveOffset = 0.0f;
    private float blowOffset = 4.0f;
    /** offset of Y */
    private float animOffset = 0.15f;

    // refresh thread
    private RefreshProgressRunnable mRefreshProgressRunnable;

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, defStyle, 0);

        aboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, default_above_wave_color);
        blowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, default_blow_wave_color);
        progress = attributes.getInt(R.styleable.WaveView_progress, default_progress);

        setProgress(progress);

        initializePainters();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(blowWavePath, blowWavePaint);
        canvas.drawPath(aboveWavePath, aboveWavePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    private void initializePainters() {
        aboveWavePaint.setColor(aboveWaveColor);
        aboveWavePaint.setAlpha(default_above_wave_alpha);
        aboveWavePaint.setStyle(Paint.Style.FILL);
        aboveWavePaint.setAntiAlias(true);

        blowWavePaint.setColor(blowWaveColor);
        blowWavePaint.setAlpha(default_blow_wave_alpha);
        blowWavePaint.setStyle(Paint.Style.FILL);
        blowWavePaint.setAntiAlias(true);
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        aboveWavePath.reset();
        blowWavePath.reset();

        getWaveOffset();

        aboveWavePath.moveTo(getLeft(), getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            aboveWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + aboveOffset)) + waveToTop);
        }
        aboveWavePath.lineTo(getRight(), getHeight());

        blowWavePath.moveTo(getLeft(), getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            blowWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + blowOffset)) + waveToTop);
        }
        blowWavePath.lineTo(getRight(), getHeight());
    }

    public void setProgress(int progress) {
        this.progress = progress > 100 ? 100 : progress;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshProgressRunnable = new RefreshProgressRunnable();
        getHandler().post(mRefreshProgressRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHandler().removeCallbacks(mRefreshProgressRunnable);
    }

    private void getWaveOffset(){
        if(blowOffset > Float.MAX_VALUE - 100){
            blowOffset = 0;
        }else{
            blowOffset += animOffset;
        }

        if(aboveOffset > Float.MAX_VALUE - 100){
            aboveOffset = 0;
        }else{
            aboveOffset += animOffset;
        }
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

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (WaveView.this) {
                waveToTop = (int) (getHeight() * (1f - progress / 100f));

                calculatePath();

                invalidate();

                getHandler().postDelayed(this,16);
            }
        }
    }


    private static class SavedState extends BaseSavedState {
        int progress;

        /**
         * Constructor called from {@link ProgressBar#onSaveInstanceState()}
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

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}

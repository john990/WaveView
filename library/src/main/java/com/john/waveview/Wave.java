package com.john.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

// y=Asin(ωx+φ)+k
class Wave extends View {

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    public final int default_above_wave_alpha = 50;
    public final int default_blow_wave_alpha = 30;

    private int mAboveWaveColor;
    private int mBlowWaveColor;

    /**
     * wave length
     */
    private int mXZoom;

    /**
     * wave height
     */
    private final int mYZoom = 8;
    /**
     * offset of X
     */
    private final float mXOffset = 20f;
    private float mMaxRight;

    // wave animation
    private float mAboveOffset = 0.0f;
    private float mBlowOffset = mYZoom/2;

    /**
     * offset of Y
     */
    private float mAnimOffset = 0.06f;

    /**
     * refresh thread
     */
    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, top, right, bottom;

    private double PI2 = 2 * Math.PI;

    // ω
    private double omega;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mYZoom * 2);
        setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBlowWavePath, mBlowWavePaint);
        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
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

    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(default_above_wave_alpha);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(default_blow_wave_alpha);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += mXOffset) {
            y = (float) (mYZoom * Math.sin(omega * x + mAboveOffset) + mYZoom);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += mXOffset) {
            y = (float) (mYZoom * Math.sin(omega * x + mBlowOffset) + mYZoom);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mXZoom == 0) {
                startWave();
            }
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mXZoom = width + width/2;
            left = getLeft();
            top = getTop();
            right = getRight();
            bottom = getBottom();
            mMaxRight = right + mXOffset;
            omega = PI2 / mXZoom;
        }
        mRefreshProgressRunnable = new RefreshProgressRunnable();
        post(mRefreshProgressRunnable);
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mAnimOffset;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mAnimOffset;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - System.currentTimeMillis() - start;
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}

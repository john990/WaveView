package com.john.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static com.john.waveview.WaveView.LARGE;
import static com.john.waveview.WaveView.MIDDLE;
import static com.john.waveview.WaveView.alphaPercentToInt;

// y=Asin(ωx+φ)+k
class Wave extends View {
    private final int WAVE_HEIGHT_LARGE = 16;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;

    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final float WAVE_HZ_FAST = 0.13f;
    private final float WAVE_HZ_NORMAL = 0.09f;
    private final float WAVE_HZ_SLOW = 0.05f;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    private float mWaveLength;
    private float mMaxRight;
    private float mWaveHz;
    private int mWaveHeight;
    private float mWaveMultiple;

    // wave animation
    private float mAboveOffset = 0.0f;
    private float mBlowOffset;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    private int mAboveWaveColor;
    private int mBlowWaveColor;
    private float mBlowWaveAlpha;
    private float mAboveWaveAlpha;


    public Wave(Context context, WaveAttributes waveAttributes) {
        this(context, null, R.attr.waveViewStyle, waveAttributes);
    }

    /*For Android Studio Tools*/
    private Wave(Context context){
        super(context); }

    private Wave(Context context, AttributeSet attrs, int defStyle, WaveAttributes waveAttributes) {
        super(context, attrs, defStyle);
        initializeWave(waveAttributes);
    }

    public int getAboveWaveColor() { return mAboveWaveColor; }

    public void setAboveWaveColor(int aboveWaveColor) {
        mAboveWavePaint.setColor(aboveWaveColor);
        setAboveWaveAlpha(mAboveWaveAlpha);
    }

    public void setBlowWaveColor(int blowWaveColor) {
        mBlowWavePaint.setColor(blowWaveColor);
        setBlowWaveAlpha(mBlowWaveAlpha);
    }

    public int getBlowWaveColor() { return  mBlowWaveColor;}

    public void setAboveWaveAlpha(float aboveWaveAlpha) { mAboveWavePaint.setAlpha(alphaPercentToInt(aboveWaveAlpha)); }

    public void setBlowWaveAlpha(float blowWaveAlpha) { mBlowWavePaint.setAlpha(alphaPercentToInt(blowWaveAlpha)); }

    public void setWaveHz(float waveHz){ mWaveHz = waveHz; }

    public float getBlowWaveColorAlpha() { return mBlowWaveAlpha; }

    public float getAboveColorAlpha() { return mAboveWaveAlpha; }

    public float getWaveHz() { return mWaveHz; }

    private float getWaveMultiple(int size) {
        switch (size) {
            case LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HEIGHT_LARGE;
            case MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HZ_FAST;
            case MIDDLE:
                return WAVE_HZ_NORMAL;
            case WaveView.LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    private void initializeWave(WaveAttributes waveAttributes) {
        mAboveWaveColor = waveAttributes.getAboveWaveColor();
        mAboveWaveAlpha = waveAttributes.getAboveWaveAlpha();
        mBlowWaveColor = waveAttributes.getBlowWaveColor();
        mBlowWaveAlpha = waveAttributes.getBlowWaveAlpha();
        int waveLength = waveAttributes.getWaveLength();
        int waveHeight = waveAttributes.getWaveHeight();
        int waveHz = waveAttributes.getWaveHz();
        initializeWaveSize(waveLength, waveHeight, waveHz);
        initializeAboveWaveColorAlpha();
        initializeBlowWaveColorAlpha();
        initializePainters();
    }

    private void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        mBlowOffset = mWaveHeight * 0.4f;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
        setLayoutParams(params);
    }

    private void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(alphaPercentToInt(mAboveWaveAlpha));
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(alphaPercentToInt(mBlowWaveAlpha));
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private void initializeAboveWaveColorAlpha() {
        setAboveWaveAlpha(mAboveWaveAlpha);
        setAboveWaveColor(mAboveWaveColor);
    }

    private void initializeBlowWaveColorAlpha() {
        setBlowWaveAlpha(mBlowWaveAlpha);
        setBlowWaveColor(mBlowWaveColor);
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
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mBlowWavePath, mBlowWavePaint);
        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveLength == 0) {
                startWave();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mWaveLength==0){
            startWave();
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom() + 2;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}

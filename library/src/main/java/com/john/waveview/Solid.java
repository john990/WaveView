package com.john.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import static com.john.waveview.WaveView.alphaPercentToInt;

/**
 * Created by John on 2014/10/15.
 */
class Solid extends View {

    private final Paint mWavePaint = new Paint();
    private int mWaveColor;
    private float mWaveAlpha;
    private Drawable mBackgroundDrawable;

    private final Paint.Style STYLE = Paint.Style.FILL;

    public Solid(Context context, SolidAttributes solidAttributes) {
        this(context,null, 0, solidAttributes);
    }

    /*For Android Studio Tools*/
    private Solid(Context context){
        super(context); }

    private Solid(Context context, AttributeSet attrs, int defStyleAttr, SolidAttributes solidAttributes) {
        super(context, attrs, defStyleAttr);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        setLayoutParams(params);
        initialize(solidAttributes);
        setWaveColor();
    }

    private void initialize(SolidAttributes solidAttributes) {
        mBackgroundDrawable = solidAttributes.getBackgroundDrawable();

        mWaveColor = solidAttributes.getWaveColor();
        mWaveAlpha = solidAttributes.getWaveAlpha();
        setWaveAlpha(mWaveAlpha);
        mWavePaint.setStyle(STYLE);
        if(mBackgroundDrawable!=null) {
            this.setBackgroundDrawable(mBackgroundDrawable);
        }
    }

    public void setWaveColor() {
        mWavePaint.setColor(mWaveColor);
        setWaveAlpha(mWaveAlpha);
    }

    public void setWaveAlpha(float alpha){ mWavePaint.setAlpha(alphaPercentToInt(alpha)); }

    public float getWaveAlpha() { return mWavePaint.getAlpha(); }

    public void setWaveColor(int color) { mWavePaint.setColor(color); }

    public int getWaveColor() { return mWavePaint.getColor(); }

    @Override
    public Drawable getBackground() { return mBackgroundDrawable; }

    public void setBackground(Drawable backgroundDrawable){ mBackgroundDrawable = backgroundDrawable; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBackgroundDrawable==null) {
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), mWavePaint);
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), mWavePaint);
        }
    }
}

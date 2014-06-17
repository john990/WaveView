package com.john.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kai.wang on 6/17/14.
 */
public class WaveView extends View {

    private Path aboveWavePath = new Path();
    private Path blowWavePath = new Path();

    private Paint aboveWavePaint = new Paint();
    private Paint blowWavePaint = new Paint();


    //    private final int default_wave_to_top = 30;
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

    // wave animation
    private float[] aboveOffset = {1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 25.469612f, 24.936907f, 24.375708f, 23.804106f, 23.272884f, 22.717838f, 22.185139f, 21.634232f, 21.089384f, 20.57109f, 19.990925f, 19.41971f, 18.889145f, 18.29396f, 17.757067f, 17.174246f, 16.607258f, 16.057583f, 15.539549f, 14.972029f, 14.440879f, 13.883532f, 13.358805f, 12.847078f, 12.265658f, 11.7357855f, 11.228427f, 10.720737f, 10.145882f, 9.637184f, 9.089122f, 8.514334f, 7.995496f, 7.482301f, 6.907622f, 6.399367f, 5.8826733f, 5.318535f, 4.807485f, 4.2638555f, 3.7463584f, 3.1917238f, 2.68992f, 2.118116f, 1.596749f, 1.089212f, 0.5654556f, 0.013689734f, -0.509612f, -1.0505137f};
    private float[] blowOffset = {4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 28.433153f, 27.924328f, 27.401146f, 26.88904f, 26.355553f, 25.840899f, 25.30798f, 24.737104f, 24.142794f, 23.630413f, 23.121172f, 22.574392f, 22.051369f, 21.515535f, 20.93495f, 20.357899f, 19.817375f, 19.288565f, 18.709202f, 18.18553f, 17.651005f, 17.069977f, 16.517073f, 15.94023f, 15.42995f, 14.885934f, 14.370088f, 13.824445f, 13.230036f, 12.640225f, 12.082165f, 11.494119f, 10.986025f, 10.465705f, 9.866534f, 9.293761f, 8.783006f, 8.1871195f, 7.588639f, 7.0171866f, 6.4263854f, 5.898551f, 5.311175f, 4.7600684f, 4.2139015f, 3.6422157f, 3.0520093f, 2.4777887f, 1.9236779f, 1.3584205f};

    private boolean refreshable = true;

    /**
     * wave crest
     */
    private final int x_zoom = 150;
    /**
     * wave length
     */
    private final int y_zoom = 6;
    private final float offset = 0.5f;
    private final float max_right = x_zoom * offset;


    private Thread refreshThread;

    private final int REFRESH = 100;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == REFRESH) {
                invalidate();
            }
            return false;
        }
    });

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

        initializePainters();

        refreshThread = new RefreshThread();
        refreshThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        waveToTop = (int) (getHeight() * (1f - progress / 100f));

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

    private void calculatePath() {
        aboveWavePath.reset();
        blowWavePath.reset();

        aboveWavePath.moveTo(getLeft(), getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            aboveWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + aboveOffset[offsetIndex])) + waveToTop);
        }
        aboveWavePath.lineTo(getRight(), getHeight());


        blowWavePath.moveTo(getLeft(), getHeight());
        for (float i = 0; x_zoom * i <= getRight() + max_right; i += offset) {
            blowWavePath.lineTo((x_zoom * i), (float) (y_zoom * Math.cos(i + blowOffset[offsetIndex])) + waveToTop);
        }
        blowWavePath.lineTo(getRight(), getHeight());
    }

    public void setProgress(int progress) {
        this.progress = progress > 100 ? 100 : progress;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        refreshable = false;
        refreshThread.interrupt();
    }

    class RefreshThread extends Thread {
        @Override
        public void run() {
            while (refreshable) {
                try {
                    sleep(100);
                    offsetIndex++;
                    if (offsetIndex == aboveOffset.length) {
                        offsetIndex = 0;
                    }

                    calculatePath();

                    handler.sendEmptyMessage(REFRESH);

                    Log.i("","refreshable");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("","wave view recyle");
        }
    }

}

package com.waveview.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;

import com.john.waveview.WaveView;

/**
 * Created by kai.wang on 6/17/14.
 */
public class MainActivity extends Activity {

    private SeekBar seekBar;
    private WaveView waveView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        waveView = (WaveView) findViewById(R.id.wave_view);
/*        final ArgbEvaluator evaluator = new ArgbEvaluator();
        final int initColor = getResources().getColor(R.color.holo_red);
        final int endColor = getResources().getColor(R.color.holo_green);*/



      seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveView.setProgress(progress);
             /* int color = (Integer) evaluator.evaluate(progress/100f,initColor,endColor);
                waveView.setAboveWaveColor(color);
                waveView.setBlowWaveColor(color);
                waveView.setWaveColor(color);*/
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
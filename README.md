![icon](https://raw.github.com/john990/WaveView/master/app/src/main/res/drawable-hdpi/ic_launcher.png)

WaveView
========
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/john990/WaveView?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A wave view of android,can be used as progress bar.


### Screenshot
-------------
<img src="https://raw.github.com/john990/WaveView/master/screenshot%26apk/screenshot.gif" style="width:300px;height:500px;"/>

### APK
---------------
[demo.apk](https://raw.github.com/john990/WaveView/master/screenshot%26apk/demo.unaligned.apk)

### What can be used as
-------------
  * background
  * progress bar

### How to use
--------------
[https://github.com/john990/WaveView/blob/master/app/src/main/res/layout/main.xml#L7-L17](https://github.com/john990/WaveView/blob/master/app/src/main/res/layout/main.xml#L7-L17)
````xml
    <com.john.waveview.WaveView
        android:id="@+id/wave_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#ff702e8c"
        wave:above_wave_color="@android:color/white"
        wave:blow_wave_color="@android:color/white"
        wave:progress="80"
        wave:wave_height="little"
        wave:wave_hz="normal"
        wave:wave_length="middle" />
````
or you can juest use(default progress is 80%)
````xml
    <com.john.waveview.WaveView
        android:id="@+id/wave_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/holo_purple"
        />
````

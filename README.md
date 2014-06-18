WaveView
========

a wave view of android,also can be used as progress bar.


### Screenshot
-------------
![screen](https://raw.github.com/kai-wang-john/WaveView/master/screenshot%26apk/screenshot.gif)

### APK
---------------
[demo.apk](https://raw.github.com/kai-wang-john/WaveView/master/screenshot%26apk/demo.unaligned.apk)

### What can be used as
-------------
  * background
  * progress bar

### How to use
--------------
[https://github.com/kai-wang-john/WaveView/blob/master/demo/res/layout/main.xml#L9-L17](https://github.com/kai-wang-john/WaveView/blob/master/demo/res/layout/main.xml#L9-L17)
````xml

<com.john.waveview.WaveView
		android:id="@+id/wave_view"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:background="@color/holo_purple"
		wave:above_wave_color="@android:color/white"
		wave:blow_wave_color="@android:color/white"
		wave:progress="60"
		/>
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

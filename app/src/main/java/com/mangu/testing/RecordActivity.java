package com.mangu.testing;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class RecordActivity extends AppCompatActivity {
    static final int MY_MSG = 1;
    static final int ERROR_MSG = -1;
    public static double REFERENCE = 0.00002;
    SplEngine mEngine;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start, stop, play;
    private double amplitude = -1; //Umbral
    public Handler mhandle = new Handler() {
      @Override
        public void handleMessage(Message msg) {
          switch (msg.what) {
              case MY_MSG:
                amplitude = (double) msg.obj;
                break;
              case ERROR_MSG:
                  Toast.makeText(
                          RecordActivity.this,
                          "Error " + msg.obj, Toast.LENGTH_LONG).show();
                  mEngine.stop_engine();
                  break;
              default :
                  super.handleMessage(msg);
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        start = (Button) findViewById(R.id.btn_grabar);
        stop = (Button) findViewById(R.id.btn_prueba);
        play = (Button) findViewById(R.id.btn_reproducir);
        stop.setEnabled(false);
        stop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        play.setEnabled(false);
        play.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);


        mEngine = new SplEngine(this.mhandle, RecordActivity.this);


        /**
         * Experimental. It may fail

         Intent intent = new Intent();
         intent.putExtra("path",outputFile);
         setResult(1, intent);
         finishActivity(1);*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("value", amplitude);
        setResult(1, intent);
        finish();
    }

    public void start(View view) {

        mEngine.start_engine();
        start.setEnabled(false);
        start.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started",
                Toast.LENGTH_LONG).show();
        Log.i(this.toString(), "Recording started");
    }

    public void stop(View view) {
        amplitude = mEngine.getMedian();
        mEngine.stop_engine();
        /*amplitude = myAudioRecorder.getMaxAmplitude();
        double md = 20.0 * Math.log10(amplitude / 32767.0);
        md = Math.abs(md)*10;
        amplitude = md;*/
        //db = 20.0 * log10(peakAmplitude/32767.0)
        stop.setEnabled(false);
        stop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        start.setEnabled(true);
        play.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Value:"+amplitude,
                Toast.LENGTH_LONG).show();
        Log.i(this.toString(), "Value: "+amplitude);

    }

    public void play(View view) {
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
            m.prepare();
        } catch (IOException ex) {
            Log.e(ex.getCause().toString(), ex.toString());
        }
        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio",
                Toast.LENGTH_LONG).show();
        Log.i(this.toString(), "Playing audio");
    }
}

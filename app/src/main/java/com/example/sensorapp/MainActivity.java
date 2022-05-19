package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView textView;
    boolean l;
    boolean a;
    TextView statusText;
    TextView textView2;
    TextView exText;
    SensorManager sensorManager;
    Sensor lightSensor;
    Sensor accSensor;
    double currentAcc;
    double prevAcc;
    int check = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter("com.example.EXAMPLE_ACTION");
        registerReceiver(broadcastReceiver,filter);
        setContentView(R.layout.activity_main);
        textView2 = (TextView) findViewById(R.id.AccText);
        textView = (TextView) findViewById(R.id.LightText);
        statusText = (TextView) findViewById(R.id.statusText);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        exText = (TextView) findViewById(R.id.exText);
 //        IntentFilter intentFilter = new IntentFilter("com.example.SensorApp")

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_NORMAL);
        IntentFilter filter = new IntentFilter("com.example.EXAMPLE_ACTION");
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (sensorEvent.values[0] > 10) {
                textView.setText("Sensor Changed: " + sensorEvent.values[0]);
                l = true;
            } else {
                textView.setText("Device in Pocket.");
                l = false;
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            currentAcc = Math.sqrt(x * x + y * y + z * z);
            double changeAcc = Math.abs(currentAcc - prevAcc);
            prevAcc = currentAcc;

            textView2.setText("Changed: " + changeAcc);
//            textView2.setText("Sensor Changed X: "+sensorEvent.values[0]+"Sensor Changed Y: "+sensorEvent.values[1]+"Sensor Changed Z: "+sensorEvent.values[2]);

//            if((Math.abs(x)<=1)&&(Math.abs(y)<=1)&&(Math.abs(z)<=10)){
            if (changeAcc == 0) {
                textView2.setText("Device In Stable Position");
                a = false;
            } else {
                a = true;
            }

            checkState(a,l);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void checkState(boolean a,boolean l){
        if ((a && !l == true)&&check!=0) {
            statusText.setText("Volume Up");
            sendBroadcast("up");
            check = 0;
        } else if ((!a && l)&&check!=1) {
            statusText.setText("Volume Down");
            sendBroadcast("down");
            check = 1;
        } else{
            statusText.setText("Keeping the current status.");
        }
    }
    public void sendBroadcast(String string){
        Intent intent = new Intent("com.example.EXAMPLE_ACTION");
        intent.putExtra("com.example.EXTRA_TEXT",string);
        sendBroadcast(intent);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedText = intent.getStringExtra("com.example.EXTRA_TEXT");
            exText.setText(receivedText);
        }
    };
}


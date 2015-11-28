package de.turtle.moving.accelratorapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import de.turtle.moving.http.HttpMethod;
import de.turtle.moving.http.impl.HttpConnectionBuilder;

public class EventActivity extends Activity implements SensorEventListener {

    private static final String CAMERA_TAG = "Camera";
    private static final String TAG = EventActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private boolean color = false;
    private boolean eventRecognized = false;
    private View view;
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(EventActivity.class.getSimpleName(), "On create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.WHITE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        Log.i(TAG, "started");
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            doStuff(event);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    }

    private void doStuff(SensorEvent event) {
        float[] values = event.values;
        final StringBuilder stringBuilder = new StringBuilder();

        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        stringBuilder
                .append(String.format("x: %s\n", x))
                .append(String.format("y: %s\n", y))
                .append(String.format("z: %s", z));
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(stringBuilder.toString());
        if(x < 0.75 && x > 0.3 ){
            view.setBackgroundColor(Color.GREEN);
            final Intent eventUpIntent = new Intent(this, EventDownActivity.class);
            startActivityForResult(eventUpIntent, 1);


            //final Intent intent = new Intent(this, CameraActivity.class);
            //startActivityForResult(intent, 1);
           // eventRecognized = false;
            //eventRecognized =true;
        }
        /*else{
            if(eventRecognized ){
                if (x >= 0.75 || x <= 0.3) {
                    view.setBackgroundColor(Color.WHITE);

                }
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        view.setBackgroundColor(Color.WHITE);
    }
}

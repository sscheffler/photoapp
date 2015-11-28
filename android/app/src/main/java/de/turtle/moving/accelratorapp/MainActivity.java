package de.turtle.moving.accelratorapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String CAMERA_TAG = "Camera";
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private boolean color = false;
    private boolean eventRecognized = false;
    private View view;
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MainActivity.class.getSimpleName(), "On create");
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
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
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
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            //Log.d(MainActivity.class.getSimpleName(), "light -> " + event.values[0]);
        }
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
            final Intent intent = new Intent(this, CameraActivity.class);
            startActivityForResult(intent, 1);
            eventRecognized = false;
            eventRecognized =true;
        }else{
            if(eventRecognized ){
                if (x >= 0.75 || x <= 0.3) {
                    view.setBackgroundColor(Color.WHITE);

                }
            }
        }
        /*if(eventRecognized && x < 0.2){
            eventRecognized = false;
            Toast.makeText(getApplicationContext(), "taking photo", Toast.LENGTH_SHORT)
                    .show();

            view.setBackgroundColor(Color.WHITE);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        eventRecognized = false;
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Log.i(TAG, result);
                Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
                toast.show();
                Log.i(TAG, "sended information");
            }
        }
    }
}

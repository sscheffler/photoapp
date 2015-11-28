package de.turtle.moving.accelratorapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import de.turtle.moving.http.HttpMethod;
import de.turtle.moving.http.impl.HttpConnectionBuilder;
import de.turtle.moving.img.CameraStateCallBack;
import de.turtle.moving.img.ImageAvailableListener;
import de.turtle.moving.model.StatisticsInformation;

import static android.graphics.ImageFormat.JPEG;

public class EventDownActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = EventDownActivity.class.getSimpleName();
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String COUCHDB_URL = "COUCHDB_URL";

    private SensorManager sensorManager;
    private View view;
    private boolean makePhotoEventHappened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_down);
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.YELLOW);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            doStuff(event);
        }
    }

    private void doStuff(final SensorEvent event) {
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
        if(!makePhotoEventHappened && x < 0.15){
            makePhotoEventHappened=true;
            view.setBackgroundColor(Color.GREEN);

            final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            final UUID deviceUuid = new UUID(tm.getDeviceId().hashCode(), ((long)tm.getDeviceId().hashCode() << 32));
            final Intent intent = new Intent(this, CameraService.class);
            intent.putExtra(DEVICE_ID, deviceUuid.toString());

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            final String couchDbUrl = prefs.getString(getString(R.string.couchdb_url_key), "not defined");
            final String couchDbDatabase = prefs.getString(getString(R.string.couchdb_database_key), "not defined");
            final String url = couchDbUrl + "/" + couchDbDatabase + "/";
            intent.putExtra(COUCHDB_URL, url);

            startService(intent);
            finish();

        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {

    }
}

class MakePhotoTask extends AsyncTask<String, Void, String> {

    private final String TAG = MakePhotoTask.class.getSimpleName();
    private final Activity activity;

    MakePhotoTask(final Activity activity) {
        this.activity = activity;
    }

    protected String doInBackground(String... urls) {

        final StatisticsInformation info = new StatisticsInformation();
        info.setDeviceId("device");
        info.setTimeStamp(System.currentTimeMillis());

        try {
            final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            final HandlerThread backgroundThread = new HandlerThread("CameraPreview");
            backgroundThread.start();
            final Handler backgroundHandler = new Handler(backgroundThread.getLooper());
            try {
                final ImageAvailableListener imageAvailableListener = new ImageAvailableListener("/storage/emulated/0/DCIM/", activity);
                final ImageReader imageReader = ImageReader.newInstance(4128, 2322, JPEG, 1);
                imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);
                final CameraStateCallBack cameraStateCallback = new CameraStateCallBack(imageReader, backgroundHandler);
                manager.openCamera("0", cameraStateCallback, backgroundHandler);
            } catch (CameraAccessException e){
                Log.e(TAG, "Error", e);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return "";
    }
}

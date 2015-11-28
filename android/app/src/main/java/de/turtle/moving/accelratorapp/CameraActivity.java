package de.turtle.moving.accelratorapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.util.Log;

import de.turtle.moving.img.CameraStateCallBack;
import de.turtle.moving.img.ImageAvailableListener;
import de.turtle.moving.img.ImageProcessor;
import de.turtle.moving.model.StatisticsInformation;

import static android.graphics.ImageFormat.JPEG;

public class CameraActivity extends Activity {
    final static String TAG = CameraActivity.class.getSimpleName();

    private boolean imageProduced = false;

    private byte[] pictureBytes ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final StatisticsInformation info = new StatisticsInformation();
        info.setDeviceId("device");
        info.setTimeStamp(System.currentTimeMillis());
        try {

            final CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
            final HandlerThread backgroundThread = new HandlerThread("CameraPreview");
            backgroundThread.start();
            final Handler backgroundHandler = new Handler(backgroundThread.getLooper());
            Log.i(TAG, "initalized camera");

            final ImageReader imageReader = ImageReader.newInstance(4128, 2322, JPEG, 1);
            try {
                final ImageAvailableListener imageAvailableListener = new ImageAvailableListener("/storage/emulated/0/DCIM/", this);
                imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);
                final CameraStateCallBack cameraStateCallback = new CameraStateCallBack(imageReader, backgroundHandler);
                manager.openCamera("0", cameraStateCallback, backgroundHandler);
            } catch (CameraAccessException e){
                Log.e(TAG, "Error", e);
            }

            Log.i(TAG, "finished");
            final Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "exited camera activity");
            setResult(Activity.RESULT_OK, returnIntent);
            Log.i(TAG, "finished image producing");
            finish();

        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    public void setPictureBytes(final byte[] pictureBytes) {
        this.pictureBytes = pictureBytes;
    }

    public void setImageProduced() {
        this.imageProduced = true;
    }
}

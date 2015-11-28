package de.turtle.moving.img;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import static android.content.Context.*;
import static android.graphics.ImageFormat.*;

/**
 * @author Stefan Scheffler
 */
public class ImageProcessor {

    private static String TAG=ImageProcessor.class.getSimpleName();

    public byte[] produce(final Activity activity){
        /*final CameraManager manager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        final HandlerThread backgroundThread = new HandlerThread("CameraPreview");
        backgroundThread.start();
        final Handler backgroundHandler = new Handler(backgroundThread.getLooper());
        Log.i(TAG, "initalized camera");

        try {
            final ImageAvailableListener imageAvailableListener = new ImageAvailableListener("/storage/emulated/0/DCIM/", cameraActivity);
            final ImageReader imageReader = ImageReader.newInstance(4128, 2322, JPEG, 1);
            imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);
            Log.i(TAG, "initialized Image Ŕeader");

            final CameraStateCallBack cameraStateCallback = new CameraStateCallBack(imageReader, backgroundHandler);
            manager.openCamera("0", cameraStateCallback, backgroundHandler);
            Log.i(TAG, "finished camera activity");
        } catch (CameraAccessException e){
            Log.e(TAG, "Error", e);
        }*/
        return new byte[0];
    }
}

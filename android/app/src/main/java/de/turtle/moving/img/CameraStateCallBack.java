package de.turtle.moving.img;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;

import static android.util.Log.*;

/**
 * @author Stefan Scheffler
 */
public class CameraStateCallBack extends CameraDevice.StateCallback {

    private static final String TAG = CameraStateCallBack.class.getCanonicalName();
    private final ImageReader imageReader;
    private final Handler handler;

    public CameraStateCallBack(final ImageReader imageReader, final Handler handler) {
        this.imageReader = imageReader;
        this.handler = handler;
    }

    @Override
    public void onOpened(final CameraDevice camera) {
        d(TAG, "Open");
        try {
            final CameraSessionCallback cameraSessionCallback = new CameraSessionCallback(camera, imageReader, handler);
            camera.createCaptureSession(Collections.singletonList(imageReader.getSurface()), cameraSessionCallback, handler);
        } catch (CameraAccessException e) {
            e(TAG, "Error", e);
        }
    }
    @Override
    public void onDisconnected(CameraDevice camera) {

        d(TAG, "Disconnected");
    }
    @Override
    public void onError(CameraDevice camera, int error) {
        d(TAG, String.format("Error: <%s>", error));
    }
}

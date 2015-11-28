package de.turtle.moving.img;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * @author Stefan Scheffler
 */
public class CameraSessionCallback extends CameraCaptureSession.StateCallback{

    private static final String TAG = CameraSessionCallback.class.getCanonicalName();
    private final CameraDevice camera;
    private final ImageReader imageReader;
    private final Handler handler;

    public CameraSessionCallback(final CameraDevice camera, final ImageReader imageReader, final Handler handler) {
        this.camera = camera;
        this.imageReader = imageReader;
        this.handler = handler;
    }

    @Override
    public void onConfigured(CameraCaptureSession session) {
        Log.d(TAG,"OnConfigured");
        try {
            final CaptureRequest captureRequest = buildCaptureRequest();
            if (captureRequest != null) {
                session.capture(captureRequest, null, handler);
            }
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }finally {
            camera.close();
        }

    }

    @Override
    public void onConfigureFailed(CameraCaptureSession session) {
        Log.d(TAG,"Configure Failed");
    }

    private CaptureRequest buildCaptureRequest() {
        try {
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(imageReader.getSurface());
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error: ", e);
            return null;
        }
    }
}


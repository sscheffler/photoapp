package de.turtle.moving.accelratorapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import de.turtle.moving.http.impl.DefaultStatisticsSender;

import static android.graphics.ImageFormat.JPEG;

/**
 * @author Stefan Scheffler
 */
public class CameraService extends Service {
    private static final String TAG = "VideoProcessing";
    private static final int CAMERA = CameraCharacteristics.LENS_FACING_BACK;
    private CameraDevice camera;
    private CameraCaptureSession session;
    private ImageReader imageReader;
    private String deviceId;
    private String couchDbUrl;

    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            CameraService.this.camera = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            CameraService.this.session = session;
            try {
                //session.setRepeatingRequest(createCaptureRequest(), null, null);
                session.capture(createCaptureRequest(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            final Image img = reader.acquireLatestImage();
            final byte[] bytes = processImage(img);
            if (bytes.length > 0) {
                new DefaultStatisticsSender(bytes, deviceId)
                        .execute(couchDbUrl);
            }


            img.close();
        }
    };

    @Override
    public void onCreate() {

        final HandlerThread backgroundThread = new HandlerThread("CameraPreview");

        backgroundThread.start();
        final Handler backgroundHandler = new Handler(backgroundThread.getLooper());
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            final String cameraId = getCamera(manager);
            manager.openCamera(cameraId, cameraStateCallback, backgroundHandler);
            //imageReader = ImageReader.newInstance(1440, 1080, JPEG, 1);
            imageReader = ImageReader.newInstance(640, 480, JPEG, 1);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Return the Camera Id which matches the field CAMERA.
     */
    public String getCamera(CameraManager manager) {
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CAMERA) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            deviceId = (String)intent.getExtras().get(EventDownActivity.DEVICE_ID);
            couchDbUrl = (String)intent.getExtras().get(EventDownActivity.COUCHDB_URL);
            final HandlerThread backgroundThread = new HandlerThread("CameraPreview");
            backgroundThread.start();
            final Handler backgroundHandler = new Handler(backgroundThread.getLooper());
            camera.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            session.abortCaptures();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        session.close();
    }

    private byte[] processImage(Image image) {
        try {
            final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            final byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            save(bytes);
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private void save(final byte[] bytes) throws IOException {
        //activity.setPictureBytes(bytes);
        final File file = new File("/storage/emulated/0/DCIM/bild.jpg");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        }
    }

    private CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(imageReader.getSurface());
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE);
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

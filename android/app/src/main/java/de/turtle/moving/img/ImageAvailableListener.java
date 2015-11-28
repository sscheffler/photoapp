package de.turtle.moving.img;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.ImageReader;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import de.turtle.moving.accelratorapp.CameraActivity;
import de.turtle.moving.accelratorapp.R;
import de.turtle.moving.http.impl.DefaultStatisticsSender;

/**
 * Assuming image is a JPEG
 *
 * @author Stefan Scheffler
 */
public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private static final String TAG = ImageAvailableListener.class.getCanonicalName();

    private final String path;
    private final Activity activity;

    public ImageAvailableListener(final String path, final Activity activity) {
        this.path = path;
        this.activity = activity;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        try (final Image img = reader.acquireLatestImage()) {
            Log.d(TAG, "Image is available");
            byte[] bytes = processImage(img);
            final DefaultStatisticsSender sender = new DefaultStatisticsSender(activity, bytes, "deviceId");

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            final String couchDbUrl = prefs.getString(activity.getString(R.string.couchdb_url_key), "not defined");
            final String couchDbDatabase = prefs.getString(activity.getString(R.string.couchdb_database_key), "not defined");
            final String url = couchDbUrl + "/" + couchDbDatabase + "/";

            sender.execute(url);
        }
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
        final File file = new File(path + "bild.jpg");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        }
    }
}




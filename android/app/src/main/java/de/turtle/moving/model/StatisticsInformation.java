package de.turtle.moving.model;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsInformation implements HttpInformation {

    private static final String LOG_TAG="Builder.HttpConnection";
    public static String GPS_LOCATION = "loc_gps";
    public static String NETWORK_LOCATION = "loc_network";
    public static String DEVICE_ID = "device_id";
    public static String TIME = "time";
    public static String PICTURE = "picture";
    public static String ATTACHMENTS = "_attachments";
    public static String LATITUDE = "lat";
    public static String LONGITUDE = "long";

    private final String id;
    private String deviceId;
    private long timeStamp;
    private byte[] bytes;
    private Location gpsLocation;

    private Location networkLocation;

    public StatisticsInformation(){
        id = UUID.randomUUID().toString();
    }

    public long getTimeStamp() {
        return timeStamp;
    }



    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public Location getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(final Location networkLocation) {
        this.networkLocation = networkLocation;
    }

    public Location getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(final Location gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    @Override
    public String buildJsonString() {
        try {
            final JSONObject gpsCoordinates = buildCoordinatesJson(gpsLocation);
            final JSONObject networkCoordinates = buildCoordinatesJson(networkLocation);
            final JSONObject o = new JSONObject();
            o.accumulate("content_type", "image/jpeg");
            o.accumulate("data",new String(Base64.encode(bytes, Base64.DEFAULT)) );

            JSONObject o2 = new JSONObject();
            o2.accumulate(PICTURE, o);

            final JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(DEVICE_ID, this.getDeviceId());
            jsonObject.accumulate(TIME, this.getTimeStamp());
            jsonObject.accumulate(ATTACHMENTS, o2);
            if(null != gpsCoordinates){
                jsonObject.accumulate(GPS_LOCATION, gpsCoordinates);
            }
            if(null != networkCoordinates){
                jsonObject.accumulate(NETWORK_LOCATION, networkCoordinates);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(LOG_TAG, String.format("<%s> could not be transformed in Json Object", this.getClass().getSimpleName() ), e);
        }
        return null;
    }

    @NonNull
    private JSONObject buildCoordinatesJson(final Location location) throws JSONException {
        if (location != null){
            final JSONObject coordinates= new JSONObject();
            final String latitude = Double.toString(location.getLatitude());
            final String longitude = Double.toString(location.getLongitude());

            coordinates.accumulate(LATITUDE, latitude);
            coordinates.accumulate(LONGITUDE, longitude);
            return coordinates;
        }
        return null;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}

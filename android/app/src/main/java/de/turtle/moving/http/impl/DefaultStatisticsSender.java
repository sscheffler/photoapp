package de.turtle.moving.http.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.UUID;

import de.turtle.moving.accelratorapp.CameraActivity;
import de.turtle.moving.accelratorapp.MainActivity;
import de.turtle.moving.http.StatisticsSender;
import de.turtle.moving.img.ImageProcessor;
import de.turtle.moving.model.StatisticsInformation;

import static de.turtle.moving.http.BodyType.*;
import static de.turtle.moving.http.HttpMethod.*;

public class DefaultStatisticsSender extends AsyncTask<String, Void, String> implements StatisticsSender {

    public static final String UTF_8 = "UTF-8";
    private static String LOG_TAG = "RequestLog";
    private Activity activity;
    private byte[] bytes;
    private String deviceId;

    public DefaultStatisticsSender(final Activity activity, final byte[] bytes, final String deviceId)  {
        this.activity = activity;
        this.bytes = bytes;
        this.deviceId = deviceId;
    }

    public DefaultStatisticsSender(final byte[] bytes, final String deviceId) {
        this.bytes = bytes;
        this.deviceId = deviceId;
    }

    @Override
    protected String doInBackground(String... params) {
        final StatisticsInformation info = new StatisticsInformation();
        if(null != activity){
            getLocationInformation(info);
        }

        info.setDeviceId(deviceId);
        info.setTimeStamp(System.currentTimeMillis());
        info.setBytes(bytes);

        try {
            return doSend(params[0], info);
        } catch (Exception e) {
            Log.w(LOG_TAG, e);
        }
        return "Did not work!";
    }

    private void getLocationInformation(final StatisticsInformation info) {
        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final Location lastGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final Location lastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        info.setGpsLocation(lastGpsLocation);
        info.setNetworkLocation(lastNetworkLocation);
    }

    private static String doSend(final String urlString, final StatisticsInformation info) throws IOException, JSONException {
        final HttpConnectionBuilder builder = new HttpConnectionBuilder(urlString, PUT);
        final HttpURLConnection con = builder
                .setStatisticsInfo(info)
                .setBodyType(JSON)
                .build();
        con.setInstanceFollowRedirects(false);
        con.setDoOutput(true);

        send(info, con);
        int status = con.getResponseCode();
        BufferedReader reader;
        if(wasSuccess(status)){
            reader = buildBufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
        }else {
            reader = buildBufferedReader(new InputStreamReader(con.getErrorStream(), UTF_8));
        }

        String line=null;
        final StringBuilder responseBuilder = new StringBuilder(2048);
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        Log.d(LOG_TAG, responseBuilder.toString());
        return responseBuilder.toString();
    }

    private static void send(final StatisticsInformation info, final HttpURLConnection con) throws IOException {
        try(final DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write(info.buildJsonString().getBytes());
        }
    }

    @NonNull
    private static BufferedReader buildBufferedReader(final InputStreamReader in) throws UnsupportedEncodingException {
        return new BufferedReader(in);
    }

    private static boolean wasSuccess(final int responseCode){
        return (200 <= responseCode) && (responseCode < 300);
    }
}


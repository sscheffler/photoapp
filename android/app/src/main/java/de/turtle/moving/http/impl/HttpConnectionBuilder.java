package de.turtle.moving.http.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.turtle.moving.http.BodyType;
import de.turtle.moving.http.HttpMethod;
import de.turtle.moving.model.StatisticsInformation;

/**
 * @author Stefan Scheffler
 */
public class HttpConnectionBuilder {

    private static final String LOG_TAG="Builder.HttpConnection";
    private final Map<String, String> propertyMap= new HashMap<>();
    private final String url;
    private final HttpMethod httpMethod;
    private BodyType bodyType;
    private StatisticsInformation info;

    public HttpConnectionBuilder(@NonNull final String url, @NonNull final HttpMethod httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public HttpConnectionBuilder addHeaderProperty(@NonNull final String key, final String value){
        propertyMap.put(key,value);
        return this;
    }

    public HttpConnectionBuilder setStatisticsInfo(@NonNull final StatisticsInformation info){
        this.info = info;
        return this;
    }

    public HttpConnectionBuilder setBodyType(@NonNull final BodyType bodyType){
        this.bodyType = bodyType;
        return this;
    }

    public HttpURLConnection buildStatusConnection(){
        try {

            final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod(httpMethod.toString());

            return con;
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("Error in Url parsing: <%s>", url), e);
            return null;
        }

    }


    public HttpURLConnection build(){
        try {
            final HttpURLConnection con = (HttpURLConnection) new URL(url+info.getId()).openConnection();
            con.setRequestMethod(httpMethod.toString());

            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setRequestProperty(bodyType.getHeaderKey(), bodyType.getBodyType());


            return con;
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("Error in Url parsing: <%s>", url), e);
            return null;
        }
    }
}
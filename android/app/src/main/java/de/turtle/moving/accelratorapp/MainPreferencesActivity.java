package de.turtle.moving.accelratorapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import de.turtle.moving.http.HttpMethod;
import de.turtle.moving.http.impl.HttpConnectionBuilder;

/**
 * @author Stefan Scheffler
 */
public class MainPreferencesActivity extends PreferenceActivity {

    public static final int LOCATION_UPDATE_DELAY = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment(this)).commit();

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_DELAY, 1, new LocListener());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_DELAY, 1, new LocListener());
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment {

        private static final String TAG = MainPreferencesActivity.class.getSimpleName();
        private final MainPreferencesActivity mainPreferencesActivity;

        public PreferenceFragment(final MainPreferencesActivity mainPreferencesActivity) {
            this.mainPreferencesActivity = mainPreferencesActivity;
        }


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final Preference startButton = (Preference) findPreference(getString(R.string.button_start));
            startButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.i(TAG, "starting");
                    Intent intent = new Intent(mainPreferencesActivity, EventActivity.class);
                    startActivity(intent);
                    //code for what you want it to do
                    return true;
                }
            });

            final Preference testConnectionButton = (Preference) findPreference(getString(R.string.button_connection_test));
            testConnectionButton.setOnPreferenceClickListener(new TestConnectionClickListener(getActivity()));

        }
    }
}

class TestConnectionClickListener implements Preference.OnPreferenceClickListener {

    private static final String TAG = TestConnectionClickListener.class.getSimpleName();
    private final Activity activity;

    TestConnectionClickListener(final Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        final TestConnectionTask testConnectionTask = new TestConnectionTask(activity, this);
        String message = "";
        try {
            message = testConnectionTask.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        final Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toast.show();
        return true;
    }

}

class LocListener implements LocationListener {

    @Override
    public void onLocationChanged(final Location location) {
        Log.i("a", "location changed");
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        Log.i("a", "status changed");
    }

    @Override
    public void onProviderEnabled(final String provider) {
        Log.i("a", "enabled");
    }

    @Override
    public void onProviderDisabled(final String provider) {
        Log.i("a", "disabled");
    }
}


class TestConnectionTask extends AsyncTask<String, Void, String> {

    private final Activity activity;
    private TestConnectionClickListener testConnectionClickListener;

    TestConnectionTask(final Activity activity, final TestConnectionClickListener testConnectionClickListener) {
        this.activity = activity;
        this.testConnectionClickListener = testConnectionClickListener;
    }

    protected String doInBackground(String... urls) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        final String couchDbUrl = prefs.getString(activity.getString(R.string.couchdb_url_key), "not defined");
        final HttpConnectionBuilder httpConnectionBuilder = new HttpConnectionBuilder(couchDbUrl, HttpMethod.GET);
        try {
            final HttpURLConnection con = httpConnectionBuilder.buildStatusConnection();

            final StringBuilder result = new StringBuilder();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            return result.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}





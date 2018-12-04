package com.pro.ahmed.weather2.sevices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pro.ahmed.weather2.model.MySingleton;
import com.pro.ahmed.weather2.model.Weather5dayesForecastModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.signin.internal.SignInClientImpl.ACTION_START_SERVICE;

public class NotificationService extends Service {
    private Timer timer;
    Notifications mNotifications;
    String UrlForeCast_5_Days;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String ApiKey = "464c9a8ca5afb26e953d3e2eaca13426";
    private String descriptionForecast_5_Days;
    private String iconForecast_5_Days;
    private String currentNotification;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        mNotifications = new Notifications(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //mNotifications.fireSoundNotify();
                getForeCastWeather_5_Days();
            }
        }, 0, 1 * 60 * 60 * 1000);//1 Hours
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        mNotifications = new Notifications(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //mNotifications.fireSoundNotify();
                getForeCastWeather_5_Days();
            }
        }, 0, 1 * 60 * 60 * 1000);//1 Hours
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    void getForeCastWeather_5_Days() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        UrlForeCast_5_Days = "http://api.openweathermap.org/data/2.5/forecast?lang=ar&units=metric&lat=" + prefs.getFloat("lat", 0) +
                "&lon=" + prefs.getFloat("lon", 0)
                + "&APPID=" + ApiKey;
        StringRequest weatherForecastRequest = new StringRequest(Request.Method.GET, UrlForeCast_5_Days, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray weatherForecastArray = jsonObject.getJSONArray("list");
                    Log.e("Length", String.valueOf(weatherForecastArray.length()));

                    JSONObject currentObjectForecast = weatherForecastArray.getJSONObject(1);
                    JSONObject main = currentObjectForecast.getJSONObject("main");
                    JSONArray weatherArray = currentObjectForecast.getJSONArray("weather");
                    for (int j = 0; j < weatherArray.length(); j++) {
                        JSONObject currentObject = weatherArray.getJSONObject(j);
                        descriptionForecast_5_Days = currentObject.getString("description");
                        iconForecast_5_Days = currentObject.getString("icon");
                    }

                    int temp = main.getInt("temp");
                    editor.putInt("tempForecast", temp);
                    editor.putString("descriptionForecast", descriptionForecast_5_Days);
                    editor.putString("iconForecast", iconForecast_5_Days);
                    editor.commit();
                    editor.apply();

                    mNotifications.fireNotify(prefs.getInt("temp", 0),
                            prefs.getString("description", null), prefs.getString("icon", null));

                    if ((iconForecast_5_Days.equals("10d")) || (iconForecast_5_Days.equals("10n")) ||
                            (iconForecast_5_Days.equals("11d")) || (iconForecast_5_Days.equals("11n")) ||
                            (iconForecast_5_Days.equals("13d")) || (iconForecast_5_Days.equals("13n")) ||
                            (iconForecast_5_Days.equals("50d")) || (iconForecast_5_Days.equals("50n"))) {
                        if (!(iconForecast_5_Days.equals(prefs.getString("notification", "null")))) {
                            mNotifications.fireSoundNotify(prefs.getInt("tempForecast", 0),
                                    "تنبيه!! " + prefs.getString( "descriptionForecast", null), prefs.getString("iconForecast", null));
                            editor.putString("notification", iconForecast_5_Days);
                            editor.commit();
                            editor.apply();
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JsonException", e.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mNotifications.fireNotify(prefs.getInt("temp", 0),
                        prefs.getString("description", null), prefs.getString("icon", null));
            }
        });
        //TODO handle case not internet found

        weatherForecastRequest.setShouldCache(true);
        MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(weatherForecastRequest);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

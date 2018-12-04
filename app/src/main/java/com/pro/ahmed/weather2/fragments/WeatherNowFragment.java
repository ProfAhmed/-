package com.pro.ahmed.weather2.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pro.ahmed.weather2.MapsActivity;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.model.MySingleton;
import com.pro.ahmed.weather2.model.Weather5dayesForecastModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherNowFragment extends Fragment {

    private static final String ApiKey = "464c9a8ca5afb26e953d3e2eaca13426";
    String UrlForeCast_5_Days;
    SharedPreferences prefs;
    String Url = "";
    String descriptionForecast_5_Days;
    String iconForecast_5_Days;
    @BindView(R.id.tvDescriptionAndWeatherIcon)
    TextView tvDescriptionAndWeatherIcon;
    @BindView(R.id.tvTemp)
    TextView tvTemp;
    @BindView(R.id.tvHumidity)
    TextView tvHumidity;
    @BindView(R.id.tvPressure)
    TextView tvPressure;
    @BindView(R.id.tvVisibility)
    TextView tvVisibility;
    @BindView(R.id.tvWindSpeed)
    TextView tvWindSpeed;
    @BindView(R.id.tvSunRise)
    TextView tvSunRise;
    @BindView(R.id.tvSunSet)
    TextView tvSunSet;
    @BindView(R.id.tvDatAndDay)
    TextView tvDatAndDay;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvLastUpdate)
    TextView tvLastUpdate;
    @Nullable
    @BindView(R.id.tvForecastDay1)
    TextView tvForecastDay1;
    @BindView(R.id.tvForecastDay2)
    TextView tvForecastDay2;
    @BindView(R.id.tvForecastDay3)
    TextView tvForecastDay3;
    @BindView(R.id.tvForecastDay4)
    TextView tvForecastDay4;
    @BindView(R.id.tvForecastDay5)
    TextView tvForecastDay5;
    @BindView(R.id.tvForeCastTempAndIcon1)
    TextView tvForeCastTempAndIcon1;
    @BindView(R.id.tvForeCastTempAndIcon2)
    TextView tvForeCastTempAndIcon2;
    @BindView(R.id.tvForeCastTempAndIcon3)
    TextView tvForeCastTempAndIcon3;
    @BindView(R.id.tvForeCastTempAndIcon4)
    TextView tvForeCastTempAndIcon4;
    @BindView(R.id.tvForeCastTempAndIcon5)
    TextView tvForeCastTempAndIcon5;
    @BindView(R.id.tvForecasrDescriprion1)
    TextView tvForecasrDescriprion1;
    @BindView(R.id.tvForecasrDescriprion2)
    TextView tvForecasrDescriprion2;
    @BindView(R.id.tvForecasrDescriprion3)
    TextView tvForecasrDescriprion3;
    @BindView(R.id.tvForecasrDescriprion4)
    TextView tvForecasrDescriprion4;
    @BindView(R.id.tvForecasrDescriprion5)
    TextView tvForecasrDescriprion5;
    @BindView(R.id.adView)
    AdView mAdView;

    private SharedPreferences.Editor editor;
    ArrayList<Weather5dayesForecastModel> foreCast_5_Days = new ArrayList<>();
    ProgressDialog progressDialog;

    public WeatherNowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();

        Url = "http://api.openweathermap.org/data/2.5/weather?lang=ar&units=metric&lat=" + prefs.getFloat("lat", 0) +
                "&lon=" + prefs.getFloat("lon", 0)
                + "&APPID=" + ApiKey;

        UrlForeCast_5_Days = "http://api.openweathermap.org/data/2.5/forecast?lang=ar&units=metric&lat=" + prefs.getFloat("lat", 0) +
                "&lon=" + prefs.getFloat("lon", 0)
                + "&APPID=" + ApiKey;

        Log.e("lat", String.valueOf(prefs.getFloat("lat", 0)));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_weather_now, container, false);
        ButterKnife.bind(this, view);
        ads();
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_DARK);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// set indeterminate style
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("جارى التحميل...");
        progressDialog.show();
        getWeatherNow();
        getForeCastWeather_5_Days();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.weather_drawer, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            if (checkGPS()) {
                ads();
                startActivity(new Intent(getActivity().getApplicationContext(), MapsActivity.class));
            } else {
                showGPSDisabledAlertToUser();
            }
            //getWeatherNow();
            //getForeCastWeather_5_Days();
            return true;
        }

        return false;
    }

    void getWeatherNow() {
        StringRequest weatherrequest = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    long js = jsonObject.length();
                    Log.i("Json Length", String.valueOf(js));
                    final JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    for (int i = 0; i < weatherArray.length(); i++) {
                        JSONObject currentObject = weatherArray.getJSONObject(i);
                        String description = currentObject.getString("description");
                        editor.putString("description", description);
                        editor.putString("icon", currentObject.getString("icon"));
                    }

                    JSONObject main = jsonObject.getJSONObject("main");
                    int temp = main.getInt("temp");
                    Log.e("Temp", String.valueOf(temp));

                    int hum = main.getInt("humidity");
                    int pressure = main.getInt("pressure");
                    if (prefs.getBoolean("is visibility", true)) {
                        int visibility = jsonObject.getInt("visibility") / 1000;
                        editor.putInt("visibility", visibility);
                    }
                    String nameAddress = jsonObject.getString("name");
                    editor.putInt("temp", temp);
                    editor.putInt("humidity", hum);
                    editor.putInt("pressure", pressure);
                    editor.putString("address", nameAddress);

                    JSONObject wind = jsonObject.getJSONObject("wind");
                    float speedWind = (float) (wind.getDouble("speed") * 3600) / 1000;
                    editor.putFloat("speedWind", speedWind);
                    JSONObject sys = jsonObject.getJSONObject("sys");
                    int sunRise = sys.getInt("sunrise");
                    int sunSet = sys.getInt("sunset");
                    DateTime dateTimeSunrise = new DateTime(sunRise * 1000L);
                    DateTime dateTimeSunset = new DateTime(sunSet * 1000L);
                    int hourSunrise = dateTimeSunrise.getHourOfDay();
                    int minuteSunrise = dateTimeSunrise.getMinuteOfHour();
                    int hourSunset = dateTimeSunset.getHourOfDay() - 12;
                    int minuteSunset = dateTimeSunset.getMinuteOfHour();
                    Log.v("SunSet", String.valueOf(hourSunset));
                    Log.v("SunRise", String.valueOf(hourSunrise));

                    editor.putInt("hourRise", hourSunrise);
                    editor.putInt("minuteRise", minuteSunrise);
                    editor.putInt("hourSet", hourSunset);
                    editor.putInt("minuteSet", minuteSunset);
                    Locale locale = new Locale("ar");
                    DateFormat forFormat = new SimpleDateFormat("yyyy/MM/dd" + " " + "h:mm a", locale);
                    String time = forFormat.format(Calendar.getInstance().getTime());
                    editor.putString("time", time);
                    editor.apply();
                    try {
                        Glide.with(getActivity().getApplicationContext())
                                .asBitmap()
                                .load("http://openweathermap.org/img/w/" + prefs.getString("icon", null) + ".png")
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new SimpleTarget<Bitmap>(150, 150) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                tvDescriptionAndWeatherIcon.setCompoundDrawablesWithIntrinsicBounds(null,
                                        new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                                        null, null);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                    //Locale locale = new Locale("ar");
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE", locale);
                    Date d = new Date();
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", locale);
                    String formattedDate = df.format(c);
                    String dayOfTheWeek = sdf.format(d);
                    tvDatAndDay.setText(dayOfTheWeek);
                    tvDatAndDay.append(" " + formattedDate);
                    tvTemp.setText("℃" + String.valueOf(prefs.getInt("temp", 1)));
                    tvDescriptionAndWeatherIcon.setText(prefs.getString("description", null));
                    tvHumidity.setText("الرطوبة:" + String.valueOf(prefs.getInt("humidity", 0)) + " %");
                    tvVisibility.setText("مدى الرؤية:" + String.valueOf(prefs.getInt("visibility", 0)) + " كم");
                    tvPressure.setText("الضغط:" + "hPa " + String.valueOf(prefs.getInt("pressure", 0)));
                    tvWindSpeed.setText("سرعة الرياح:" + String.valueOf(prefs.getFloat("speedWind", 0)) + " كم/س");
                    tvSunRise.setText("شروق الشمس:" + String.valueOf(prefs.getInt("hourSet", 0)));
                    tvSunRise.append(":" + String.valueOf(prefs.getInt("minuteSet", 0)) + "ص");

                    tvSunSet.setText("غروب الشمس:" + String.valueOf(prefs.getInt("hourRise", 0)));
                    tvSunSet.append(":" + String.valueOf(prefs.getInt("minuteRise", 0)) + "م");
                    tvAddress.setText(prefs.getString("address", null));
                    tvLastUpdate.setText("تم التحديث" + prefs.getString("time", null));

                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                try {
                    Glide.with(getActivity().getApplicationContext())
                            .asBitmap()
                            .load("http://openweathermap.org/img/w/" + prefs.getString("icon", null) + ".png")
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            tvDescriptionAndWeatherIcon.setCompoundDrawablesWithIntrinsicBounds(null,
                                    new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                                    null, null);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvTemp.setText("℃" + String.valueOf((int) prefs.getInt("temp", 1)));
                tvDescriptionAndWeatherIcon.setText(prefs.getString("description", null));

                tvHumidity.setText("الرطوبة:" + String.valueOf(prefs.getInt("humidity", 0)));
                tvPressure.setText("الضغط :" + String.valueOf(prefs.getInt("pressure", 0)));
                tvVisibility.setText("مدى الرؤية:" + String.valueOf(prefs.getInt("visibility", 0)));
                tvWindSpeed.setText("سرعة الرياح:" + String.valueOf(prefs.getFloat("speedWind", 0)));
                tvSunRise.setText("شروق الشمس:" + String.valueOf(prefs.getInt("hourSet", 0)));
                tvSunRise.append(":" + String.valueOf(prefs.getInt("minuteSet", 0)) + "ص");
                tvSunSet.setText("غروب الشمس:" + String.valueOf(prefs.getInt("hourRise", 0)));
                tvSunSet.append(":" + String.valueOf(prefs.getInt("minuteRise", 0)) + "م");
                tvAddress.setText(prefs.getString("address", null));
                tvLastUpdate.setText(prefs.getString("time", null));

                Toast.makeText(getActivity(), "لا يوجد اتصال بالانترنت  !!!", Toast.LENGTH_SHORT).show();
            }
        });

        weatherrequest.setShouldCache(false);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(weatherrequest);
    }


    void getForeCastWeather_5_Days() {

        StringRequest weatherForecastRequest = new StringRequest(Request.Method.GET, UrlForeCast_5_Days, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray weatherForecastArray = jsonObject.getJSONArray("list");
                    Log.e("Length", String.valueOf(weatherForecastArray.length()));
                    for (int i = 0; i < weatherForecastArray.length(); i++) {
                        Weather5dayesForecastModel foreCastModelObject = new Weather5dayesForecastModel();
                        JSONObject currentObjectForecast = weatherForecastArray.getJSONObject(i);
                        JSONObject main = currentObjectForecast.getJSONObject("main");
                        JSONObject wind = currentObjectForecast.getJSONObject("wind");
                        JSONArray weatherArray = currentObjectForecast.getJSONArray("weather");
                        for (int j = 0; j < weatherArray.length(); j++) {
                            JSONObject currentObject = weatherArray.getJSONObject(j);
                            descriptionForecast_5_Days = currentObject.getString("description");
                            iconForecast_5_Days = currentObject.getString("icon");
                        }

                        Locale locale = new Locale("ar");
                        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", locale);
                        String dt_txt = currentObjectForecast.getString("dt_txt");//date and time from api
                        String date_txt = dt_txt.substring(0, dt_txt.indexOf(" ")); // get just date without time to get day name.
                        int subTime = Integer.parseInt(dt_txt.substring(dt_txt.indexOf(" ") + 1, dt_txt.indexOf(":")));// to get the time
                        Date date = null;
                        try {
                            date = inFormat.parse(date_txt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String dayName = outFormat.format(date);
                        int temp = main.getInt("temp");
                        float hum = (float) main.getDouble("humidity");
                        float pressure = (float) main.getDouble("pressure");
                        float windSpeed = (float) wind.getDouble("speed");
                        foreCastModelObject.setDescription(descriptionForecast_5_Days);
                        foreCastModelObject.setIcon(iconForecast_5_Days);
                        foreCastModelObject.setDt_txt(dt_txt);
                        foreCastModelObject.setDayName(dayName);
                        foreCastModelObject.setTemp(temp);
                        foreCastModelObject.setHumidity(hum);
                        foreCastModelObject.setWindSpeed(windSpeed);
                        foreCastModelObject.setPressure(pressure);
                        if (subTime == 12) {
                            foreCastModelObject.setIcon(iconForecast_5_Days);
                            foreCastModelObject.setDayName(dayName);
                            foreCastModelObject.setTemp(temp);
                            foreCast_5_Days.add(foreCastModelObject);
                        }
                    }

                    for (int i = 0; i < foreCast_5_Days.size(); i++) {

                        Log.e("ForeCast_5_DaysNow", foreCast_5_Days.get(i).toString());
                        editor.putString("dayForecast" + i, foreCast_5_Days.get(i).getDayName());
                        Log.d("dayForecast" + i, foreCast_5_Days.get(i).getDayName());
                        editor.putInt("tempForecast" + i, foreCast_5_Days.get(i).getTemp());
                        editor.putString("descriptionForecast" + i, foreCast_5_Days.get(i).getDescription());
                        editor.putString("iconForecast" + i, foreCast_5_Days.get(i).getIcon());
                        editor.apply();
                        editor.commit();
                    }
                    setForecastViewData();

                } catch (JSONException e) {
                    Log.e("JsonException", e.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (prefs.getString("dayForecast0", null) != null) {
                    setForecastViewData();
                }
            }
        });
        //TODO handle case not internet found

        //weatherForecastRequest.setShouldCache(false);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(weatherForecastRequest);
    }

    void setForecastViewData() {

        Log.d("dayForecastDay1", prefs.getString("dayForecast0", null));
        tvForecastDay1.setText(prefs.getString("dayForecast0", null));
        tvForeCastTempAndIcon1.setText("℃" + String.valueOf(prefs.getInt("temp", 0)));
        tvForecasrDescriprion1.setText(prefs.getString("description", null));

        tvForecastDay2.setText(prefs.getString("dayForecast1", null));
        tvForeCastTempAndIcon2.setText("℃" + String.valueOf(prefs.getInt("tempForecast1", 0)));
        tvForecasrDescriprion2.setText(prefs.getString("descriptionForecast1", null));

        tvForecastDay3.setText(prefs.getString("dayForecast2", null));
        tvForeCastTempAndIcon3.setText("℃" + String.valueOf(prefs.getInt("tempForecast2", 0)));
        tvForecasrDescriprion3.setText(prefs.getString("descriptionForecast2", null));

        tvForecastDay4.setText(prefs.getString("dayForecast3", null));
        tvForeCastTempAndIcon4.setText("℃" + String.valueOf(prefs.getInt("tempForecast3", 0)));
        tvForecasrDescriprion4.setText(prefs.getString("descriptionForecast3", null));

        tvForecastDay5.setText(prefs.getString("dayForecast4", null));
        tvForeCastTempAndIcon5.setText("℃" + String.valueOf(prefs.getInt("tempForecast4", 0)));
        tvForecasrDescriprion5.setText(prefs.getString("descriptionForecast4", null));

        try {
            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load("http://openweathermap.org/img/w/" + prefs.getString("iconForecast0", null) + ".png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new SimpleTarget<Bitmap>(100, 100) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    tvForeCastTempAndIcon1.setCompoundDrawablesWithIntrinsicBounds(null,
                            new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                            null, null);
                }
            });

            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load("http://openweathermap.org/img/w/" + prefs.getString("iconForecast1", null) + ".png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)).into(new SimpleTarget<Bitmap>(100, 100) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    tvForeCastTempAndIcon2.setCompoundDrawablesWithIntrinsicBounds(null,
                            new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                            null, null);
                }
            });

            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load("http://openweathermap.org/img/w/" + prefs.getString("iconForecast2", null) + ".png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            tvForeCastTempAndIcon3.setCompoundDrawablesWithIntrinsicBounds(null,
                                    new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                                    null, null);
                        }
                    });

            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load("http://openweathermap.org/img/w/" + prefs.getString("iconForecast3", null) + ".png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            tvForeCastTempAndIcon4.setCompoundDrawablesWithIntrinsicBounds(null,
                                    new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource),
                                    null, null);
                        }
                    });

            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load("http://openweathermap.org/img/w/" + prefs.getString("iconForecast4", null) + ".png")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            tvForeCastTempAndIcon5.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(tvDescriptionAndWeatherIcon.getResources(), resource), null, null);
                        }

                    });

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    private boolean checkGPS() {
        return ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showGPSDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
        alertDialogBuilder.create();

        alertDialogBuilder.setMessage("خدمة تحديد المواقع معطلة هل تريد تفيعلها الآن ؟")
                .setCancelable(false)
                .setPositiveButton("نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                //startActivity(callGPSSettingIntent);
                                startActivityForResult(callGPSSettingIntent, 111);

                            }
                        });
        alertDialogBuilder.setNegativeButton("لا",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getWeatherNow();
                        getForeCastWeather_5_Days();
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            startActivity(new Intent(getActivity().getApplicationContext(), MapsActivity.class));
        }
    }

    private void ads() {
        MobileAds.initialize(this.getActivity(), "ca-app-pub-9247071754231219~9842647610");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}

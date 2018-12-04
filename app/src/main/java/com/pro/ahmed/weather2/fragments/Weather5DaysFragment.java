package com.pro.ahmed.weather2.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.adapters.ForecastAdapter;
import com.pro.ahmed.weather2.model.MySingleton;
import com.pro.ahmed.weather2.model.Weather5dayesForecastModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Weather5DaysFragment extends Fragment {

    @BindView(R.id.rvForecast5DaysDetails)
    RecyclerView rvForecast5DaysDetails;
    @BindView(R.id.adView)
    AdView mAdView;
    private static final String ApiKey = "464c9a8ca5afb26e953d3e2eaca13426";
    SharedPreferences prefs;
    String Url = "";
    String description;
    String icon;
    List<Weather5dayesForecastModel> foreCastDetails = new ArrayList<>();
    ForecastAdapter adapter;
    ProgressDialog progressDialog;

    public Weather5DaysFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Url = "http://api.openweathermap.org/data/2.5/forecast?lang=ar&units=metric&lat=" + prefs.getFloat("lat", 0) +
                "&lon=" + prefs.getFloat("lon", 0)
                + "&APPID=" + ApiKey;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_weather5_days, container, false);
        ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_DARK);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("جارى التحميل...");
        progressDialog.show();
        getForeCastWeather();
        MobileAds.initialize(this.getActivity(), "ca-app-pub-9247071754231219~9842647610");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    void getForeCastWeather() {
        StringRequest weatherForecastRequest = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
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
                            description = currentObject.getString("description");
                            icon = currentObject.getString("icon");
                        }

                        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                        String dt_txt = currentObjectForecast.getString("dt_txt");//date and time from api
                        String date_txt = dt_txt.substring(0, dt_txt.indexOf(" ")); // get just date without time to get day name.
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
                        foreCastModelObject.setDescription(description);
                        foreCastModelObject.setIcon(icon);
                        foreCastModelObject.setDt_txt(dt_txt);
                        foreCastModelObject.setDayName(dayName);
                        foreCastModelObject.setTemp(temp);
                        foreCastModelObject.setHumidity(hum);
                        foreCastModelObject.setWindSpeed(windSpeed);
                        foreCastModelObject.setPressure(pressure);
                        foreCastDetails.add(foreCastModelObject);
                    }
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
                    rvForecast5DaysDetails.setHasFixedSize(true);
                    rvForecast5DaysDetails.setLayoutManager(llm);
                    adapter = new ForecastAdapter(getActivity().getApplicationContext(), foreCastDetails);
                    //ToDO create recycler view and forecast fragment layout and complete weather now fragment forecast
                    for (int i = 0; i < foreCastDetails.size(); i++) {
                        Log.e("ForeCastDetails", foreCastDetails.get(i).toString());
                    }
                    rvForecast5DaysDetails.setAdapter(adapter);

                } catch (JSONException e) {
                    Log.e("JsonException", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "لا يوجد اتصال بالانترنت  !!!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public void deliverError(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Cache.Entry entry = this.getCacheEntry();
                    if (entry != null) {
                        Response<String> response = parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
                        deliverResponse(response.result);
                        return;
                    }
                }
                super.deliverError(error);
            }
        };
        //weatherForecastRequest.setShouldCache(true);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(weatherForecastRequest);
    }
}

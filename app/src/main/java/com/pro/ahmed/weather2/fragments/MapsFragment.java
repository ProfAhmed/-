
package com.pro.ahmed.weather2.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.pro.ahmed.weather2.R;
import com.pro.ahmed.weather2.model.MySingleton;
import com.pro.ahmed.weather2.model.Weather5dayesForecastModel;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private RadioGroup rGroup;
    ProgressDialog progressDialog;
    Weather5dayesForecastModel weatherInfo;
    private AdView mAdView;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        mAdView = (AdView) v.findViewById(R.id.adView);
        ads();
        // This will get the radiogroup
        rGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        // This will get the radiobutton in the radiogroup that is checked
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        return v;
    }

    private void ads() {
        MobileAds.initialize(this.getActivity(), "ca-app-pub-9247071754231219~9842647610");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                switch (checkedId) {
                    case R.id.temMap:
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked) {
                            mMap.clear();
                            getWeatherInfo(mMap);
                            ads();
                            // Changes the textview's text to "Checked: example radiobutton text"
                            Toast.makeText(getActivity().getApplicationContext(), "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.windMap:
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked) {
                            mMap.clear();
                            tile("wind_new");
                            ads();
                            // Changes the textview's text to "Checked: example radiobutton text"
                            Toast.makeText(getActivity().getApplicationContext(), "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.cloudMap:
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked) {
                            mMap.clear();
                            tile("clouds_new");
                            ads();
                            // Changes the textview's text to "Checked: example radiobutton text"
                            Toast.makeText(getActivity().getApplicationContext(), "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.PrecipitationMap:
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked) {
                            mMap.clear();
                            tile("Precipitation_new");
                            ads();
                            // Changes the textview's text to "Checked: example radiobutton text"
                            Toast.makeText(getActivity().getApplicationContext(), "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.PressureMap:
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked) {
                            mMap.clear();
                            tile("pressure_new");
                            ads();
                            // Changes the textview's text to "Checked: example radiobutton text"
                            Toast.makeText(getActivity().getApplicationContext(), "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    private void tile(String type) {
        TileOverlay mTile;
        final String WEATHER_URL = "https://tile.openweathermap.org/map/" + type + "/%d/%d/%d.png?APPID=464c9a8ca5afb26e953d3e2eaca13426";
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.US, WEATHER_URL, zoom, x, reversedY);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        mTile = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        mTile.setTransparency(0.5f);
    }

    void getWeatherInfo(final GoogleMap mMap) {
        final String Url = "https://api.openweathermap.org/data/2.5/box/city?bbox=-160,-160,160,160,3&appid=464c9a8ca5afb26e953d3e2eaca13426";
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("جارى التحميل...");

        progressDialog.show();

        StringRequest weatherForecastRequest = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            public List<Weather5dayesForecastModel> weatherInfoS = new ArrayList<>();

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray weatherForecastArray = jsonObject.getJSONArray("list");
                    Log.e("Length", String.valueOf(weatherForecastArray.length()));
                    for (int i = 0; i < weatherForecastArray.length(); i++) {
                        JSONObject currentObjectForecast = weatherForecastArray.getJSONObject(i);
                        JSONObject main = currentObjectForecast.getJSONObject("main");
                        JSONObject coord = currentObjectForecast.getJSONObject("coord");
                        JSONObject wind = currentObjectForecast.getJSONObject("wind");
                        String cityName = currentObjectForecast.getString("name");

                        int temp = main.getInt("temp");
                        double lat = coord.getDouble("Lat");
                        double lon = coord.getDouble("Lon");
                        float hum = (float) main.getDouble("humidity");
                        float pressure = (float) main.getDouble("pressure");
                        float windSpeed = (float) wind.getDouble("speed");
                        JSONArray weatherArray = currentObjectForecast.getJSONArray("weather");
                        String description = null;
                        String icon = null;
                        for (int j = 0; j < weatherArray.length(); j++) {
                            JSONObject currentObject = weatherArray.getJSONObject(j);
                            description = currentObject.getString("description");
                            icon = currentObject.getString("icon");
                        }
                        weatherInfo = new Weather5dayesForecastModel(cityName, description, icon, lat, lon, pressure,
                                hum, windSpeed, temp);
                        weatherInfoS.add(weatherInfo);
                    }
                    new DownloadMarkerTask(getActivity()).execute(weatherInfoS);
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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(weatherForecastRequest);
    }

    public static Bitmap createCustomMarker(Context context, String icon, String cityName, int temp) {

        final TextView tvCitName, tvTemp;
        ImageView ivIcon;
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        tvCitName = (TextView) marker.findViewById(R.id.tvCityNameCustomMarker);
        tvTemp = (TextView) marker.findViewById(R.id.tvTempCustomMarker);
        ivIcon = (ImageView) marker.findViewById(R.id.ivIconCustomMarker);

        tvCitName.setText(cityName);
        tvTemp.setText("℃" + String.valueOf(temp));
        ivIcon.setImageDrawable(context.getResources().getDrawable(context.getResources()
                .getIdentifier("a" + icon, "drawable", context.getPackageName())));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        Weather5dayesForecastModel weatherInfoModel;
        Marker marker;

        CustomInfoWindowAdapter(Marker marker, Weather5dayesForecastModel weatherInfoModel) {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
            this.weatherInfoModel = weatherInfoModel;
            this.marker = marker;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow, weatherInfoModel);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view, Weather5dayesForecastModel weatherInfoModel) {

            weatherInfoModel = (Weather5dayesForecastModel) marker.getTag();
            TextView tvPressure;
            TextView tvHumidity;
            TextView tvwindSpeed;
            tvPressure = (TextView) view.findViewById(R.id.tvPressureInfoContent);
            tvHumidity = (TextView) view.findViewById(R.id.tvHumidityInfoContent);
            tvwindSpeed = (TextView) view.findViewById(R.id.tvWindSpeedInfoContent);

            tvPressure.setTextDirection(View.TEXT_DIRECTION_LTR);

            tvPressure.setText("hPa " + weatherInfoModel.getPressure());
            tvHumidity.setText("%" + weatherInfoModel.getHumidity());
            tvwindSpeed.setText("km/h " + weatherInfoModel.getWindSpeed());
        }
    }

    private class DownloadMarkerTask extends AsyncTask<List<Weather5dayesForecastModel>, Weather5dayesForecastModel, Void> {
        Context mContext;
        SharedPreferences prefs;

        DownloadMarkerTask(Context mContext) {
            this.mContext = mContext;
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Void doInBackground(List<Weather5dayesForecastModel>... weatherInfos) {
            for (int i = 0; i < weatherInfos[0].size(); i++) {
                Weather5dayesForecastModel weatherInfo = weatherInfos[0].get(i);
                LatLng customLatLng = new LatLng(weatherInfo.getLat(), weatherInfo.getLng());
                weatherInfo.setmPoistion(customLatLng);
                try {
                    Bitmap bitmap = Glide.with(mContext)
                            .asBitmap()
                            .load(createCustomMarker(mContext, weatherInfo.getIcon(), weatherInfo.getCityName(), weatherInfo.getTemp()))
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .submit(150, 150)
                            .get();
                    weatherInfo.setBitmap(bitmap);
                    publishProgress(weatherInfo);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onProgressUpdate(Weather5dayesForecastModel... weatherInfo) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(weatherInfo[0].getmPoistion())
                    .title(weatherInfo[0].getCityName())
                    .icon(BitmapDescriptorFactory.fromBitmap(weatherInfo[0].getBitmap())));

            marker.setSnippet(weatherInfo[0].getIcon());
            marker.setTag(weatherInfo[0]);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(marker, weatherInfo[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            LatLng latLng = new LatLng(prefs.getFloat("lat", 0), prefs.getFloat("lon", 0));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 4);
            mMap.animateCamera(cameraUpdate);
        }
    }
}

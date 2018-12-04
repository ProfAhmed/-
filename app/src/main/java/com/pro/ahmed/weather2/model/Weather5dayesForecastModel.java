package com.pro.ahmed.weather2.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Weather5dayesForecastModel {
    private String description;
    private String icon;
    private String dt_txt;
    private String dayName;
    private String cityName;
    private double lat, lng;
    private float pressure;
    private float humidity;
    private float windSpeed;
    private int temp;
    private LatLng mPoistion;
    Bitmap bitmap;

    public Weather5dayesForecastModel() {
    }

    public Weather5dayesForecastModel(String cityName, String description, String icon, double lat, double lng, float pressure,
                                      float humidity, float windSpeed, int temp) {
        this.cityName = cityName;
        this.description = description;
        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.temp = temp;
        mPoistion = new LatLng(lat, lng);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public String getDayName() {
        return dayName;
    }

    public int getTemp() {
        return temp;
    }

    public float getPressure() {
        return pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public String getCityName() {
        return cityName;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public LatLng getmPoistion() {
        return mPoistion;
    }

    public void setmPoistion(LatLng mPoistion) {
        this.mPoistion = mPoistion;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Weather5dayesForecastModel{" +
                "description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", dt_txt='" + dt_txt + '\'' +
                ", dayName='" + dayName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                ", temp=" + temp +
                '}';
    }
}

package com.example.cweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ka on 2017/7/2.
 */

public class Weather {
    public String status;  //状态
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;
}

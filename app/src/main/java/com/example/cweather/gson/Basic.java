package com.example.cweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ka on 2017/7/2.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}

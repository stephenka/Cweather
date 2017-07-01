package com.example.cweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ka on 2017/7/1.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int paovinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getPaovinceId() {
        return paovinceId;
    }

    public void setPaovinceId(int paovinceId) {
        this.paovinceId = paovinceId;
    }
}

package com.example.cweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ka on 2017/7/2.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    @SerializedName("drsg")
    public ClothWear clothWear;
    public Flu flu;
    @SerializedName("trav")
    public Travel travel;

    public Sport sport;

    public Uv uv;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    public class ClothWear {
        @SerializedName("txt")
        public String info;

    }
    public class Flu {
        @SerializedName("txt")
        public String info;

    }
    public class Travel {
        @SerializedName("txt")
        public String info;

    }

    public class Uv {
        @SerializedName("txt")
        public String info;

    }
}

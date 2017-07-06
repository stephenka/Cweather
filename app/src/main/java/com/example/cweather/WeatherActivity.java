package com.example.cweather;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cweather.gson.Forecast;
import com.example.cweather.gson.Weather;
import com.example.cweather.util.HttpUtil;
import com.example.cweather.util.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public static int SUGGESTION=1;
    public static String mId;

    public DrawerLayout drawerLayout;
    private Button navButton;

    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    private ScrollView weatherLayout;
    private LinearLayout suggestionLayout;
    private Button suggestionButton;

    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView clothText;
    private TextView fluText;
    private TextView travelText;
    private TextView uvText;

    private ImageView bingPicImg;
    private ImageView testImg;
    private Button albumButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);


        //初始化
        bingPicImg= (ImageView) findViewById(R.id.bing_pic_img);

        testImg= (ImageView) findViewById(R.id.iv_test);



        suggestionButton= (Button) findViewById(R.id.suggest_bt);
        suggestionLayout= (LinearLayout) findViewById(R.id.suggest_layout);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton= (Button) findViewById(R.id.nav_button);
        albumButton = (Button)findViewById(R.id.album_bt);

        weatherLayout= (ScrollView) findViewById(R.id.weather_layout);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdateTime= (TextView) findViewById(R.id.title_update_time);
        degreeText= (TextView) findViewById(R.id.degree_text);
        weatherInfoText= (TextView) findViewById(R.id.weather_info_text);
        forecastLayout= (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText= (TextView) findViewById(R.id.aqi_text);
        pm25Text= (TextView) findViewById(R.id.pm25_text);
        comfortText= (TextView) findViewById(R.id.comfort_text);
        carWashText= (TextView) findViewById(R.id.car_wash_text);
        sportText= (TextView) findViewById(R.id.sport_text);
        clothText= (TextView) findViewById(R.id.cloth_text);
        fluText= (TextView) findViewById(R.id.flu_text);
        travelText= (TextView) findViewById(R.id.travel_text);
        uvText= (TextView) findViewById(R.id.uv_text);

        swipeRefresh= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                requestWeather(mId);
            }
        });

        String bingpic = prefs.getString("bing_pic",null);
        if(bingpic!=null){
            Glide.with(this).load(bingpic).into(bingPicImg);
            Glide.with(this).load(bingpic).placeholder(R.mipmap.logo).into(bingPicImg);
            Glide.with(this).load(bingpic).error(R.drawable.start).into(bingPicImg);
            Glide.with(WeatherActivity.this).load("https://www.baidu.com/img/bd_logo1.png").into(testImg);
        }else{
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        suggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SUGGESTION==1){
                    SUGGESTION=0;
                    //Glide.with(WeatherActivity.this).load(R.drawable.ic_action_control_point).into(suggestionButton);
                    suggestionButton.setBackgroundResource(R.drawable.ic_action_control_point);
                    suggestionLayout.setVisibility(View.GONE);

                }else{
                    SUGGESTION=1;
                    suggestionButton.setBackgroundResource(R.drawable.ic_action_remove_circle_outline);
                    suggestionLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
		        /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
		        /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
		        /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }

        });
    }

    private void loadBingPic() {
        //TODO
        String requestBingPic ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        Glide.with(WeatherActivity.this).load(bingPic).error(R.drawable.start).into(bingPicImg);

                    }
                });

            }
        });

    }

    private void showWeatherInfo(Weather weather) {
        if (weather!=null&&"ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime=weather.basic.update.updateTime.split(" ")[1];
            String degree=weather.now.temperature+"℃";
            String weatherInfo=weather.now.more.info;

            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

            forecastLayout.removeAllViews();
            for (Forecast forecast:weather.forecastList){
                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
                TextView dateText= (TextView) view.findViewById(R.id.date_text);
                TextView infoText= (TextView) view.findViewById(R.id.info_text);
                TextView maxText= (TextView) view.findViewById(R.id.max_text);
                TextView minText= (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }

            if (weather.aqi!=null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort="舒适度："+weather.suggestion.comfort.info;
            String carWash="洗车指数："+ weather.suggestion.carWash.info;
            String sport="运动建议：" + weather.suggestion.sport.info;
            String cloth="穿衣建议：" + weather.suggestion.clothWear.info;
            String flu="感冒指数：" + weather.suggestion.flu.info;
            String travel="旅游建议：" + weather.suggestion.travel.info;
            String uv="紫外线指数：" + weather.suggestion.uv.info;

            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            clothText.setText(cloth);
            fluText.setText(flu);
            travelText.setText(travel);
            uvText.setText(uv);
            weatherLayout.setVisibility(View.VISIBLE);
           // suggestionLayout.setVisibility(View.GONE);

            //TODO
            //Intent intent = new Intent(this, AutoUpdateService.class);
            //startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this,"showWeatherInfo.获取天气信息失败",Toast.LENGTH_SHORT).show();
        }


    }

    public void requestWeather(final String weatherId) {
        String weatherUrl="http://guolin.tech/api/weather?cityid=" +
                weatherId+"&key=3a1c612068914edb9860bac30f703d31";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"requestWeather.onFailure.获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this,"requestWeather.onResponse.weather==null.获取天气信息失败"+mId,Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        loadBingPic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
               // Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

				/* 将Bitmap设定到ImageView */
				Glide.with(WeatherActivity.this).load(uri).into(testImg);

                //testImg.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

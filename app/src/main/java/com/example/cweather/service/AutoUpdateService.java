package com.example.cweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.cweather.R;
import com.example.cweather.gson.Weather;
import com.example.cweather.util.HttpUtil;
import com.example.cweather.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        updateWeather();
        updateBingpic();
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        int tenS =10*1000;

        long triggerAtTime = SystemClock.elapsedRealtime() +tenS;
        Intent i = new Intent(this,AutoUpdateService.class);
        //Intent i2= new Intent(this, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        //PendingIntent pi2=PendingIntent.getActivity(this,0,i2,0);

        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String   tt   =   formatter.format(curDate);


        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("title")
                .setContentText(tt)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.logo))
                .build();
        NotificationManager manager2=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager2.notify(1,notification);


        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }




    //更新天气信息
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl="http://guolin.tech/api/weather?cityid=" +
                    weatherId+"&key=3a1c612068914edb9860bac30f703d31";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }

                }
            });
        }
    }


    //每日一图
    private void updateBingpic() {
        final String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingpic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingpic);
                editor.apply();

            }
        });
    }

}

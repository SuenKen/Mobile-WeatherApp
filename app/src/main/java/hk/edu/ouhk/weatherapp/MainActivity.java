package hk.edu.ouhk.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void location(View v) {
        Intent location = new Intent();
        location.setClass(this, setDefaultLocation.class);
        startActivity(location);
    }

    public void viewWeather(View v) {
        Intent weather = new Intent();
        weather.setClass(this, ViewWeather.class);
        startActivity(weather);
    }

    public void todayForcast(View v) {
        Intent today = new Intent();
        today.setClass(this, TodayForecast.class);
        startActivity(today);
    }

    public void futureForcast(View v) {
        Intent future = new Intent();
        future.setClass(this, WeekFuture.class);
        startActivity(future);
    }

    public void airPollution(View v) {
        Intent future = new Intent();
        future.setClass(this, AirPollution.class);
        startActivity(future);
    }
    public void report(View v) {
        Intent report = new Intent();
        report.setClass(this, report.class);
        startActivity(report);
    }
    public void temp_report(View v) {
        Intent report = new Intent();
        report.setClass(this, temp_report.class);
        startActivity(report);
    }
    public void exit(View v) {
        super.finish();
        finish();
        System.exit(0);
    }

}
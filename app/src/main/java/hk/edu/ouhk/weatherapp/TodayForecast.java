package hk.edu.ouhk.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodayForecast extends AppCompatActivity {
    FetchPageTask task = null;
    Getlatlon latlon = null;
    TextView weather, min_temp, max_temp, location, pressure, humidity, windSpeed, windDirection,
            windGust, cloud, RainVolume, RainVolume_Label, uvi, pop, dew_point, moon_phase,
            SnowVolume, SunriseTime, SunsetTime, feels_like_day, feels_like_night, feels_like_eve, feels_like_morn,
            SnowVolume_Label, update, MoonriseTime, MoonsetTime, morn_temp, day_temp, eve_temp, night_temp;
    String lat, lon = "";
    boolean time = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_forcast);
        weather = findViewById(R.id.today_weather);
        min_temp = findViewById(R.id.today_min_temp);
        max_temp = findViewById(R.id.today_max_temp);
        morn_temp = findViewById(R.id.today_morn_temp);
        day_temp = findViewById(R.id.today_day_temp);
        eve_temp = findViewById(R.id.today_eve_temp);
        night_temp = findViewById(R.id.today_night_temp);
        feels_like_day = findViewById(R.id.today_feels_like_day);
        feels_like_night = findViewById(R.id.today_feels_like_night);
        feels_like_eve = findViewById(R.id.today_feels_like_eve);
        feels_like_morn = findViewById(R.id.today_feels_like_morn);
        location = findViewById(R.id.today_location);
        pressure = findViewById(R.id.today_pressure);
        humidity = findViewById(R.id.today_humidity);
        windSpeed = findViewById(R.id.today_windSpeed);
        windDirection = findViewById(R.id.today_windDirection);
        windGust = findViewById(R.id.today_windGust);
        cloud = findViewById(R.id.today_clouds);
        RainVolume = findViewById(R.id.today_RainVolume);
        RainVolume_Label = findViewById(R.id.today_RainVolume_Label);
        SnowVolume = findViewById(R.id.today_SnowVolume);
        SnowVolume_Label = findViewById(R.id.today_SnowVolume_Label);
        SunriseTime = findViewById(R.id.today_SunriseTime);
        SunsetTime = findViewById(R.id.today_SunsetTime);
        MoonriseTime = findViewById(R.id.today_MoonriseTime);
        MoonsetTime = findViewById(R.id.today_MoonsetTime);
        update = findViewById(R.id.today_update);
        uvi = findViewById(R.id.today_uvi);
        pop = findViewById(R.id.today_pop);
        dew_point = findViewById(R.id.today_dew_point);
        moon_phase = findViewById(R.id.today_moon_phase);
        String url = "";

        if (getSharedPreferences("location", MODE_PRIVATE).getString("Country", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lat", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lon", "") == ""
        ) {
            if (!time) {
            AlertDialog alertDialog = new AlertDialog.Builder(TodayForecast.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent location = new Intent();
                            location.setClass(TodayForecast.this, setDefaultLocation.class);
                            startActivity(location);
                            TodayForecast.this.finish();
                        }
                    });
            alertDialog.show();
            time = true;
        }}

        if ((getSharedPreferences("location", MODE_PRIVATE)
                .getString("lat", "") != "") && (getSharedPreferences("location", MODE_PRIVATE)
                .getString("lon", "") != "")) {
            url = "http://api.openweathermap.org/data/2.5/onecall?lat=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lat", "") + "&lon=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lon", "") + "&appid=&units=metric";
        }
        if (getSharedPreferences("location", MODE_PRIVATE)
                .getString("Country", "") == "") {
            if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                task = new FetchPageTask();
                task.execute(url);
            }
        }else{
            if (latlon == null || latlon.getStatus().equals(AsyncTask.Status.FINISHED)) {
                latlon = new Getlatlon();
                latlon.execute("http://api.openweathermap.org/geo/1.0/direct?q=" + getSharedPreferences("location", MODE_PRIVATE)
                        .getString("Country", "") + "&appid=");
            }

        }
    }


    private class FetchPageTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... values) {
            InputStream inputStream = null;
            String result = "";
            URL url = null;

            try {
                url = new URL(values[0]);
                HttpURLConnection con = (HttpURLConnection)
                        url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                    result += line;

                Log.d("doInBackground", "get data complete");
                inputStream.close();

            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                String icon;
                JSONObject jobj = new JSONObject(result);
                JSONArray weatherlist = new JSONArray(jobj.getString("daily"));
                JSONObject today = weatherlist.getJSONObject(0);
                JSONObject temp = today.getJSONObject("temp");
                JSONObject feels_like = today.getJSONObject("feels_like");
                JSONArray weatherarray = today.getJSONArray("weather");
                String snow = "";
                String rain = "";
                if (today.has("rain")) {
                    RainVolume.setText(today.getString("rain") + "mm");
                } else {
                    RainVolume_Label.setVisibility(View.GONE);
                    RainVolume.setVisibility(View.GONE);
                }
                if (today.has("snow")) {
                    SnowVolume.setText(today.getString("snow") + "mm");
                } else {
                    SnowVolume_Label.setVisibility(View.GONE);
                    SnowVolume.setVisibility(View.GONE);
                }
                icon = weatherarray.getJSONObject(0).getString("icon");
                new DownloadImageTask((ImageView) findViewById(R.id.today_weatherIcon)).execute("http://openweathermap.org/img/wn/" + icon + "@4x.png");
                weather.setText(weatherarray.getJSONObject(0).getString("main"));
                humidity.setText(today.getString("humidity") + "%");
                Integer timwzone = jobj.getInt("timezone_offset") / 3600;
                Date updateTime = new java.util.Date(today.optInt("dt") * 1000L);
                Date Sunrise = new java.util.Date(today.optInt("sunrise") * 1000L);
                Date Sunset = new java.util.Date(today.optInt("sunset") * 1000L);
                Date Moonrise = new java.util.Date(today.optInt("moonrise") * 1000L);
                Date Moonset = new java.util.Date(today.optInt("moonset") * 1000L);
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss(z)");
                if (timwzone > 0) {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+" + timwzone));
                } else {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT" + timwzone));
                }
                update.setText(sdf.format(updateTime));
                SunriseTime.setText(sdf.format(Sunrise));
                SunsetTime.setText(sdf.format(Sunset));
                MoonriseTime.setText(sdf.format(Moonrise));
                MoonsetTime.setText(sdf.format(Moonset));
                location.setText(jobj.getString("timezone"));
                min_temp.setText(temp.getString("min") + "°C");
                max_temp.setText(temp.getString("max") + "°C");

                day_temp.setText(temp.getString("day") + "°C");
                night_temp.setText(temp.getString("night") + "°C");
                eve_temp.setText(temp.getString("eve") + "°C");
                morn_temp.setText(temp.getString("morn") + "°C");
                pressure.setText(today.optString("pressure") + " hPa");
                windSpeed.setText(today.optString("wind_speed") + " metre/sec");
                windDirection.setText(today.optString("wind_deg") + " degrees");
                windGust.setText(today.optString("wind_gust"));
                feels_like_day.setText(feels_like.getString("day") + "°C");
                feels_like_eve.setText(feels_like.getString("eve") + "°C");
                feels_like_morn.setText(feels_like.getString("morn") + "°C");
                feels_like_night.setText(feels_like.getString("night") + "°C");
                cloud.setText(today.optString("clouds") + "％");
                pop.setText(String.valueOf(today.optDouble("pop") * 100) + "%");
                uvi.setText(today.optString("uvi"));
                dew_point.setText(today.optString("dew_point") + "°C");
                String phase = "";
                double moon = today.optDouble("moon_phase");
                if (moon == 0 || moon == 1) {
                    phase = "new moon";
                } else if (moon == 0.25) {
                    phase = "first quarter moon";
                } else if (moon == 0.5) {
                    phase = "full moon";
                } else if (moon == 0.75) {
                    phase = "last quarter moon";
                } else if (moon > 0 && moon < 0.25) {
                    phase = "waxing crescent";
                } else if (moon > 0.25 && moon < 0.5) {
                    phase = "waxing gibous";
                } else if (moon > 0.5 && moon < 0.75) {
                    phase = "waning gibous";
                } else if (moon > 0.75 && moon < 1) {
                    phase = "waning crescent', respectively";
                }
                moon_phase.setText(phase);
            } catch (Exception e) {
                weather.setText(e.getMessage());
                if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(TodayForecast.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(TodayForecast.this, setDefaultLocation.class);
                                startActivity(location);
                                TodayForecast.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }}
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView weatherIcon;

        public DownloadImageTask(ImageView weatherIcon) {
            this.weatherIcon = weatherIcon;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            weatherIcon.setImageBitmap(result);
        }
    }

    public void back(View v) {
        Intent home = new Intent();
        home.setClass(this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        super.finish();
    }


    private class Getlatlon extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... values) {
            InputStream inputStream = null;
            String result = "";
            URL url = null;

            try {
                url = new URL(values[0]);
                HttpURLConnection con = (HttpURLConnection)
                        url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                    result += line;

                Log.d("doInBackground", "get data complete");
                inputStream.close();

            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray array = new JSONArray(result);
                lon = array.getJSONObject(0).getString("lon");
                lat = array.getJSONObject(0).getString("lat");
                String url = "http://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=&units=metric";
                if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    task = new FetchPageTask();
                    task.execute(url);
                }
            } catch (Exception e) {
                weather.setText(e.getMessage());
                if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(TodayForecast.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(TodayForecast.this, setDefaultLocation.class);
                                startActivity(location);
                                TodayForecast.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }}
        }
    }


}

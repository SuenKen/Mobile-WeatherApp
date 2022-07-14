package hk.edu.ouhk.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ViewWeather extends AppCompatActivity {
    FetchPageTask task = null;
    TextView weather, min_temp, max_temp, feels_like, location, pressure, humidity, windSpeed, windDirection, windGust, visibility, cloud, current, pressureSea, pressureGround, RainVolume1h, RainVolume3h, SnowVolume1h, SnowVolume3h, SunriseTime, SunsetTime, RainVolume1h_Label, RainVolume3h_Label, SnowVolume1h_Label, SnowVolume3h_Label, update;
    boolean time = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_weather);
        weather = findViewById(R.id.weather);
        min_temp = findViewById(R.id.min_temp);
        max_temp = findViewById(R.id.max_temp);
        feels_like = findViewById(R.id.feels_like);
        location = findViewById(R.id.location);
        pressure = findViewById(R.id.pressure);
        pressureSea = findViewById(R.id.pressureSea);
        pressureGround = findViewById(R.id.pressureGround);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.windSpeed);
        windDirection = findViewById(R.id.windDirection);
        windGust = findViewById(R.id.windGust);
        visibility = findViewById(R.id.visibility);
        cloud = findViewById(R.id.clouds);
        current = findViewById(R.id.current);
        RainVolume1h = findViewById(R.id.RainVolume1h);
        RainVolume1h_Label = findViewById(R.id.RainVolume1h_Label);
        RainVolume3h = findViewById(R.id.RainVolume3h);
        RainVolume3h_Label = findViewById(R.id.RainVolume3h_Label);
        SnowVolume1h = findViewById(R.id.SnowVolume1h);
        SnowVolume1h_Label = findViewById(R.id.SnowVolume1h_Label);
        SnowVolume3h = findViewById(R.id.SnowVolume3h);
        SnowVolume3h_Label = findViewById(R.id.SnowVolume3h_Label);
        SunriseTime = findViewById(R.id.SunriseTime);
        SunsetTime = findViewById(R.id.SunsetTime);
        update = findViewById(R.id.update);
        SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
        String url = "";
        if (getSharedPreferences("location", MODE_PRIVATE).getString("Country", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lat", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lon", "") == ""
        ) {
            if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(ViewWeather.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(ViewWeather.this, setDefaultLocation.class);
                                startActivity(location);
                                ViewWeather.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }

        }

        if (getSharedPreferences("location", MODE_PRIVATE)
                .getString("Country", "") != "") {
            url = "http://api.openweathermap.org/data/2.5/weather?q=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("Country", "") + "&appid=948986c084e7b519f342475da4166a9f&units=metric";
        } else {
            url = "http://api.openweathermap.org/data/2.5/weather?lat=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lat", "") + "&lon=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lon", "") + "&appid=948986c084e7b519f342475da4166a9f&units=metric";

        }
        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task = new FetchPageTask();
            task.execute(url);
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
                JSONObject jobj = new JSONObject(result);
                JSONArray weatherlist = new JSONArray(jobj.getString("weather"));
                JSONObject main = new JSONObject(jobj.getString("main"));
                JSONObject wind = new JSONObject(jobj.getString("wind"));
                JSONObject sys = new JSONObject(jobj.getString("sys"));
                JSONObject clouds = new JSONObject();
                JSONObject rain = new JSONObject();
                JSONObject snow = new JSONObject();
                if (jobj.has("clouds")) {
                    clouds = new JSONObject(jobj.getString("clouds"));
                }
                if (jobj.has("rain")) {
                    rain = new JSONObject(jobj.getString("rain"));
                } else {
                    RainVolume1h_Label.setVisibility(View.GONE);
                    RainVolume3h_Label.setVisibility(View.GONE);
                    RainVolume1h.setVisibility(View.GONE);
                    RainVolume3h.setVisibility(View.GONE);
                }
                if (jobj.has("snow")) {
                    snow = new JSONObject(jobj.getString("snow"));
                } else {
                    SnowVolume1h_Label.setVisibility(View.GONE);
                    SnowVolume3h_Label.setVisibility(View.GONE);
                    SnowVolume1h.setVisibility(View.GONE);
                    SnowVolume3h.setVisibility(View.GONE);
                }
                // weather.setText("123");
                weather.setText(weatherlist.getJSONObject(0).optString("main"));
                String icon = weatherlist.getJSONObject(0).getString("icon");
                current.setText(main.optString("temp") + "°C");
                max_temp.setText(main.optString("temp_max") + "°C");
                min_temp.setText(main.optString("temp_min") + "°C");
                feels_like.setText(main.optString("feels_like") + "°C");
                location.setText(jobj.optString("name"));
                pressure.setText(main.optString("pressure") + " hPa");

                pressureSea.setText(main.optString("sea_level ") + "hPa");

                pressureGround.setText(main.optString("grnd_level ") + "hPa");

                humidity.setText(main.optString("humidity") + "%");
                windSpeed.setText(wind.optString("speed") + " meter/sec");
                windDirection.setText(wind.optString("deg") + " degrees");

                windGust.setText(wind.optString("gust") + " meter/sec");

                visibility.setText(jobj.optString("visibility"));
                cloud.setText(clouds.optString("all") + "%");

                RainVolume1h.setText(rain.optString("1h") + "mm");
                RainVolume3h.setText(rain.optString("3h") + "mm");
                SnowVolume1h.setText(snow.optString("1h") + "mm");
                SnowVolume3h.setText(snow.optString("3h") + "mm");
                Integer timwzone = jobj.optInt("timezone") / 3600;
                Date updateTime = new java.util.Date(jobj.optInt("dt") * 1000L);
                Date Sunrise = new java.util.Date(sys.optInt("sunrise") * 1000L);
                Date Sunset = new java.util.Date(sys.optInt("sunset") * 1000L);
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss(z)");
                if (timwzone > 0) {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+" + timwzone));
                } else {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT" + timwzone));
                }
                SunriseTime.setText(sdf.format(Sunrise));
                SunsetTime.setText(sdf.format(Sunset));
                update.setText(sdf.format(updateTime));
                new DownloadImageTask((ImageView) findViewById(R.id.weatherIcon))
                        .execute("http://openweathermap.org/img/wn/" + icon + "@4x.png");
            } catch (Exception e) {
                weather.setText(e.getMessage());
                if (!time) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ViewWeather.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent location = new Intent();
                                    location.setClass(ViewWeather.this, setDefaultLocation.class);
                                    startActivity(location);
                                    ViewWeather.this.finish();
                                }
                            });
                    alertDialog.show();
                    time = true;
                }
            }
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
}


package hk.edu.ouhk.weatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class report extends AppCompatActivity {
    int clear_sky, clouds, rain, thunderstorm, mist, snow;
    LinearLayout layout;
    FetchPageTask task = null;
    String lat, lon = "";
    Getlatlon latlon = null;
    boolean time = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report);
        layout = findViewById(R.id.relayout);


        String url = "";

        if (getSharedPreferences("location", MODE_PRIVATE).getString("Country", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lat", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lon", "") == ""
        ) {
            if (!time) {
            AlertDialog alertDialog = new AlertDialog.Builder(report.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent location = new Intent();
                            location.setClass(report.this, setDefaultLocation.class);
                            startActivity(location);
                            report.this.finish();
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
                    .getString("lon", "") + "&appid=948986c084e7b519f342475da4166a9f&units=metric";
        }
        if (getSharedPreferences("location", MODE_PRIVATE)
                .getString("Country", "") == "") {
            if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                task = new FetchPageTask();
                task.execute(url);
            }
        } else {
            if (latlon == null || latlon.getStatus().equals(AsyncTask.Status.FINISHED)) {
                latlon = new Getlatlon();
                latlon.execute("http://api.openweathermap.org/geo/1.0/direct?q=" + getSharedPreferences("location", MODE_PRIVATE)
                        .getString("Country", "") + "&appid=948986c084e7b519f342475da4166a9f");
            }

        }
        draw();
    }

    public void draw() {


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
                for (int i = 1; i < weatherlist.length(); i++) {

                    if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Clouds")) {
                        clouds++;
                    } else if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Clear")) {
                        clear_sky++;
                    }else if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Rain")||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Drizzle")) {
                        rain++;
                    }else if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Snow")) {
                        snow++;
                    }else if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Thunderstorm")) {
                        thunderstorm++;
                    }else if (weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Mist")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Smoke")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Haze")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Dust")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Fog")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Sand")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Dust")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Ash")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Squall")
                            ||weatherlist.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main").equals("Tornado")
                    ) {
                        mist++;
                    }

                }
                Panel alarmView = new Panel(getApplicationContext());
                layout.addView(alarmView);


            } catch (Exception e) {
                //weather.setText(e.getMessage());
                if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(report.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(report.this, setDefaultLocation.class);
                                startActivity(location);
                                report.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }}
        }
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
                String url = "http://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=948986c084e7b519f342475da4166a9f&units=metric";
                if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    task = new FetchPageTask();
                    task.execute(url);
                }
            } catch (Exception e) {
                //weather.setText(e.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(report.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(report.this, setDefaultLocation.class);
                                startActivity(location);
                                report.this.finish();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    public void back(View v) {
        this.finish();
    }

    class Panel extends View {

        public Panel(Context context) {
            super(context);

        }

        public void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            int a = 0;
            paint.setTextSize(50);
            for (int i = 1550; i >= 0 && a < 8; i -= 200) {
                canvas.drawText(Integer.toString(a), 0, i, paint);
                canvas.drawLine(45, i, getWidth(), i, paint);
                a += 1;
            }
            paint.setColor(Color.BLUE);
            canvas.drawRect(new Rect(150, 1550 - (clear_sky * 200), 200, 1550), paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(new Rect(450, 1550 - (clouds * 200), 500, 1550), paint);
            paint.setColor(Color.YELLOW);
            canvas.drawRect(new Rect(650, 1550 - (rain * 200), 700, 1550), paint);
            paint.setColor(Color.RED);
            canvas.drawRect(new Rect(900, 1550 - (thunderstorm * 200), 950, 1550), paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(45);
            canvas.drawText("clear sky", 100, 1650, paint);
            canvas.drawText("clouds", 400, 1650, paint);
            canvas.drawText("rain ", 650, 1650, paint);
            canvas.drawText("thunderstorm", 800, 1650, paint);
            canvas.drawText("mist", 1100, 1650, paint);
            canvas.drawText("snow", 1250, 1650, paint);


        }
    }
}

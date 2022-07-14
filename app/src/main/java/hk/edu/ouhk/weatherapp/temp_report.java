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
import java.text.SimpleDateFormat;

public class temp_report extends AppCompatActivity {
    int day1,day2,day3,day4,day5,day6,day7;
    String day1_label,day2_label,day3_label,day4_label,day5_label,day6_label,day7_label;
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
            AlertDialog alertDialog = new AlertDialog.Builder(temp_report.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent location = new Intent();
                            location.setClass(temp_report.this, setDefaultLocation.class);
                            startActivity(location);
                            temp_report.this.finish();
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
        } else {
            if (latlon == null || latlon.getStatus().equals(AsyncTask.Status.FINISHED)) {
                latlon = new Getlatlon();
                latlon.execute("http://api.openweathermap.org/geo/1.0/direct?q=" + getSharedPreferences("location", MODE_PRIVATE)
                        .getString("Country", "") + "&appid=");
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
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd");
                day1 = weatherlist.getJSONObject(0).getJSONObject("temp").getInt("max");
                day2 = weatherlist.getJSONObject(1).getJSONObject("temp").getInt("max");
                day3 = weatherlist.getJSONObject(2).getJSONObject("temp").getInt("max");
                day4 = weatherlist.getJSONObject(3).getJSONObject("temp").getInt("max");
                day5 = weatherlist.getJSONObject(4).getJSONObject("temp").getInt("max");
                day6 = weatherlist.getJSONObject(5).getJSONObject("temp").getInt("max");
                day7 = weatherlist.getJSONObject(6).getJSONObject("temp").getInt("max");
                day1_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(0).getInt("dt") * 1000L));
                day2_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(1).getInt("dt") * 1000L));
                day3_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(2).getInt("dt") * 1000L));
                day4_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(3).getInt("dt") * 1000L));
                day5_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(4).getInt("dt") * 1000L));
                day6_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(5).getInt("dt") * 1000L));
                day7_label = sdf.format(new java.util.Date(weatherlist.getJSONObject(6).getInt("dt") * 1000L));
                Panel alarmView = new Panel(getApplicationContext());
                layout.addView(alarmView);


            } catch (Exception e) {
                //weather.setText(e.getMessage());
                if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(temp_report.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(temp_report.this, setDefaultLocation.class);
                                startActivity(location);
                                temp_report.this.finish();
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
                if (task == null || task.getStatus().equals(Status.FINISHED)) {
                    task = new FetchPageTask();
                    task.execute(url);
                }
            } catch (Exception e) {
                //weather.setText(e.getMessage());
                if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(temp_report.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(temp_report.this, setDefaultLocation.class);
                                startActivity(location);
                                temp_report.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }}
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
            paint.setTextSize(40);
            for (int i = 1550; i >= 0 && a < 41; i -= 35) {
                canvas.drawText(Integer.toString(a), 0, i, paint);
                canvas.drawLine(45, i, getWidth(), i, paint);
                a += 1;
            }
            paint.setColor(Color.BLUE);
            canvas.drawRect(new Rect((getWidth()/8)-50, 1550 - (day1 * 35), (getWidth()/8)+50, 1550), paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(new Rect((getWidth()/8)*2-50, 1550 - (day2 * 35), (getWidth()/8)*2+50, 1550), paint);
            paint.setColor(Color.YELLOW);
            canvas.drawRect(new Rect((getWidth()/8)*3-50, 1550 - (day3 * 35), (getWidth()/8)*3+50, 1550), paint);
            paint.setColor(Color.RED);
            canvas.drawRect(new Rect((getWidth()/8)*4-50, 1550 - (day4 * 35), (getWidth()/8)*4+50, 1550), paint);
            paint.setColor(Color.GREEN);
            canvas.drawRect(new Rect((getWidth()/8)*5-50, 1550 - (day5 * 35), (getWidth()/8)*5+50, 1550), paint);
            paint.setColor(Color.MAGENTA);
            canvas.drawRect(new Rect((getWidth()/8)*6-50, 1550 - (day6 * 35), (getWidth()/8)*6+50, 1550), paint);
            paint.setColor(Color.CYAN);
            canvas.drawRect(new Rect((getWidth()/8)*7-50, 1550 - (day7 * 35), (getWidth()/8)*7+50, 1550), paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText(day1_label, getWidth()/13, 1650, paint);
            canvas.drawText(day2_label, (getWidth()/14)*3, 1650, paint);
            canvas.drawText(day3_label, (getWidth()/15)*5, 1650, paint);
            canvas.drawText(day4_label, (getWidth()/20)*9, 1650, paint);
            canvas.drawText(day5_label, (getWidth()/17)*10, 1650, paint);
            canvas.drawText(day6_label, (getWidth()/21)*15, 1650, paint);
            canvas.drawText(day7_label, (getWidth()/20)*17, 1650, paint);
        }
    }
}

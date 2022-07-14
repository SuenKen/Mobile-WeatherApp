package hk.edu.ouhk.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeekFuture extends AppCompatActivity {
    FetchPageTask task = null;
    Getlatlon latlon = null;
    String lat, lon = "";
    TextView future_location;
    TextView day1, day2, day3, day4, day5, day6, day7;
    boolean time = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_forecast);
        future_location = findViewById(R.id.future_location);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        day7 = findViewById(R.id.day7);


        String url = "";

        if (getSharedPreferences("location", MODE_PRIVATE).getString("Country", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lat", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lon", "") == ""
        ) {
            if (!time){
            AlertDialog alertDialog = new AlertDialog.Builder(WeekFuture.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent location = new Intent();
                            location.setClass(WeekFuture.this, setDefaultLocation.class);
                            startActivity(location);
                            WeekFuture.this.finish();
                        }
                    });
            alertDialog.show();
        }
        time = true;
        }

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
                Integer timwzone = jobj.getInt("timezone_offset") / 3600;
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");
                if (timwzone > 0) {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+" + timwzone));
                } else {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT" + timwzone));
                }
                DecimalFormat df = new DecimalFormat("##.#");
                future_location.setText(jobj.getString("timezone"));
                day1.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(1).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(1).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(1).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("main"));

                day2.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(2).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(2).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(2).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("main"));

                day3.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(3).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(3).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(3).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(3).getJSONArray("weather").getJSONObject(0).getString("main"));

                day4.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(4).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(4).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(4).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(4).getJSONArray("weather").getJSONObject(0).getString("main"));

                day5.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(5).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(5).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(5).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(5).getJSONArray("weather").getJSONObject(0).getString("main"));

                day6.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(6).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(6).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(6).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(6).getJSONArray("weather").getJSONObject(0).getString("main"));

                day7.setText(sdf.format(new java.util.Date(weatherlist.getJSONObject(7).getInt("dt") * 1000L))
                        + "|High:" + df.format(weatherlist.getJSONObject(7).getJSONObject("temp").getDouble("max")) + "°C|Low :"
                        + df.format(weatherlist.getJSONObject(7).getJSONObject("temp").getDouble("min")) + "°C\n"
                        + weatherlist.getJSONObject(7).getJSONArray("weather").getJSONObject(0).getString("main"));


            } catch (Exception e) {
                //weather.setText(e.getMessage());
                if (!time){
                AlertDialog alertDialog = new AlertDialog.Builder(WeekFuture.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(WeekFuture.this, setDefaultLocation.class);
                                startActivity(location);
                                WeekFuture.this.finish();
                            }
                        });
                alertDialog.show();
                    time = true;
            }

            }
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
                if (!time){
                AlertDialog alertDialog = new AlertDialog.Builder(WeekFuture.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(WeekFuture.this, setDefaultLocation.class);
                                startActivity(location);
                                WeekFuture.this.finish();
                            }
                        });
                alertDialog.show();
                    time = true;
            }

            }
        }
    }


    public void back(View v) {
        Intent home = new Intent();
        home.setClass(this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        super.finish();
    }

    public void day1Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 1);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day2Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 2);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day3Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 3);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day4Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 4);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day5Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 5);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day6Click(View v) {
        Intent day1Intent = new Intent();
        day1Intent.putExtra("day", 6);
        day1Intent.setClass(this, FutureDetail.class);
        startActivity(day1Intent);
        super.finish();
    }

    public void day7Click(View v) {
        Intent home = new Intent();
        home.putExtra("day", 7);
        home.setClass(this, FutureDetail.class);
        //home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        super.finish();
    }
}

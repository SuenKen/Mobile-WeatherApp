package hk.edu.ouhk.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Date;

public class AirPollution extends AppCompatActivity {
    FetchPageTask task = null;
    Getlatlon latlon = null;
    String lat, lon = "";
    TextView aqi, co, no, no2, o3, so2, pm2_5, pm10, nh3, air_date;
    Spinner chooseDate;
    int position = 0;
    boolean time = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.air_pollution);
        aqi = findViewById(R.id.aqi);
        co = findViewById(R.id.co);
        no = findViewById(R.id.no);
        no2 = findViewById(R.id.no2);
        o3 = findViewById(R.id.o3);
        so2 = findViewById(R.id.so2);
        pm2_5 = findViewById(R.id.pm2_5);
        pm10 = findViewById(R.id.pm10);
        nh3 = findViewById(R.id.nh3);
        air_date = findViewById(R.id.air_date);
        chooseDate = findViewById(R.id.chooseDate);

        String url = "";
        if (getSharedPreferences("location", MODE_PRIVATE).getString("Country", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lat", "") == "" &&
                getSharedPreferences("location", MODE_PRIVATE).getString("lon", "") == ""
        ) {
            if (!time) {
                AlertDialog alertDialog = new AlertDialog.Builder(AirPollution.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("You have not Set the Default Location ! Please Set it first");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent location = new Intent();
                                location.setClass(AirPollution.this, setDefaultLocation.class);
                                startActivity(location);
                                AirPollution.this.finish();
                            }
                        });
                alertDialog.show();
                time = true;
            }
        }

        if ((getSharedPreferences("location", MODE_PRIVATE)
                .getString("lat", "") != "") && (getSharedPreferences("location", MODE_PRIVATE)
                .getString("lon", "") != "")) {
            url = "http://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lat", "") + "&lon=" + getSharedPreferences("location", MODE_PRIVATE)
                    .getString("lon", "") + "&appid=948986c084e7b519f342475da4166a9f";
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
        String finalUrl = url;
        chooseDate.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        if (position != pos) {
                            position = pos;
                            if (getSharedPreferences("location", MODE_PRIVATE)
                                    .getString("Country", "") == "") {
                                if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                                    task = new FetchPageTask();
                                    task.execute(finalUrl);
                                }
                            } else {
                                if (latlon == null || latlon.getStatus().equals(AsyncTask.Status.FINISHED)) {
                                    latlon = new Getlatlon();
                                    latlon.execute("http://api.openweathermap.org/geo/1.0/direct?q=" + getSharedPreferences("location", MODE_PRIVATE)
                                            .getString("Country", "") + "&appid=948986c084e7b519f342475da4166a9f");
                                }

                            }
                            Object item = parent.getItemAtPosition(pos);
                            System.out.println(position);

                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


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
                JSONObject list = jobj.getJSONArray("list").getJSONObject(position);
                JSONObject components = list.getJSONObject("components");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                switch (list.getJSONObject("main").getInt("aqi")) {
                    case 1:
                        aqi.setText("Good");
                    case 2:
                        aqi.setText("Fair");
                    case 3:
                        aqi.setText("Moderate");
                    case 4:
                        aqi.setText("Poor");
                    case 5:
                        aqi.setText("Very Poor");
                }
                //Integer timwzone = jobj.getInt("timezone_offset") / 3600;
                co.setText(components.getString("co"));
                no.setText(components.getString("no"));
                no2.setText(components.getString("no2"));
                o3.setText(components.getString("o3"));
                so2.setText(components.getString("so2"));
                pm2_5.setText(components.getString("pm2_5"));
                pm10.setText(components.getString("pm10"));
                nh3.setText(components.getString("nh3"));
                air_date.setText(sdf.format(new Date(list.getInt("dt") * 1000L)));


                ArrayList<String> items = new ArrayList<>();
                ArrayList<String> date = new ArrayList<>();
                for (int i = 0; i < jobj.getJSONArray("list").length(); i++) {
                    items.add(jobj.getJSONArray("list").getJSONObject(i).getString("dt"));
                    date.add(sdf.format(new Date(jobj.getJSONArray("list").getJSONObject(i).getInt("dt") * 1000L)));
                }
                ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_style, date);
                chooseDate.setAdapter(adapter);
                chooseDate.setSelection(position);
            } catch (Exception e) {
                aqi.setText(e.getMessage());
                if (!time) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AirPollution.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent location = new Intent();
                                    location.setClass(AirPollution.this, setDefaultLocation.class);
                                    startActivity(location);
                                    AirPollution.this.finish();
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
                String url = "http://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=" + lat + "&lon=" + lon + "&appid=948986c084e7b519f342475da4166a9f";
                if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    task = new FetchPageTask();
                    task.execute(url);
                }
            } catch (Exception e) {
                //weather.setText(e.getMessage());
                if (!time) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AirPollution.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This Location is not support ! Please ReSet it first");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent location = new Intent();
                                    location.setClass(AirPollution.this, setDefaultLocation.class);
                                    startActivity(location);
                                    AirPollution.this.finish();
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
}

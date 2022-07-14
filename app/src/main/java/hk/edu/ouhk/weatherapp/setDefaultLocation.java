package hk.edu.ouhk.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class setDefaultLocation extends AppCompatActivity {
    EditText lat;
    EditText lon;
    Spinner country;
    RadioButton storeBylatlon;
    RadioButton storeBycountry;
    TextView Lat_Label,Lon_Label,selectLocation;
    Button currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_default_location);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        currentLocation = findViewById(R.id.current);
        storeBylatlon = findViewById(R.id.setBylatlon);
        storeBycountry = findViewById(R.id.setByname);
        Lat_Label = findViewById(R.id.Lat_Label);
        Lon_Label = findViewById(R.id.Lon_Label);
        selectLocation = findViewById(R.id.selectLocation);
        getSharedPreferences("location",MODE_PRIVATE);
        lat.setText(getSharedPreferences("location", MODE_PRIVATE)
                .getString("lat", ""));
        lon.setText(getSharedPreferences("location", MODE_PRIVATE)
                .getString("lon", ""));
        country = findViewById(R.id.tete);
        String[] items = getResources().getStringArray(R.array.country_arrays);
        List al = Arrays.asList(items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_style, items);
        country.setAdapter(adapter);
        country.setSelection(al.indexOf(getSharedPreferences("location", MODE_PRIVATE)
                .getString("Country", "")));
        if(getSharedPreferences("location", MODE_PRIVATE)
                .getString("Country", "")!=""){
            storeBycountry.setChecked(true);
            lat.setText("");
            lon.setText("");
            lat.setVisibility(View.GONE);
            lon.setVisibility(View.GONE);
            currentLocation.setVisibility(View.GONE);
            Lat_Label.setVisibility(View.GONE);
            Lon_Label.setVisibility(View.GONE);
        }else{
            storeBylatlon.setChecked(true);
            selectLocation.setVisibility(View.GONE);
            country.setSelection(0);
            country.setVisibility(View.GONE);
        }
    }

    public  void onClick(View v){
        SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);

        if (storeBycountry.isChecked()){
            lat.setText("");
            lon.setText("");
            if (country.getSelectedItemPosition() == 0){

                pref.edit()
                        .putString("lat", "")
                        .putString("lon", "")
                        .putString("Country", "")
                        .apply();
            }
            else{
                pref.edit()
                        .putString("lat", lat.getText().toString())
                        .putString("lon", lon.getText().toString())
                        .putString("Country", country.getSelectedItem().toString())
                        .apply();
            }
        }else if (storeBylatlon.isChecked()){
            pref.edit()
                    .putString("lat", lat.getText().toString())
                    .putString("lon", lon.getText().toString())
                    .putString("Country", "")
                    .apply();
        }


        Intent home = new Intent();
        home.setClass( this, MainActivity.class );
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( home );
        super.finish();
    }
    public void  current(View v){
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        GpsTracker gpsTracker = new GpsTracker(setDefaultLocation.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            lon.setText(String.valueOf(longitude));
            lat.setText(String.valueOf(latitude));
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    public  void setCountryOption(View v){
        lat.setText("");
        lon.setText("");
        lat.setVisibility(View.GONE);
        lon.setVisibility(View.GONE);
        Lat_Label.setVisibility(View.GONE);
        Lon_Label.setVisibility(View.GONE);
        selectLocation.setVisibility(View.VISIBLE);
        country.setVisibility(View.VISIBLE);
        currentLocation.setVisibility(View.GONE);
    }
    public  void setlatlonOption(View v){
        Lat_Label.setVisibility(View.VISIBLE);
        Lon_Label.setVisibility(View.VISIBLE);
        lat.setVisibility(View.VISIBLE);
        lon.setVisibility(View.VISIBLE);
        selectLocation.setVisibility(View.GONE);
        country.setSelection(0);
        country.setVisibility(View.GONE);
        currentLocation.setVisibility(View.VISIBLE);
    }
    public void back(View v) {
        Intent home = new Intent();
        home.setClass(this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        super.finish();
    }
}

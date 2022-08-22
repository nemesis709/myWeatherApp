package edu.skku.testapp.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String Location;
    String Weather;
    String loc;
    TextView my_location;
    TextView weatherResult;
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Location="https://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&language=ko&key=AIzaSyDH62BVTuLtnIW78SgZ93jB4J2ji_YqPp8";
            Weather="https://api.openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=400c047eb6333f9fcba21f2c03564ea8&units=metric";
            Log.d("loc",Weather);
            HttpTask1 httpTask_loc = new HttpTask1();
            httpTask_loc.execute();

            HttpTask2 httpTask_wtr = new HttpTask2();
            httpTask_wtr.execute();
        }

        @Override
        public void onStatusChanged(String Provider, int stauts, Bundle extraws){

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherResult=findViewById(R.id.weather);
    }

    class HttpTask1 extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL loc= new URL(Location);
                HttpURLConnection http= (HttpURLConnection) loc.openConnection();
                http.setRequestMethod("GET");
                http.setConnectTimeout(10*1000);
                http.setReadTimeout(10*1000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                String result = "";
                while((line = reader.readLine())!=null){
                    result=result+line;
                }
                JSONObject json = null;
                json = new JSONObject(result);
                JSONArray j1 = json.getJSONArray("results");
                JSONObject j2 = j1.getJSONObject(3);
                JSONArray j3 = j2.getJSONArray("address_components");
                String Ro = (String) j3.getJSONObject(0).get("long_name");
                String Dong = (String) j3.getJSONObject(1).get("long_name");
                String Si = (String) j3.getJSONObject(2).get("long_name");
                String loc_result = Si+" "+Dong+" "+Ro;
                stringBuffer.append(loc_result);
                Log.d("string", String.valueOf(stringBuffer));
                publishProgress(stringBuffer.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            my_location=findViewById(R.id.location);
            my_location.setText(values[0]);
        }
    }

    class HttpTask2 extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL wtr= new URL(Weather);
                HttpURLConnection http= (HttpURLConnection) wtr.openConnection();
                http.setRequestMethod("GET");
                http.setConnectTimeout(10*1000);
                http.setReadTimeout(10*1000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String line;
                String result = "";
                while((line = reader.readLine())!=null){
                    result=result+line;
                }
                JSONObject json = null;
                json = new JSONObject(result);
                JSONArray j1 = json.getJSONArray("weather");
                String j2 = (String) j1.getJSONObject(0).get("description");
                JSONObject j3 = json.getJSONObject("main");
                String temp = "현재온도 : "+ String.format("%.0f",(Double) j3.get("temp"));
                String temp_min = "최저온도 : "+ String.format("%.0f",(Double) j3.get("temp_min"));
                String temp_max = "최고온도 : "+ String.format("%.0f",(Double) j3.get("temp_max"));
                String humidity = "습도 : "+ String.format("%d",(int) j3.get("humidity"));

                String wtr_result = "<"+ j2 + ">\n\n" + temp+ "°C\n" + temp_min+ "°C\n" + temp_max+ "°C\n"+ humidity + "%";
                stringBuffer.append(wtr_result);
                Log.d("string", String.valueOf(stringBuffer));
                publishProgress(stringBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            weatherResult.setText(values[0]);
        }
    }


    private boolean checkPermission(){
        if((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&(checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1213) {
            if (!checkPermission()) {
                finish();
            }
            else{
                // init execute
                requestLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocation(){
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,locationListener);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(!checkPermission()){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},1213);
            }
            else{
                //already had
                requestLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager != null)
            locationManager.removeUpdates(locationListener);

    }

}
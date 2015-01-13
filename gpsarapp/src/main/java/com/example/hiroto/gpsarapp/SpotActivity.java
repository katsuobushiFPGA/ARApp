package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hiroto on 2014/12/26.
 */
public class SpotActivity extends Activity implements View.OnClickListener , LocationListener{
    private ImageView image;
    private TextView text;
    private Button navi;
    private Button distance;
    private int lat;
    private int lng;
    private String info;
    private LocationManager mLocationManager;//locationManager

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_spot);
        GPSCalibration();

        Intent i = getIntent();//呼び出し元のintentを取得

        String description = i.getStringExtra("description");//説明文
        String img = i.getStringExtra("image");//画像
        lat = i.getIntExtra("lat",0);//latitude get 0
        lng = i.getIntExtra("lng",0);//longitude get 0
        info = i.getStringExtra("info");

        int desid = getResources().getIdentifier(description,"string", getPackageName());//desid取得
        int imgid = getResources().getIdentifier(img,"drawable", getPackageName());//imgid取得

        image = (ImageView)findViewById(R.id.imageView);
        image.setImageResource(imgid);
        text = (TextView)findViewById(R.id.textView);
        text.setText(getResources().getString(desid));
        navi = (Button)findViewById(R.id.button_navi);
        navi.setOnClickListener(this);
        distance = (Button)findViewById(R.id.button_distance);
        distance.setOnClickListener(this);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //naviを押した時の処理
    @Override
    public void onClick(View v) {
        if(v == navi){
            Toast.makeText(this, "ナビを開始します.\n下の矢印に沿って歩いてください.", Toast.LENGTH_SHORT).show();
            GeoPoint geo = new GeoPoint(lat,lng);
            double lat_target = geo.getLatitudeE6() / 1E6;
            double lng_target = geo.getLongitudeE6() / 1E6;

            //現在地の緯度、経度の精度がデータベースと合わないので変換
            double lat_now = ((int)(nowPoint().getLatitude() * 1E6) / 1E6);
            double lng_now = ((int)(nowPoint().getLongitude() * 1E6) / 1E6);

            //ここでナビゲーションの設定をする。3Dナビの実装が必要。および2Dマップでも必要。
            routeSearch(new LatLng(lat_now, lng_now), new LatLng(lat_target,lng_target));
            finish();
        }else if(v == distance) {
            calcDistance(lat,lng,info);//現在地からの距離を計算
        }else {
            Toast.makeText(this, "No detected Button", Toast.LENGTH_SHORT).show();
        }
    }
    private Location nowPoint() {
        Location location;
        if(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }
    private void GPSCalibration() {
        mLocationManager = (LocationManager)this. getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        String providerName = mLocationManager.getBestProvider(criteria, true);

        // If no suitable provider is found, null is returned.
        if (providerName != null) {
            mLocationManager.requestLocationUpdates(providerName, 0, 0, this);
        } else {
            Toast.makeText(this, "GPS情報を取得できませんでした。\nもう一度取得します。", Toast.LENGTH_SHORT).show();
            GPSCalibration();
        }
    }
    private void calcDistance(int latitude,int longitude, String info) {
        float[] results = new float[3];
        String distance = "";
        Location location = nowPoint();
        //目的地の緯度経度を計算
        GeoPoint geo = new GeoPoint(latitude,longitude);
        double lat = geo.getLatitudeE6() / 1E6;
        double lng = geo.getLongitudeE6() / 1E6;
        //現在地の緯度、経度の精度がデータベースと合わないので変換
        double lat_now = ((int)(location.getLatitude() * 1E6) / 1E6);
        double lng_now = ((int)(location.getLongitude() * 1E6) / 1E6);

        if(location == null){
            Toast.makeText(this,"位置情報が取得できませんでした",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Location.distanceBetween(lat_now,lng_now,lat,lng,results);
            if(results != null && results.length > 0) {
                if(results[0] < 1000)
                    distance = String.valueOf((int)results[0] + "m") ;
                else
                    distance = new BigDecimal(results[0]).divide(new BigDecimal(1E3),3,BigDecimal.ROUND_HALF_UP).toString() + "km";
                Toast.makeText(this,"現在地から" + info + "までの距離" + distance ,Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this,"IllegalArgumentException" ,Toast.LENGTH_SHORT).show();
        }
    }
    //----ルート検索用メソッド----
    private void routeSearch(LatLng origin,LatLng target){
        NavigationManager.setTarget(target);
        String url = getDirectionsUrl(origin, target);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
    private String getDirectionsUrl(LatLng origin,LatLng target){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_target = "destination="+target.latitude+","+target.longitude;
        String sensor = "sensor=false";
        //パラメータ
        String parameters = str_origin+"&"+str_target+"&"+sensor + "&language=ja" + "&mode=" + NavigationManager.getTravelMode();
        //JSON指定
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        //非同期で取得
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /*parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                parseJsonpOfDirectionAPI parser = new parseJsonpOfDirectionAPI();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
    }
    //Location Listener
    //センサーオーバライドメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){}
    @Override
    public void onProviderEnabled(String provider){}
    @Override
    public void onProviderDisabled(String provider){}
    @Override
    public void onLocationChanged(Location location){}
}

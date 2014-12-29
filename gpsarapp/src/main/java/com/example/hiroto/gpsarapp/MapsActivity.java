package com.example.hiroto.gpsarapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {
    private static final int MENU_A = 0;
    private static final int MENU_B = 1;
    private static final int MENU_c = 2;
    private static boolean isNavigation = false;
    public ProgressDialog progressDialog;
    public String travelMode = "driving";//default
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);
        setUpMapIfNeeded();//インスタンスをmMapで取得
        setUISettings();//UIを設定
        setUpCamera();//カメラの初期位置を設定
        setDBMarker();//データベースの情報をマーカに設定
        //プログレス
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("ルート検索中...");
        progressDialog.hide();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }
    //marker set
    private void setUpMarker(int lat,int lng,String name) {
        GeoPoint geo = new GeoPoint(lat,lng);
        double latitude = geo.getLatitudeE6() / 1E6;
        double longitude = geo.getLongitudeE6() / 1E6;
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name));
    }
    private Location nowPoint() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }
    private void setUpCamera() {
        Location location = nowPoint();
        if(location == null) {
            //現在地情報取得失敗時の処理
            Toast.makeText(this, "現在地取得できません", Toast.LENGTH_SHORT).show();
        }else {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);//カメラのズーム15
            mMap.moveCamera(cu);
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
            Location.distanceBetween(
                    lat_now,
                    lng_now,
                    lat,
                    lng,
                    results);
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
    //DBからmarker情報を取得
    private void setDBMarker() {
        SQLiteDatabase sql = DBService.db;
        Cursor cur = sql.query(DBService.DB_TABLE, new String[]{"info", "latitude",
                "longitude", "image", "description"}, null, null, null, null, null);
        cur.moveToFirst();
        do {
            String info = cur.getString(0);
            int latitude = cur.getInt(1);
            int longitude = cur.getInt(2);
            setUpMarker(latitude, longitude, info);
            // マーカークリック時のイベントハンドラ登録
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Location lc = nowPoint();
                    LatLng lg = new LatLng(lc.getLatitude(),lc.getLongitude());
                    calcDistance((int) (marker.getPosition().latitude * 1E6), (int) (marker.getPosition().longitude * 1E6), marker.getTitle());
                    if(isNavigation == true) {
                        mMap.clear();
                        isNavigation=false;
                        setDBMarker();
                    }
                    routeSearch(lg, marker.getPosition());
                    return false;
                }
            });
        } while (cur.moveToNext());
        //カーソルクローズ
        cur.close();
    }
    private void setUISettings() {
        // 現在位置表示の有効化
        mMap.setMyLocationEnabled(true);
        // 設定の取得
        UiSettings settings = mMap.getUiSettings();
        // コンパスの有効化
        settings.setCompassEnabled(true);
        // 現在位置に移動するボタンの有効化
        settings.setMyLocationButtonEnabled(true);
        // ズームイン・アウトボタンの有効化
        settings.setZoomControlsEnabled(true);
        // すべてのジェスチャーの有効化
        settings.setAllGesturesEnabled(true);
        // 回転ジェスチャーの有効化
        settings.setRotateGesturesEnabled(true);
        // スクロールジェスチャーの有効化
        settings.setScrollGesturesEnabled(true);
        // Tlitジェスチャー(立体表示)の有効化
        settings.setTiltGesturesEnabled(true);
        // ズームジェスチャー(ピンチイン・アウト)の有効化
        settings.setZoomGesturesEnabled(true);
    }
    //----ルート検索用メソッド----
    private void routeSearch(LatLng origin,LatLng target){
        progressDialog.show();
        String url = getDirectionsUrl(origin, target);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
    private String getDirectionsUrl(LatLng origin,LatLng target){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_target = "destination="+target.latitude+","+target.longitude;
        String sensor = "sensor=false";
        //パラメータ
        String parameters = str_origin+"&"+str_target+"&"+sensor + "&language=ja" + "&mode=" + travelMode;
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

        //ルート検索で得た座標を使って経路表示
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if(result.size() != 0){

                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.get(i);
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    //ポリライン
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(0x550000ff);
                }
                //描画
                mMap.addPolyline(lineOptions);
                isNavigation=true;
            }else{
                Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
            }
            progressDialog.hide();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, MENU_A,   0, "Info");
        menu.add(0, MENU_B,   0, "Legal Notices");
        menu.add(0, MENU_c,   0, "Mode");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() )
        {
            case MENU_A:
                //show_mapInfo();
                return true;

            case MENU_B:
                //Legal Notices(免責事項)
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapsActivity.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
            case MENU_c:
                //show_settings();
                return true;
        }
        return false;
    }
}

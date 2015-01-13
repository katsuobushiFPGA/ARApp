package com.example.hiroto.gpsarapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

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

/**
 * MapsActivity
 * GoogleMapsによるルートナビゲーションを行うクラス.
 * @author hirto
 *
 */
public class MapsActivity extends FragmentActivity  implements LocationListener {
    //route
    public ProgressDialog progressDialog;
    public String travelMode = "driving";//default
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;//locationManager

    /**
     * Activityが生成時に呼び出されるクラス.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GPSCalibration();
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
        if (NavigationManager.getNavigationFlag()) {
            Location loc = nowPoint();
            LatLng l = new LatLng(loc.getLatitude(), loc.getLongitude());
            routeSearch(l, NavigationManager.getTarget());
        }
    }

    /**
     * ActivityがResume時に呼び出される.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        GPSCalibration();
    }

    /**
     * ActivityがStop時に呼び出される.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }
    /**
     * ActivityがRestart時に呼び出される.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    /**
     * ActivityがDestroy時に呼び出される.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    /**
     * GPSの緯度と経度を初期化する.
     * GPSが取得できない場合に関してのエラー
     */
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

    /**
     * GoogleMapオブジェクトが取得されていない場合取得する処理を行う.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    /**
     * マーカーを配置する.
     * @param lat 緯度
     * @param lng 経度
     * @param name 情報
     */
    private void setUpMarker(int lat,int lng,String name) {
        GeoPoint geo = new GeoPoint(lat,lng);
        double latitude = geo.getLatitudeE6() / 1E6;
        double longitude = geo.getLongitudeE6() / 1E6;
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(name));
    }

    /**
     * 現在地を取得する.
     * 最適な精度を選ぶ処理をさせる.
     * @return 成功時Location 失敗時null
     */
    private Location nowPoint() {
        Location location;
        if(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }
    /**
     * 地図のカメラの位置を現在位置に設定する.
     */
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

    /**
     * 現在地と引数から距離を計算する.
     * @param latitude 緯度
     * @param longitude 経度
     * @param info スポット情報
     */
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

    /**
     *  DBから情報を取得しイベント,マーカーを設定する.
     */
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
                    NavigationManager.setTarget(marker.getPosition());//target 設定
                    NavigationManager.setInfo(marker.getTitle());//info 設定
                    final String[] DoList = new String[]{"CalcDistance","RouteSearch"};
                    final String[] NaviList = new String[]{"driving","walking","bicycling"};
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("What do you want?")
                            .setItems(DoList,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    switch(which) {
                                        case 0: calcDistance((int) (NavigationManager.getTarget().latitude * 1E6), (int) (NavigationManager.getTarget().longitude * 1E6), NavigationManager.getInfo());
                                                 break;
                                        case 1:   new AlertDialog.Builder(MapsActivity.this)
                                                   .setTitle("What is vehicle?")
                                                   .setItems(NaviList,new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog,int vehicle) {
                                                        switch(vehicle) {
                                                            case 0:NavigationManager.setTravelMode(NaviList[0]);
                                                                break;
                                                            case 1:NavigationManager.setTravelMode(NaviList[1]);
                                                                break;
                                                            case 2:NavigationManager.setTravelMode(NaviList[2]);
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                     }
                                                    })
                                                    .create()
                                                    .show();

                                                    Location lc = nowPoint();
                                                    if(NavigationManager.getNavigationFlag()) {
                                                            mMap.clear();
                                                            NavigationManager.setNavigationFlag(false);
                                                            setDBMarker();
                                                     }
                                                     routeSearch(new LatLng(lc.getLatitude(),lc.getLongitude()),new LatLng(NavigationManager.getTarget().latitude, NavigationManager.getTarget().longitude));
                                                        break;
                                        default:break;
                                    }
                                }
                            })
                            .create()
                            .show();
                    return false;
                }
            });
        } while (cur.moveToNext());
        //カーソルクローズ
        cur.close();
    }

    /**
     * UIを設定する.
     */
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


    /**
     * LocationProviderが無効になった場合に呼び出される
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {}

    /**
     * LocationProviderが無効になった場合に呼び出される
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {}

    /**
     * LocationProviderの状態が変更された場合に呼び出される
     * @param provider プロバイダ(GPS,Internet)
     * @param status 状態を保存
     * @param extras プロバイダ固有のステータス変数が含まれまる。(オプション)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     * 自身の位置が変更された時に呼び出される.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location){}

    //----ルート検索用メソッド----

    /**
     * 第一引数から第二引数までのルートを探索する.
     * ルート情報等は全てNavigationManagerクラスが管理する.
     * @param origin 起点
     * @param target 終点
     */
    private void routeSearch(LatLng origin,LatLng target){
        progressDialog.show();
        NavigationManager.setTarget(target);
        String url = getDirectionsUrl(origin, target);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    /**
     * 第一引数から第二引数までのルートのURLを取得する.
     * @param origin 起点
     * @param target 終点
     * @return 成功時 DirectionsAPIのURL
     */
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

    /**
     * URLから情報をダウンロードする.
     * @param strUrl URL
     * @return 成功時 DirectionsAPIのデータ
     * @throws IOException
     */
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

    /**
     * 非同期,バックグラウンドにルート情報を取得する
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        /**
         * バックグラウンドでdataをダウンロードする.
         * @param url
         * @return
         */
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

        /**
         * parseJsonpOfDirectionsAPIにてJSONをparseする.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /**
     *  非同期に,JSONを解析する
    */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        /**
         * ルート情報を取得する
         * @param jsonData ルートデータ
         * @return 緯度経度のリスト
         */
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

        /**
         *  ルート検索で得た座標を使ってポリラインで経路表示
         * @param result
         */
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
            }else{
                Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }
    }

}

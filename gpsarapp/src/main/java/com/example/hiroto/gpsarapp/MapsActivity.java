package com.example.hiroto.gpsarapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);
        setUpMapIfNeeded();//インスタンスをmMapで取得
        setUISettings();//UIを設定
        setUpCamera();//カメラの初期位置を設定
        setDBMarker();//データベースの情報をマーカに設定
        Toast.makeText(this,"onStart",Toast.LENGTH_SHORT).show();
        calcDistance(35727594,139765215,"谷中銀座");

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Toast.makeText(this,"onResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);//カメラのズーム12
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
                    distance = String.valueOf((int)(results[0] / 1E3) + "km") ;
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
                    calcDistance((int)(marker.getPosition().latitude*1E6),(int)(marker.getPosition().longitude*1E6),marker.getTitle());
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
}

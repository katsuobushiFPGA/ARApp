package com.example.hiroto.gpsarapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);
        setUpMapIfNeeded();//インスタンスをmMapで取得
        setUpCamera();
        setUISettings();
        setDBMarker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
    private void setUpCamera() {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(35.689487, 139.691706), 8);//カメラのズーム8
        mMap.moveCamera(cu);
    }
    //DBからmarker情報を取得
    //GPSARAppを起動してからでないと落ちる。(DBデータがないため)
    private void setDBMarker() {
        SQLiteDatabase sql = new GPSARApp().getDB();
        Cursor cur = sql.query(GPSARApp.DB_TABLE, new String[]{"info", "latitude",
                "longitude", "image", "description"}, null, null, null, null, null);
        cur.moveToFirst();
        do {
            String info = cur.getString(0);
            int latitude = cur.getInt(1);
            Log.d("setDBMarker","latitude:" + latitude);
            int longitude = cur.getInt(2);
            Log.d("setDBMarker","longitude:" + longitude);
            setUpMarker(latitude, longitude, info);
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

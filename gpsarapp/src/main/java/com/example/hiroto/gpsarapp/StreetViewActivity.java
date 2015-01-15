package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

/**
 * @author hiroto
 */
public class StreetViewActivity extends Activity implements LocationListener {

    private StreetViewPanoramaView svpView;
    private LocationManager mLocationManager;
    private GeoPoint geoPoint;

    private static  LatLng PIN;//位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        GPSCalibration();
        geoPoint = new GeoPoint((int)(nowPoint().getLatitude()*1E6),(int)(nowPoint().getLongitude()*1E6));
        PIN = new LatLng((double)(geoPoint.getLatitudeE6() / 1E6),(double)(geoPoint.getLongitudeE6() / 1E6));
        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();

        if (savedInstanceState == null) {
            options.position(PIN); //場所の指定
            options.panningGesturesEnabled(true); //画面を指でグリグリ動かす
            options.streetNamesEnabled(false); //道路名は表示しない
            options.userNavigationEnabled(true); //ナビゲーションを有効
            options.zoomGesturesEnabled(true); //ピンチで拡大を有効
        }

        svpView = new StreetViewPanoramaView(this, options);
        addContentView(svpView,
                new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        svpView.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        svpView.onDestroy();
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }
    @Override
    public void onLowMemory() {
        svpView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onPause() {
        svpView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        svpView.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        svpView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
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
    public void onLocationChanged(Location location){
        geoPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location
                .getLongitude() * 1E6));
        Log.d("LocationChanged",String.valueOf(geoPoint));
        PIN = new LatLng((double)(geoPoint.getLatitudeE6() / 1E6),(double)(geoPoint.getLongitudeE6() / 1E6));
        svpView.invalidate();
    }

}


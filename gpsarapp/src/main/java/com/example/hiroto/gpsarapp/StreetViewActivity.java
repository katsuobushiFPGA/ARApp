package com.example.hiroto.gpsarapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.maps.GeoPoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ストリートビューを用い,ナビゲーションを行うクラス
 * @author hiroto
 */
public class StreetViewActivity extends ActionBarActivity implements LocationListener {

    private StreetViewPanoramaView svpView;
    private LocationManager mLocationManager;
    private GeoPoint geoPoint;
    private StreetViewPanoramaFragment fm;
    private StreetViewPanoramaCamera camera;
    private StreetViewPanorama map;
    private static  LatLng PIN;//位置
    // メニューアイテム識別用のID
    private static final int MENU_ID_A = 0;
    private static final int MENU_ID_B = 1;
    //タイマーによる処理
    private RouteNaviTask mTask = null;
    private Timer mTimer   = null;
    private Handler mHandler = new Handler();
    private float mLaptime = 0.0f;
    //タイマ用イテレータ
    private int counter;
    //距離表示用
    private TextView mTdistance;
    private static final int TEXT_SIZE = 80;
    /**
     * Activity生成時に行われる処理
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 画面サイズの取得
        Display disp = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayX = disp.getWidth();
        int displayY =disp.getHeight();

        setContentView(R.layout.activity_street_view);
        // タイトルバーの削除
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        GPSCalibration();
        geoPoint = new GeoPoint((int)(nowPoint().getLatitude()*1E6),(int)(nowPoint().getLongitude()*1E6));
        PIN = new LatLng((double)(geoPoint.getLatitudeE6() / 1E6),(double)(geoPoint.getLongitudeE6() / 1E6));

        fm = (StreetViewPanoramaFragment)getFragmentManager().findFragmentById(R.id.street_view);
        map = fm.getStreetViewPanorama();
        camera = new StreetViewPanoramaCamera(10.0f, 5.0f, 165.0f);
        if (savedInstanceState == null) {
            map.setStreetNamesEnabled(false);//道路名は表示しない
            map.setUserNavigationEnabled(true);//ナビゲーションを有効
            map.setZoomGesturesEnabled(true);//ピンチで拡大を有効
            map.setPanningGesturesEnabled(true);//画面を指で動かす
            map.setPosition(PIN);
//            map.animateTo(camera, 1000);//カメラをその位置へ移動
        }
        // 登録処理を行うlayoutの作成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        //距離を表示するテキスト
        mTdistance = new TextView(this);
        mTdistance.setText("");
        mTdistance.setWidth(displayX);
        mTdistance.setHeight(displayY - TEXT_SIZE);

        // 各要素の登録
        layout.addView(mTdistance, 0);

        addContentView(layout, new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * OptionsMenuを作成する
     * @param menu メニュー
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューの要素を追加
        // メニューアイテムの追加
        menu.add(Menu.NONE, MENU_ID_A, Menu.NONE, "ナビゲーション開始");
        menu.add(Menu.NONE, MENU_ID_B, Menu.NONE, "ナビゲーション解除");

        return true;
    }
    /**
     *
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(!NavigationManager.getNavigationFlag())
            menu.findItem(MENU_ID_A).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }
    /**
     * メニューが選択された時の処理
     * @param item メニュー項目
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // addしたときのIDで識別
        switch (item.getItemId()) {
            case MENU_ID_A:
                Toast.makeText(this, "ナビゲーション開始", Toast.LENGTH_SHORT).show();
                //タイマ処理
                //タイマーの初期化処理
                mTask = new RouteNaviTask();
                mLaptime = 0.0f;
                mTimer = new Timer(true);
                mTimer.schedule(mTask, 5000, 5000);
                //ルート処理
                return true;

            case MENU_ID_B:
                Toast.makeText(this, "ナビゲーション解除", Toast.LENGTH_SHORT).show();
                NavigationManager.setNavigationFlag(false);
                return true;
        }
        return false;
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
//        map.setPosition(PIN);
    }

    /**
     *  ルートを検索するタスクを生成するクラス
     */
    class RouteNaviTask extends TimerTask {

        @Override
        public void run() {
            // mHandlerを通じてUI Threadへ処理をキューイング
            mHandler.post( new Runnable() {
                public void run() {
                    List<List<HashMap<String, String>>> route = NavigationManager.getRoute();
                    Log.d("size",String.valueOf(route.get(0).size()));
                    HashMap<String,String> hm = route.get(0).get(counter);
                    float[] results = {0,0,0};//距離,方位角,方位角
                    double lat_now = 0;
                    double lng_now = 0;
                    //counterが0の場合,現在位置を起点とする.そうでない場合はひとつ前のルート情報を参照する.
                    if(counter==0){
                        lat_now = PIN.latitude;
                        lng_now = PIN.longitude;
                    }else {
                        lat_now = Double.valueOf(route.get(0).get(counter-1).get("lat"));
                        lng_now = Double.valueOf(route.get(0).get(counter-1).get("lng"));
                    }
                    double lat = Double.valueOf(hm.get("lat"));
                    double lng = Double.valueOf(hm.get("lng"));
                    Location.distanceBetween(lat_now,lng_now,lat,lng,results);//方位角の計算
                    int rad = 0;
                    if(results[1] < 0) {
                        rad = 360 - Math.abs((int)results[1]);
                    }else {
                        rad = (int)results[1];
                    }
                    map.animateTo(new StreetViewPanoramaCamera(0.0f, 0.0f, rad),5000);

                    map.setPosition( new LatLng(lat,lng) ,rad);
                    Log.d("results",String.valueOf(results[1]));
                    calcDistance(new LatLng(lat_now,lng_now),NavigationManager.getTarget());
                    counter++;
                    if(counter >= route.get(0).size()) {
                        //タイマーの停止処理
                        mTimer.cancel();
                        mTimer = null;
                        Toast.makeText(StreetViewActivity.this, "ナビゲーション終了", Toast.LENGTH_SHORT).show();
                        counter=0;
                    }
                }
            });
        }
    }
    private void calcDistance(LatLng origin,LatLng target) {
        float[] results = new float[3];
        String distance = "";

        try {
            Location.distanceBetween(origin.latitude,origin.longitude,target.latitude,target.longitude,results);
            if(results != null && results.length > 0) {
                if(results[0] < 1000)
                    mTdistance.setText("あと" + String.valueOf((int)results[0] + "m")) ;

                else
                    mTdistance.setText("あと" + new BigDecimal(results[0]).divide(new BigDecimal(1E3),3,BigDecimal.ROUND_HALF_UP).toString() + "km");
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }


}


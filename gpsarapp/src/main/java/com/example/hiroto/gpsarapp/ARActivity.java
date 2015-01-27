package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import java.util.Date;
import java.util.List;

/**
 * ARActivityクラス
 * ビューの重ねあわせ,Viewの操作を行う.
 * 使用するAndroid固有のセンサ:磁気センサ,位置センサ
 * @author hiroto
 *
 */
public class ARActivity extends Activity implements SensorEventListener,
        LocationListener {

    private SensorManager sensorManager;
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];
    List<Sensor> listMag;
    List<Sensor> listAcc;

    private ARView arView;

    private LocationManager locationManager;
    private GeoPoint geoPoint;
    private GeomagneticField geomagneticField;
    
    private TextView mTnaviView;
    private static final int TEXT_SIZE = 80;
    /**
     * ARActivityが作成された時に初期化を行う。
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // フルスクリーン指定
        getWindow().clearFlags(
                LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ARViewの取得
        arView = new ARView(this, DBService.cursor);

        // 各種センサーの用意
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        // 画面サイズの取得
        Display disp = ((WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayX = disp.getWidth();
        int displayY =disp.getHeight();
        //ナビ情報を表示するViewの生成
        LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        // TextView
        //距離を表示するテキスト
        mTnaviView = new TextView(this);
        mTnaviView.setText("");
        mTnaviView.setTextSize(12);
        mTnaviView.setWidth(displayX);
        mTnaviView.setHeight(displayY - TEXT_SIZE);
        layout.addView(mTnaviView,0);
        
        // Viewの重ね合わせ
        setContentView(new CameraView(this));
        addContentView(arView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        addContentView(layout,new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
    }
    //	HTMLのタグ除去と「:」と「,」を削除する。
    private String htmlRemover(String str){
       return  str.replaceAll("<.+?>", "");
    }
    private String tagReplacer(String str) {
        return str.replaceAll("<br>","\n");
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * ActivityがResume時に呼び出される。
     */
    @Override
    protected void onResume() {
        super.onResume();
        // ロケーションマネージャの設定
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, //建物内だと取得しにくいのでネットワークにする。
                0, this);
        // センサー処理の登録
        sensorManager.registerListener(this, listMag.get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, listAcc.get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
        if(NavigationManager.getNavigationFlag())
            mTnaviView.setText(tagReplacer(NavigationManager.getPosinfo()));
        Log.d("ONRESUME:", "TEST");
        Log.d("FLAG:", String.valueOf(NavigationManager.getNavigationFlag()));
    }

    /**
     * ActivityがStop時に呼び出される。
     */
    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
    }

    /**
     * ActivityがRestart時に呼び出される。
     */
    @Override
    public void onRestart() {
        super.onRestart();
    }

    /**
     * ActivityがDestroy時に呼び出される。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * センサの精度が変更されたときに呼び出される。
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * センサ値が変更されたときに呼び出される.
     *
     * @param event イベント
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                break;
        }

        if (magneticValues != null && accelerometerValues != null
                && geomagneticField != null) {
            float[] R = new float[16];
            float[] I = new float[16];

            SensorManager.getRotationMatrix(R, I, accelerometerValues,
                    magneticValues);

            float[] actual_orientation = new float[3];

            SensorManager.getOrientation(R, actual_orientation);

            // 求まった方位角をラジアンから度に変換する
            float direction = (float) Math.toDegrees(actual_orientation[0])
                    + geomagneticField.getDeclination();
            arView.drawScreen(direction, geoPoint);
        }
    }

    /**
     * 自身の位置が変更された時に呼び出される.
     *
     * @param arg0
     */
    @Override
    public void onLocationChanged(Location arg0) {
        geoPoint = new GeoPoint((int) (arg0.getLatitude() * 1E6), (int) (arg0
                .getLongitude() * 1E6));
        geomagneticField = new GeomagneticField((float) arg0.getLatitude(),
                (float) arg0.getLongitude(), (float) arg0.getAltitude(),
                new Date().getTime());
    }

    /**
     * LocationProviderが無効になった場合に呼び出される
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * LocationProviderが無効になった場合に呼び出される
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * LocationProviderの状態が変更された場合に呼び出される
     *
     * @param provider プロバイダ(GPS,Internet)
     * @param status   状態を保存
     * @param extras   プロバイダ固有のステータス変数が含まれまる。(オプション)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

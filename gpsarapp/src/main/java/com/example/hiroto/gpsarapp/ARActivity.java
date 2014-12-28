package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.ContentValues;
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
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import java.util.Date;
import java.util.List;

public class ARActivity extends Activity implements SensorEventListener,
        LocationListener, View.OnClickListener {

    private SensorManager sensorManager;
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];
    List<Sensor> listMag;
    List<Sensor> listAcc;

    private ARView arView;

    private LocationManager locationManager;
    private GeoPoint geoPoint;
    private GeomagneticField geomagneticField;



    private static final int BUTTON_SIZE = 80;
    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // フルスクリーン指定
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ARViewの取得
        arView = new ARView(this, DBService.cursor);
        DBService.cursor.close();

        // 各種センサーの用意
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        // 画面サイズの取得
        Display disp = ((WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayX = disp.getWidth();

        // 登録処理を行うlayoutの作成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        // 登録文字
        editText = new EditText(this);
        editText.setWidth(displayX - BUTTON_SIZE);
        // 登録ボタン
        Button button = new Button(this);
        button.setText("Register");
        button.setOnClickListener(this);
        button.setWidth(BUTTON_SIZE);
        // 各要素の登録
        layout.addView(editText, 0);
        layout.addView(button, 1);

        // Viewの重ね合わせ
        setContentView(new CameraView(this));
        addContentView(arView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ロケーションマネージャの設定
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);
        // センサー処理の登録
        sensorManager.registerListener(this, listMag.get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, listAcc.get(0),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // センサー値の反映
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

    @Override
    public void onLocationChanged(Location arg0) {
        geoPoint = new GeoPoint((int) (arg0.getLatitude() * 1E6), (int) (arg0
                .getLongitude() * 1E6));
        geomagneticField = new GeomagneticField((float) arg0.getLatitude(),
                (float) arg0.getLongitude(), (float) arg0.getAltitude(),
                new Date().getTime());
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }



    @Override
    public void onClick(View v) {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(this, "テキストを入力してください", Toast.LENGTH_LONG).show();
        } else if (geoPoint == null) {
            Toast.makeText(this, "位置情報が取得できません", Toast.LENGTH_LONG).show();
        } else {
            ContentValues values = new ContentValues();
            values.put("info", editText.getText().toString());
            values.put("latitude", geoPoint.getLatitudeE6());
            values.put("longitude", geoPoint.getLongitudeE6());
            values.put("image","dummy");//image dummy
            values.put("description","dummy");//description dummy
            DBService.db.insert(DBService.DB_TABLE, "", values);
            DBService.cursor = DBService.db.query(DBService.DB_TABLE, new String[] { "info", "latitude",
                    "longitude","image","description" }, null, null, null, null, null);
            arView.readTable(DBService.cursor);
            DBService.cursor.close();
            editText.setText("");
            Toast.makeText(this, "テキストが登録されました", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }



}
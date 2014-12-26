package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

public class GPSARApp extends Activity implements SensorEventListener,
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

    // データベースで使用する変数
    private final static String DB_NAME = "gps_data.db";
    private final static String DB_TABLE = "gps_data";
    private final static int DB_VERSION = 1;
    private SQLiteDatabase db;
    Cursor cursor;

    private static final int BUTTON_SIZE = 80;
    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // データベースの用意
        initData();

        // フルスクリーン指定
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ARViewの取得
        arView = new ARView(this, cursor);
        cursor.close();

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

    public void initData() {
        // SQLiteOpenHelperを継承したクラスを使用してデータベースを作成します
        SQLiteOpenHelperEx helper = new SQLiteOpenHelperEx(this);
        db = helper.getWritableDatabase();

        cursor = db.query(DB_TABLE, new String[] { "info", "latitude",
                "longitude","image" , "description" }, null, null, null, null, null);
        // テーブルが空の時内容をセットする
        if (cursor.getCount() < 1) {
            presetTable();
            cursor = db.query(DB_TABLE, new String[] { "info", "latitude",
                    "longitude","image" , "description" }, null, null, null, null, null);
        }
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
            db.insert(DB_TABLE, "", values);
            cursor = db.query(DB_TABLE, new String[] { "info", "latitude",
                    "longitude" }, null, null, null, null, null);
            arView.readTable(cursor);
            cursor.close();
            editText.setText("");
            Toast.makeText(this, "テキストが登録されました", Toast.LENGTH_LONG).show();
        }
    }

    private void presetTable() {
        Log.d("presetTable","run!");
        // テーブルの内容が空の時以下の内容をセットする
        ContentValues values = new ContentValues();
        values.put("info", "安田講堂");
        values.put("latitude", 35713433);
        values.put("longitude", 139762594);
        values.put("image", "yasudakodo");
        values.put("description", "yasuda_kodo");
        db.insert(DB_TABLE, "", values);
        values.put("info", "東京ドーム");
        values.put("latitude", 35705593);
        values.put("longitude", 139752252);
        values.put("image","tokyodome");
        values.put("description", "tokyo_dome");
        db.insert(DB_TABLE, "", values);
        values.put("info", "東京スカイツリー");
        values.put("latitude", 35710084);
        values.put("longitude", 139810751);
        values.put("image", "skytree");
        values.put("description", "sky_tree");
        db.insert(DB_TABLE, "", values);
        values.put("info", "明治神宮");
        values.put("latitude", 35676402);
        values.put("longitude", 139700174);
        values.put("image", "meijijingu");
        values.put("description", "meiji_jingu");
        db.insert(DB_TABLE, "", values);
        values.put("info", "国会議事堂");
        values.put("latitude", 35675844);
        values.put("longitude", 139745578);
        values.put("image", "kokkaigijido");
        values.put("description", "kokkai_gijido");
        db.insert(DB_TABLE, "", values);
        values.put("info", "谷中銀座");
        values.put("latitude", 35727594);
        values.put("longitude", 139765215);
        values.put("image", "yanakaginza");
        values.put("description", "yanaka_ginza");
        db.insert(DB_TABLE, "", values);
        values.put("info", "東京タワー");
        values.put("latitude", 3565858);
        values.put("longitude", 139745433);
        values.put("image","tokyotower");
        values.put("description", "tokyo_tower");
        db.insert(DB_TABLE, "", values);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public class SQLiteOpenHelperEx extends SQLiteOpenHelper {
//      コンストラクタ
        public SQLiteOpenHelperEx(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // テーブルの作成
            String sql = "create table if not exists " + DB_TABLE
                    + "(info text, latitude numeric, longitude numeric, image text , description text)";
            Log.d("GPSARApp ,Table:",sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // データベースのアップグレード
            // ここでは、テーブルを作り直しをしています
            db.execSQL("drop table if exists " + DB_TABLE);
            onCreate(db);
        }
        //dbアクセス用
        public SQLiteDatabase getDB(){
            return db;
        }


    }


}
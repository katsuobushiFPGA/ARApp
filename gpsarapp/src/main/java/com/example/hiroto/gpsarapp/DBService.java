package com.example.hiroto.gpsarapp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by hiroto on 2014/12/28.
 */
public class DBService extends Service {
    // データベースで使用する変数
    public final static String DB_NAME = "gps_data.db";
    public final static String DB_TABLE = "gps_data";
    public final static int DB_VERSION = 1;
    public static SQLiteDatabase db;
    public static Cursor cursor;
    private TitleActivity title;

    @Override
    public void onCreate() {
        initData();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
    }

    //サービスに接続するためのBinder
    public class DBServiceBinder extends Binder {
        //サービスの取得
        DBService getService() {
            return DBService.this;
        }
    }
    //Binderの生成
    private final IBinder mBinder = new DBServiceBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
    public void presetTable() {
        Log.d("presetTable", "run!");
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
        values.put("latitude", 35658580);
        values.put("longitude", 139745433);
        values.put("image","tokyotower");
        values.put("description", "tokyo_tower");
        db.insert(DB_TABLE, "", values);
    }
    //SQLクラス
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
    }
}

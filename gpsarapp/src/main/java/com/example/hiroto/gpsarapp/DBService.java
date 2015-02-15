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

/**
 * 観光地の値を保持する常駐するサービスクラス
 * @author hiroto
 */
public class DBService extends Service {
    // データベースで使用する変数
    public final static String DB_NAME = "gps_data.db";
    public final static String DB_TABLE = "gps_data";
    public final static int DB_VERSION = 1;
    public static SQLiteDatabase db;
    public static Cursor cursor;
    private final IBinder mBinder = new DBServiceBinder();    //Binderの生成

    /**
     * Serviceクラスが生成された時に処理する.
     */
    @Override
    public void onCreate() {
        initData();
    }

    /**
     * startServiceでサービスが開始要求を受けたときのコールバック.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }

    /**
     * Service破棄時に呼び出される.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    /**
     * サービスに接続するためのBinderクラス
     */
    public class DBServiceBinder extends Binder {
        //サービスの取得
        DBService getService() {
            return DBService.this;
        }
    }

    /**
     * Bindするために必要なメソッド
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * データベースの初期設定を行う.
     */
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
    /**
     *  テーブルの内容が空の時以下の内容をセットする
     */
    public void presetTable() {
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
        values.put("info", "東京電機大学");
        values.put("latitude", 35748256);
        values.put("longitude", 139806906);
        values.put("image","tokyo_denki_university");
        values.put("description", "tokyo_denki_university");
        db.insert(DB_TABLE, "", values);
        values.put("info", "レインボーブリッジ");
        values.put("latitude", 35636564);
        values.put("longitude", 139763144);
        values.put("image","rainbow_bridge");
        values.put("description", "rainbow_bridge");
        db.insert(DB_TABLE, "", values);
        values.put("info", "北千住駅");
        values.put("latitude", 35749412);
        values.put("longitude", 139805108);
        values.put("image","kitasenju_station");
        values.put("description", "kitasenju_station");
        db.insert(DB_TABLE, "", values);
        values.put("info", "足立区中央図書館");
        values.put("latitude", 35756542);
        values.put("longitude", 139802675);
        values.put("image","adachi_library");
        values.put("description", "adachi_library");
        db.insert(DB_TABLE, "", values);
        values.put("info", "アサヒビール");
        values.put("latitude", 35709811);
        values.put("longitude", 139800481);
        values.put("image","asahi");
        values.put("description", "asahi");
        db.insert(DB_TABLE, "", values);
        values.put("info", "浅草寺");
        values.put("latitude", 35714765);
        values.put("longitude", 139796655);
        values.put("image","sensoji");
        values.put("description", "sensoji");
        db.insert(DB_TABLE, "", values);
    }

    /**
     * DBを扱うためのSQLクラス
     */
    public class SQLiteOpenHelperEx extends SQLiteOpenHelper {
        //      コンストラクタ
        public SQLiteOpenHelperEx(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        /**
         * DB生成時に呼び出される.
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // テーブルの作成
            String sql = "create table if not exists " + DB_TABLE
                    + "(info text, latitude numeric, longitude numeric, image text , description text)";
            db.execSQL(sql);
        }

        /**
         * DBのバージョン変更時に呼び出される.
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // データベースのアップグレード
            // ここでは、テーブルを作り直しをしています
            db.execSQL("drop table if exists " + DB_TABLE);
            onCreate(db);
        }
    }
}

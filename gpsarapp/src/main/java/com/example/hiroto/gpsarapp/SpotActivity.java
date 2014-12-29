package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import java.math.BigDecimal;

/**
 * Created by hiroto on 2014/12/26.
 */
public class SpotActivity extends Activity implements View.OnClickListener{
    private ImageView image;
    private TextView text;
    private Button navi;
    private Button distance;
    private int lat;
    private int lng;
    private String info;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_spot);

        Intent i = getIntent();//呼び出し元のintentを取得

        String description = i.getStringExtra("description");//説明文
        String img = i.getStringExtra("image");//画像
        lat = i.getIntExtra("lat",0);//latitude get 0
        lng = i.getIntExtra("lng",0);//longitude get 0
        info = i.getStringExtra("info");

        int desid = getResources().getIdentifier(description,"string", getPackageName());//desid取得
        int imgid = getResources().getIdentifier(img,"drawable", getPackageName());//imgid取得

        image = (ImageView)findViewById(R.id.imageView);
        image.setImageResource(imgid);
        text = (TextView)findViewById(R.id.textView);
        text.setText(getResources().getString(desid));
        navi = (Button)findViewById(R.id.button_navi);
        navi.setOnClickListener(this);
        distance = (Button)findViewById(R.id.button_distance);
        distance.setOnClickListener(this);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //naviを押した時の処理
    @Override
    public void onClick(View v) {
        if(v == navi){
            Toast.makeText(this, "未実装です,遷移します.", Toast.LENGTH_SHORT).show();
            //ここでナビゲーションの設定をする。3Dナビの実装が必要。および2Dマップでも必要。
//            Intent intent = new Intent(SpotActivity.this, ARActivity.class);
//            startActivity(intent);
        }else if(v == distance) {
            calcDistance(lat,lng,info);//現在地からの距離を計算
        }else {
            Toast.makeText(this, "No detected Button", Toast.LENGTH_SHORT).show();
        }
    }
    private Location nowPoint() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
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
                    distance = new BigDecimal(results[0]).divide(new BigDecimal(1E3),3,BigDecimal.ROUND_HALF_UP).toString() + "km";

                Toast.makeText(this,"現在地から" + info + "までの距離" + distance ,Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this,"IllegalArgumentException" ,Toast.LENGTH_SHORT).show();
        }
    }
}

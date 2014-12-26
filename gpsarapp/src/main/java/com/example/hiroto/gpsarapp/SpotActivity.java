package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hiroto on 2014/12/26.
 */
public class SpotActivity extends Activity {
    private ImageView image;
    private TextView text;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_spot);

        Intent i = getIntent();//呼び出し元のintentを取得

        String description = i.getStringExtra("description");//説明文
        String img = i.getStringExtra("image");//画像

        int desid = getResources().getIdentifier(description,"drawable", getPackageName());//desid取得
        int imgid = getResources().getIdentifier(img,"drawable", getPackageName());//imgid取得

        image = (ImageView)findViewById(R.id.imageView);
        image.setImageResource(R.drawable.tokyotower);
//        image.setImageResource(imgid);
//        text.setText(desid);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

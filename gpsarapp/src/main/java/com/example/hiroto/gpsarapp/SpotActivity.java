package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        Log.d("i:",String.valueOf(i) );

        String description = i.getStringExtra("description");//説明文
        String img = i.getStringExtra("image");//画像
        Log.d("img:",img);
        Log.d("description",description);
        int desid = getResources().getIdentifier(description,"string", getPackageName());//desid取得
        Log.d("SpotActivity",description);
        int imgid = getResources().getIdentifier(img,"drawable", getPackageName());//imgid取得
        Log.d("img",String.valueOf(imgid));

        image = (ImageView)findViewById(R.id.imageView);
//        image.setImageResource(R.drawable.tokyotower);
        image.setImageResource(imgid);
        text = (TextView)findViewById(R.id.textView);
//        text.setText(R.string.tokyo_dome);
        text.setText(getResources().getString(desid));
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

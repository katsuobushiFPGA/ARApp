package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by hiroto on 2014/12/27.
 */
public class TitleActivity extends Activity implements View.OnClickListener{
    Button button_gps;
    Button button_game;
    Button button_map;
    Button button_object;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_title);
        button_gps  = (Button)findViewById(R.id.button_gpsarapp);
        button_game = (Button)findViewById(R.id.button_gamemode);
        button_map  = (Button)findViewById(R.id.button_map);
        button_object  = (Button)findViewById(R.id.button_object);
        button_gps.setOnClickListener(this);
        button_game.setOnClickListener(this);
        button_map.setOnClickListener(this);
        button_object.setOnClickListener(this);
        startService(new Intent(TitleActivity.this, DBService.class));
    }
    @Override
    public void onRestart() {
        super.onRestart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(TitleActivity.this, DBService.class));
    }

    @Override
    public void onClick(View v) {
        if (v == button_gps){
            Intent intent = new Intent(TitleActivity.this, ARActivity.class);
            startActivity(intent);
        }else if (v == button_game){
            Toast.makeText(this, "未実装です", Toast.LENGTH_LONG).show();
            return;
        }else if (v == button_map){
            Intent intent = new Intent(TitleActivity.this, MapsActivity.class);
            startActivity(intent);
        }else if (v == button_object){
            Intent intent = new Intent(TitleActivity.this, TestActivity.class);
            startActivity(intent);
            Toast.makeText(this, "未実装です", Toast.LENGTH_LONG).show();
            return;
        }else {
            Toast.makeText(this, "No detected Button", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
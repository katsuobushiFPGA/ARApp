package com.example.hiroto.gl20modelview;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

//3Dモデルの読み込み
public class MainActivity extends Activity {
    private GLSurfaceView glView;

    //アクティビティ生成時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //GLサーフェイスビュー
        glView=new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GLRenderer2(this));
        setContentView(glView);
    }

    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }
}
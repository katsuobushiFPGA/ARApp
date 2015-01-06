package com.example.hiroto.gl20modelview;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

//3Dモデルの読み込み
public class MainActivity extends Activity {
    private GLSurfaceView glView;

    //アクティビティ生成時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //GLサーフェイスビュー
        glView=new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);//OpenGL ES 2.0を使用
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(new GLRenderer2(this));
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(glView);
        addContentView(new CameraView(this), new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));
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
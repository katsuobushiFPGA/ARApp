package com.example.hiroto.gl20modelview;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
        glView.getHolder().setFormat(PixelFormat.RGBA_8888);
        glView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
//        setContentView(glView);
        FrameLayout layout = new FrameLayout(this);
        setContentView(layout);
        addContentView(glView, new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));
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
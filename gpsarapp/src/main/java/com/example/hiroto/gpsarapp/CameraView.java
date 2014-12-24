package com.example.hiroto.gpsarapp;

/**
 * Created by hiroto on 2014/12/24.
 */

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    // コンストラクタ
    public CameraView(Context context) {
        super(context);

        // サーフェイスホルダーの取得とコールバック通知先の指定
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // SurfaceViewの種別をプッシュバッファーに変更します
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceholder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceholder);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // プレビューの開始
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}

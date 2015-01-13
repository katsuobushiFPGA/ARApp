package com.example.hiroto.gpsarapp;

/**
 * Created by hiroto on 2014/12/24.
 */

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * CameraViewクラス
 * カメラを起動し制御する.
 * @author hiroto
 */
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

    /**
     * 任意の構造変化（形式やサイズが）表面に行われた直後に呼び出される.
     * このメソッドは、常にsurfaceCreated （ SurfaceHolder ）の後に、少なくとも一回呼び出されます。
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // プレビューの開始
        camera.startPreview();
    }

    /**
     * surfaceが破棄される直前に呼び出される.
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}

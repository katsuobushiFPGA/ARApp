package com.example.hiroto.gl20modelview;

/**
 * Created by hiroto on 2015/01/06.
 */
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//レンダラー
public class GLRenderer2 implements
        GLSurfaceView.Renderer {
    //システム
    private float aspect;//アスペクト比
    private int   tick;  //時間経過

    //モデル
    private Object3D model=new Object3D();
    private Object3D handL=new Object3D();
    private Object3D handR=new Object3D();
    private Object3D legL =new Object3D();
    private Object3D legR =new Object3D();

    //コンストラクタ
    public GLRenderer2(Context context) {
        GLES.context=context;
    }

    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        //プログラムの生成
        GLES.makeProgram();

        //デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //光源の有効化
        GLES20.glUniform1i(GLES.useLightHandle,1);

        //光源色の指定
        GLES20.glUniform4f(GLES.lightAmbientHandle,0.2f,0.2f,0.2f,1.0f);
        GLES20.glUniform4f(GLES.lightDiffuseHandle,0.7f,0.7f,0.7f,1.0f);
        GLES20.glUniform4f(GLES.lightSpecularHandle,0.9f,0.9f,0.9f,1.0f);

        //モデルの読み込み
        try {
            //手
            Figure handFigure=ObjLoader.load("droid_hand.obj");
            handL.figure=handFigure;
            handL.position.set(-0.65f,1.24f,0.0f);
            handR.figure=handFigure;
            handR.position.set( 0.65f,1.24f,0.0f);

            //足
            Figure legFigure=ObjLoader.load("droid_leg.obj");
            legL.figure=legFigure;
            legL.position.set(-0.2f,0.5f,0.0f);
            legR.figure=legFigure;
            legR.position.set( 0.2f,0.5f,0.0f);

            //体
            model.figure=ObjLoader.load("droid_body.obj");
            model.childs.add(handL);
            model.childs.add(handR);
            model.childs.add(legL);
            model.childs.add(legR);
        } catch (Exception e) {
            android.util.Log.e("debug",e.toString());
            for (StackTraceElement ste:e.getStackTrace()) {
                android.util.Log.e("debug","    "+ste);
            }
        }
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 gl10) {
        //画面のクリア
        GLES20.glClearColor(0,0,0,0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES.gluPerspective(GLES.pMatrix,
                45.0f,  //Y方向の画角
                aspect, //アスペクト比
                0.01f,  //ニアクリップ
                100.0f);//ファークリップ

        //光源位置の指定
        GLES20.glUniform4f(GLES.lightPosHandle,5.0f,5.0f,5.0f,1.0f);

        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix,0);
        GLES.gluLookAt(GLES.mMatrix,
                0.0f,0.8f,5.0f, //カメラの視点
                0.0f,0.8f,0.0f, //カメラの焦点
                0.0f,1.0f,0.0f);//カメラの上方向

        //モデル変換
        Matrix.rotateM(GLES.mMatrix,0,0,0,1,0);
        tick++;
        //左手・右足
        int rotate=tick%180;
        rotate=(rotate<90)?rotate-45:180-45-rotate;
        handL.rotate.x=rotate;
        legR.rotate.x=rotate;

        //右手・左足
        rotate=(tick+90)%180;
        rotate=(rotate<90)?rotate-45:180-45-rotate;
        handR.rotate.x=rotate;
        legL.rotate.x=rotate;

        //モデルの描画
        model.draw();
    }
}
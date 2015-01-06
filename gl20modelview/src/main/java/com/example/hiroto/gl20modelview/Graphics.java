package com.example.hiroto.gl20modelview;

/**
 * Created by hiroto on 2015/01/06.
 */
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//2Dグラフィックス
public class Graphics {
    public  int         screenW;     //画面幅
    public  int         screenH;     //画面高さ
    private FloatBuffer vertexBuffer;//頂点バッファ
    private FloatBuffer uvBuffer;    //UVバッファ

    //コンストラクタ
    public Graphics(int screenW,int screenH) {
        this.screenW=screenW;
        this.screenH=screenH;

        //頂点バッファの生成
        float[] vertexs={
                -1.0f, 1.0f,0.0f,//頂点0
                -1.0f,-1.0f,0.0f,//頂点1
                1.0f, 1.0f,0.0f,//頂点2
                1.0f,-1.0f,0.0f,//頂点3
        };
        vertexBuffer=makeFloatBuffer(vertexs);

        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);
    }

    //float配列→floatバッファ
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb=ByteBuffer.allocateDirect(array.length*4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    //2D描画の設定
    public void setup2D() {
        //頂点配列の有効化
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.uvHandle);

        //デプステストと光源の無効化
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES.useLightHandle,0);

        //ブレンドの指定
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES20.glUniform4fv(GLES.colorHandle,1,new float[]{1,1,1,1},0);
        GLES20.glVertexAttribPointer(GLES.uvHandle,2,
                GLES20.GL_FLOAT,false,0,uvBuffer);
    }

    //イメージの描画
    public void drawImage(Texture texture,int x,int y) {
        drawImage(texture,x,y,texture.width,texture.height);
    }

    //イメージの描画
    public void drawImage(Texture texture,int x,int y,int w,int h) {
        drawImage(texture,x,y,w,h,0,0,texture.width,texture.height);
    }

    //イメージの描画
    public void drawImage(Texture texture,int dx,int dy,int dw,int dh,
                          int sx,int sy,int sw,int sh) {
        //ウィンドウ座標を正規化デバイス座標に変換
        float tw=(float)sw/(float)texture.width;
        float th=(float)sh/(float)texture.height;
        float tx=(float)sx/(float)texture.width;
        float ty=(float)sy/(float)texture.height;

        //テクスチャのバインド
        texture.bind();

        //テクスチャ行列の移動・拡縮
        Matrix.setIdentityM(GLES.texMatrix,0);
        Matrix.translateM(GLES.texMatrix,0,tx,ty,0.0f);
        Matrix.scaleM(GLES.texMatrix,0,tw,th,1.0f);
        GLES20.glUniformMatrix4fv(GLES.texMatrixHandle,1,
                false,GLES.texMatrix,0);

        //ウィンドウ座標を正規化デバイス座標に変換
        float mx=((float)dx/(float)screenW)*2.0f-1.0f;
        float my=((float)dy/(float)screenH)*2.0f-1.0f;
        float mw=((float)dw/(float)screenW);
        float mh=((float)dh/(float)screenH);

        //モデルビュー行列の移動・拡縮
        Matrix.setIdentityM(GLES.mMatrix,0);
        Matrix.translateM(GLES.mMatrix,0,mx+mw,-(my+mh),0.0f);
        Matrix.scaleM(GLES.mMatrix,0,mw,mh,1.0f);
        GLES20.glUniformMatrix4fv(GLES.mMatrixHandle,1,
                false,GLES.mMatrix,0);
        GLES.updateMatrix();

        //四角形の描画
        GLES20.glVertexAttribPointer(GLES.positionHandle,3,
                GLES20.GL_FLOAT,false,0,vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        //テクスチャのアンバインド
        texture.unbind();
    }
}
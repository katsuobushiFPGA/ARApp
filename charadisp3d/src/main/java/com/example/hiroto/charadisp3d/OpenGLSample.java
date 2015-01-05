package com.example.hiroto.charadisp3d;

/**
 * Created by hiroto on 2015/01/04.
 */

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLSample implements Renderer {
    int viewWidth = 0;
    int viewHeight = 0;
    Figure vvakame = null;

    @Override
    public void onDrawFrame(GL10 gl) {
        // Frame毎に呼び出される描画メソッド
        gl.glClearColor(0, 1, 1, 1);
        // 描画内容をクリアする
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        setUpCamera(gl);
        vvkame.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 大きさが変更されたときなどに呼び出される

        // 描画範囲を指定する
        gl.glViewport(0, 0, width, height);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        this.viewWidth = width;
        this.viewHeight = height;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }
    void setUpCamera(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        {
            final float fovY = 45.0f;
            final float aspect = (float) viewWidth / (float) viewHeight;
            final float near = 0.1f;
            final float far = 1000.0f;
            final float eyeX = -1.0f;
            final float eyeY = 5.0f;
            final float eyeZ = 5.0f;
            final float centerX = 0;
            final float centerY = 0;
            final float centerZ = 0;
            final float upX = 0;
            final float upY = 0;
            final float upZ = 0;
            GLU.gluPerspective(gl,fovY,aspect,near,far);
            GLU.gluLookAt(gl,eyeX,eyeY,eyeZ,centerX,centerY,centerZ,upX,upY,upZ);
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }
    void drawPoly(GL10 gl) {
        float positions[] = {
                //三角形1
                -0.5f, 0.5f, 0.0f,//頂点1
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,

                //三角形2
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
        //バッファで転送
        ByteBuffer vertices = ByteBuffer.allocateDirect(positions.length * 4);
        vertices.order(ByteOrder.nativeOrder());
        vertices.asFloatBuffer().put(positions);

        //描画
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glColor4f(0, 0, 1, 1);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3 * 2);
    }
}
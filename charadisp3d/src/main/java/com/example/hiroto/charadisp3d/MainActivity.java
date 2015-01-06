package com.example.hiroto.charadisp3d;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //フルスクリーン表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //タイトルバーを非表示に
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //CRenderオブジェクトの作成
        CRenderGLES renderer = new CRenderGLES(this);
        
        //GLSurfaceViewオブジェクトの作成
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        
        //GLSurfaceViewオブジェクトに対してCRenderGLESの内容を適用
        glSurfaceView.setRenderer(renderer);
        
        //ビューをglSurfaceViewに指定
        setContentView(glSurfaceView);
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	//テクスチャを削除する
    	//TextureManager.deleteAllTexture(Global.gl);
    }
    

}
package com.example.hiroto.charadisp3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class CRenderGLES implements GLSurfaceView.Renderer
{
	private Context mContext;
	private int width;
	private int height;
	
	public CRenderGLES(Context context)
	{
		mContext = context;
	}
	
	
//	private int mTexture;
//	
//	//描画するもの
//	private float[] sikaku = {
//			-1.0f, -1.0f, 0.0f,
//			1.0f, -1.0f, 0.0f,
//			-1.0f, 1.0f, 0.0f,
//			1.0f, 1.0f, 0.0f
//			
//	};
//
//	//描画物と色のインデックス
//	private int[] indices = {
//			0, 1, 2, 3
//	};
//
//	private float[] sikakuColor = {
//			1.0f, 0.0f, 0.0f, 1.0f,
//			0.0f, 1.0f, 0.0f, 1.0f,
//			0.0f, 0.0f, 1.0f, 1.0f,
//			0.5f, 0.5f, 0.0f, 1.0f
//	};
//	
//	private float[] coords = {
//			0.0f, 1.0f,
//			1.0f, 1.0f, 
//			0.0f, 0.0f, 
//			1.0f, 0.0f
//	};
	
	
	//objファイルのオブジェクト
	Obj obj;
	
	//描画する頂点、法線、テクスチャ配列
	private float[] obj_v;
	private float[] obj_vn;
	private float[] obj_vt;
	
	//ライトの設定
	float lightPos[]     = { 1.0f, 1.0f, 2.0f, 0.0f };
	float lightColor[]   = { 1.0f, 1.0f, 1.0f, 1.0f };
	float lightSpecular[] = { 1.0f, 1.0f, 1.0f, 1.0f };

	
	public void initGLES(GL10 gl)
	{
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
		//点と線のアンチエイリアス処理を有効にする
		gl.glEnable(GL10.GL_POINT_SMOOTH);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		//gl.glEnable(GL10.GL_POLYGON_SMOOTH);	//ポリゴン

		gl.glEnable(GL10.GL_NORMALIZE);		/*法線を有効化*/
		gl.glEnable(GL10.GL_DEPTH_TEST);	/*デプスを有効化*/

		gl.glShadeModel(GL10.GL_SMOOTH);	/*スムースシェーディングを有効化*/
		
		/*とりあえず画面をこの色でクリア*/
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

		/*ビューポートを設定する*/
		gl.glViewport(0, 0, (int)width, height);

		/*投影変換行列スタックを操作対象とする*/
		gl.glMatrixMode(GL10.GL_PROJECTION);

		/*P行列スタックをクリア(単位行列にする)*/
		gl.glLoadIdentity();

		//gluPerspective()の代わりにこうする
		//gl.glOrtho(-2.0f, 2.0f, -2.0f * width / height, 2.0f * width / height, -10.0f, 10.0f);

		//透視投影
		float aspect = (float)width / height;
		gl.glFrustumf(-aspect, aspect, -1.0f, 1.0f, 1.0f, 10.0f);
		
		/*幾何変換スタックの操作を対象とする*/
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		//drawSikakuAndTextureTest(gl);
		drawObjFileData(gl);
	}
	
	private void drawObjFileData(GL10 gl)
	{
		initGLES(gl);
				
		//カラーバッファ、デプスバッファのクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		//現在のモデルビュー行列を保存
		gl.glPushMatrix();
		
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		gl.glEnable(GL10.GL_CULL_FACE);		/*glCullFaceで指定された面を削除する*/
		gl.glCullFace(GL10.GL_BACK);		/*削除する面を指定する（ここでは裏面）*/
		
		//アルファブレンディングの有効化
		//gl.glEnable(GL10.GL_BLEND);
		
		//合成アルゴリズムを指定する
		//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		
		
		gl.glEnable(gl.GL_LIGHTING);
		gl.glEnable(gl.GL_LIGHT0);
		gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, makeFloatBuffer(lightPos));
		//gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, makeFloatBuffer(lightColor));
		//gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, makeFloatBuffer(lightSpecular));
		
		gl.glShadeModel(gl.GL_SMOOTH);

		

		//平行移動
		gl.glTranslatef(0.0f, 0.0f, -4.0f);
		//gl.glPointSize(2.0f);

		//回転移動
		//gl.glRotatef(55.0f, 0.0f, 1.0f, 0.5f);
		
		//テクスチャを有効
		//gl.glEnable(GL10.GL_TEXTURE_2D);
		
		//テクスチャオブジェクトの指定
		//gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
		/*
		for(int i = 0; i < obj_v.length; i++ )
		{
			Log.d("Obj", "floatbuf get: " + obj_v[i] + ", count: " +i);
		}
		*/
		
		
		
		for(int i = 0; i < obj.getObjSize(); i++)
		{
			MtlData mtl;
			float[] ambient = new float[4];		//環境光の反射成分
			float[] diffuse = new float[4];		//拡散反射の成分
			float[] specular = new float[4];		//鏡面反射の成分
			float specular_angle;				//鏡面反射の角度
			
			//mtlデータを取得
			mtl = obj.getMtlSeparateMtlData(i);
			
			//mtlファイルの環境光の反射成分を代入
			ambient[0] = mtl.ka.x;		//赤(R)
			ambient[1] = mtl.ka.y;		//緑(G)
			ambient[2] = mtl.ka.z;		//青(B)
			ambient[3] = 1.0f;
			
			//mtlファイルの拡散反射の成分を代入
			diffuse[0] = mtl.kd.x;		//赤(R)
			diffuse[1] = mtl.kd.y;		//緑(G)
			diffuse[2] = mtl.kd.z;		//青(B)
			diffuse[3] = 1.0f;
			
			//mtlファイルの鏡面反射の成分を代入
			specular[0] = mtl.ks.x;		//赤(R)
			specular[1] = mtl.ks.y;		//緑(G)
			specular[2] = mtl.ks.z;		//青(B)
			specular[3] = 1.0f;
			
			//mtlファイルの鏡面反射の角度を代入
			specular_angle = mtl.ns;
			
			//Log.d("Obj", "ambient: " + ambient[0] + ", " + ambient[1] + ", " + ambient[2] + ", " + ambient[3]);
			
			gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_AMBIENT, makeFloatBuffer(ambient));		//環境光反射の設定
			gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_DIFFUSE, makeFloatBuffer(diffuse));		//拡散反射の設定
			gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_SPECULAR, makeFloatBuffer(specular));	//鏡面反射の設定
			gl.glMaterialf(gl.GL_FRONT_AND_BACK, gl.GL_SHININESS, specular_angle);				//鏡面反射の鋭さの設定

			//テクスチャがあるかどうかを判定
			if(mtl.map_kd.length() > 0)
			{
				//テクスチャを有効
				gl.glEnable(GL10.GL_TEXTURE_2D);
				
				//テクスチャオブジェクトの指定
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mtl.textureID);
				
				//Log.d("Obj", "textureID: " + mtl.textureID);
			}

			//法線配列を有効化
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			
			//描画する法線配列を取得
			obj_vn = obj.getMtlSeparateNormals(i);
			
			//描画する法線配列を指定
			gl.glNormalPointer(GL10.GL_FLOAT, 0, makeFloatBuffer(obj_vn));
			
			//Log.d("Obj", "vn size: " + obj_vn.length);
			
			
			//テクスチャ配列を有効化
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			gl.glTexEnvx(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE);
			
			//描画するテクスチャ配列を取得
			obj_vt = obj.getMtlSeparateTexCoods(i);
			
			//描画するテクスチャ配列を指定
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(obj_vt));
			
			//Log.d("Obj", "vt size: " + obj_vt.length);
			
			
//			for(int j = 0; j < obj_vt.length; j++ )
//			{
//				Log.d("Obj", "floatbuf get: " + obj_vt[j] + ", count: " +j);
//			}
			
			
			
			
			//頂点配列を有効化
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			//描画する頂点配列を取得
			obj_v = obj.getMtlSeparateVertices(i);
			
			//描画する頂点配列を指定
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(obj_v));
			
			//Log.d("Obj", "v size: " + obj_v.length);
			
			//頂点配列、色配列の内容を描画する			
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (int)obj_v.length/3);
			
			
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			
			if(mtl.map_kd.length() > 0)
			{
				//テクスチャを有効
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}			
		}
				
		gl.glDisable(GL10.GL_CULL_FACE);
		
		//テクスチャを無効
		//gl.glDisable(GL10.GL_TEXTURE_2D);

		//gl.glDisable(GL10.GL_BLEND);
		
		gl.glEnable(gl.GL_LIGHT0);
		gl.glEnable(gl.GL_LIGHTING);
		gl.glPopMatrix();		
	}
	
	
//	private void drawSikakuAndTextureTest(GL10 gl)
//	{
//		initGLES(gl);
//		
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();
//
//		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
//		
//		//アルファブレンディングの有効化
//		gl.glEnable(GL10.GL_BLEND);
//		
//		//合成アルゴリズムを指定する
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//
//		/*カラーバッファ、デプスバッファのクリア*/
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//
//		/*現在のモデルビュー行列を保存*/
//		gl.glPushMatrix();
//
//		//平行移動
//		gl.glTranslatef(0.0f, 0.0f, -2.0f);
//
//		//回転移動
//		gl.glRotatef(20.0f, 1.0f, 0.0f, 0.5f);
//		
//		//テクスチャを有効
//		gl.glEnable(GL10.GL_TEXTURE_2D);
//		
//		//テクスチャオブジェクトの指定
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
//		
//		//描画物の頂点配列を指定
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(sikaku));
//
//		//頂点配列を有効化
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//
//		//描画する色配列を指定
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, makeFloatBuffer(sikakuColor));
//
//		//色配列を有効化
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		
//		//テクスチャ配列を指定
//		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(coords));
//		
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		
//		/*頂点配列、色配列の内容を描画する*/
//		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 4);
//		
//		//テクスチャを無効
//		gl.glDisable(GL10.GL_TEXTURE_2D);
//
//		gl.glDisable(GL10.GL_BLEND);
//		gl.glPopMatrix();
//	}
	
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		//Globalクラスにglを格納する
		Global.gl = gl;
		
		
//		this.mTexture = GLESUtil.loadTexture(gl, mContext, "android_image.png");
//		if(mTexture == 0)
//		{
//			Log.e(getClass().toString(), "テクスチャーが読み込めません！！　mTexture");
//		}
		
		
		//objファイルの内容を開く
		obj = new Obj(mContext, "testpensan.obj");
	}
		
	//システム上のメモリ領域を確保するためのメソッド
	public static final FloatBuffer makeFloatBuffer(float[] arr)
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(arr.length*4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuf = byteBuf.asFloatBuffer();
		floatBuf.put(arr);
		floatBuf.position(0);
		
		return floatBuf;
	}
}

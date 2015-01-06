package com.example.hiroto.charadisp3d;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.opengl.GLUtils;

public class GLESUtil 
{
	//loadTextureのオプション指定
	private static final BitmapFactory.Options options = new BitmapFactory.Options();
	static {
		//リソースの自動リサイズをしない
		options.inScaled = false;
		
		//32bit画像として読み込む
		options.inPreferredConfig = Config.ARGB_8888;
	}
	
	//テクスチャ画像を開いてテクスチャを作成する
	public static final int loadTexture(GL10 gl, Context context, String textureName)
	{ 
		int[] textures = new int[1];
		
		try
		{
			//Bitmapの作成
			/*Bitmap bmp = BitmapFactory.decodeResource(resources, resID);
			if(bmp == null)
			{
				return 0;
			}*/
			
			//テクスチャ画像
			Bitmap texImg, texImgFromFile;
			
			String toFile = textureName;
			
			//ファイルからBitmapを取得
			texImgFromFile = BitmapFactory.decodeFile("/data/data/" + context.getPackageName() + "/files/" + toFile);
			
			//画像をMatrixを使って反転させるため、matrixを作成してscaleを設定
			Matrix matrix = new Matrix();
			matrix.setScale(-1.0f, -1.0f);	//上下反転。左右反転(-1.0f, 1.0f)
			
			//画像を上下反転させる
			texImg = Bitmap.createBitmap(texImgFromFile, 0, 0, texImgFromFile.getWidth(), texImgFromFile.getHeight(), matrix, true);

			//OpenGL用のテクスチャを生成する
			gl.glGenTextures(1, textures, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texImg, 0);
			
			//テクスチャの貼り方　第3引数で元の色との混ぜ合わせができたりする）
			gl.glTexEnvf(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_MODULATE);
			
			gl.glTexParameterf( gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE );	//　s座標の1を超える端処理をループにしない
			gl.glTexParameterf( gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE );	//　t座標の1を超える端処理をループにしない
            
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);									
			
			//TextureManagerに登録する
			//TextureManager.addTexture(textures[0], texImg);
			
			//OpenGLへの転送が完了したので、VMメモリ上に作成したBitmapを破棄する。
			//TextureManager.GetTextureBitmap(gl, textures[0]).recycle();
			
			texImg.recycle();
			
		}
		catch(Exception e){}
		
		return textures[0];
	}
	
	private final void copyAndView(Context context) throws IOException 
    {
	        //AssetManager as = mContext.getResources().getAssets();
	        //InputStream is = as.open("android_img.png"); // アセットファイルのストリーム作成
	        //String toFile = "android_image.png";
	        //copy2Apk(is, toFile); //appローカルにコピー
	
	        //texImg = BitmapFactory.decodeFile("/data/data/" + context.getPackageName() + "/files/" + toFile);
	        //texImg.setImageBitmap(bit);
	        //is.close();
	        //as.close();
	}
	
	/*
	private void copy2Apk(Context context, InputStream input, String file) throws IOException 
	{
	    FileOutputStream output = context.openFileOutput(file, Context.MODE_WORLD_READABLE);
	    int DEFAULT_BUFFER_SIZE = 1024 * 4;
	    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	    int n = 0;
	    
	    while (-1 != (n = input.read(buffer))) 
	    {
	    	output.write(buffer, 0, n);
	    }
	    output.close();
	} 
	*/
}

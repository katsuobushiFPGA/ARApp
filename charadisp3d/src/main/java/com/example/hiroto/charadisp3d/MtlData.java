package com.example.hiroto.charadisp3d;

import android.util.Log;

public class MtlData {
	protected String name;		//マテリアル名
	
	protected Vector ka;			//環境光
	protected Vector kd;			//拡散反射光
	protected Vector ks;			//鏡面反射光
	protected float ns;			//鏡面反射光が働く角度
	
	protected String map_kd;		//テクスチャ画像のファイル名
	protected int textureID;		//テクスチャID（GLESUtilのloadTextureメソッドから返る値）
	
	public MtlData()
	{
		try{
			//Log.d("MtlData", "GET DATA!!");
			name = "";
			
			ka = new Vector();
			ka.x = 0.0f;
			ka.y = 0.0f;
			ka.z = 0.0f;
			
			kd = new Vector();
			kd.x = 0.0f;
			kd.y = 0.0f;
			kd.z = 0.0f;
			
			ks = new Vector();
			ks.x = 0.0f;
			ks.y = 0.0f;
			ks.z = 0.0f;
			
			ns = 0.0f;
			
			map_kd = "";
			textureID = -1;
			//Log.d("MtlData", "GET DATA!! 2");
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d("MtlData", e.toString());
		}
		
	}
	
	//マテリアル要素Kaを返す
	public Vector getMtlDataKa()
	{
		return ka;
	}
	
	//マテリアル要素Ksを返す
	public Vector getMtlDataKs()
	{
		return ks;
	}
	
	//マテリアル要素Kdを返す
	public Vector getMtlDataKd()
	{
		return kd;
	}
	
	//マテリアル要素Nsを返す
	public float getMtlDataNs()
	{
		return ns;
	}	
	
	//map_kdで指定されたテクスチャ名を返す
	public String getMtlDataMap_kd()
	{
		return map_kd;
	}
	
	//map_kdで指定されたテクスチャを示すIDを返す
	public float getMtlDataTexId()
	{
		return textureID;
	}
}

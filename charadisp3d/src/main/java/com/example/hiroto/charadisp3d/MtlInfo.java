package com.example.hiroto.charadisp3d;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import android.content.Context;
import android.util.Log;

public class MtlInfo{
	private MtlData mtl;
	
	//コンストラクタ
	public MtlInfo(Context context, String mtlFileName, String usemtlname)
	{
		BufferedReader in;
		try
		{
			//mtlファイルを開く
			FileInputStream inFile = context.openFileInput(mtlFileName);
			in = new BufferedReader( new InputStreamReader(inFile, "SJIS") );
			
			String line;
			String cmpStr;
			MessageFormat mf;
			Object[] result;
			
			while(true)
			{				
				line = in.readLine();
				
				//Log.d("Mtl", "ReadLine: " + line);
				
				if(line == null)
				{
					break;
				}
				else if(line.length() > 0 && !line.equals(""))
				{
					if(line.length() >= 6)
					{
						cmpStr = line.substring(0, 6);
						if(cmpStr.equals("newmtl"))		//mtlファイル名を保存
						{
							//lineで読みとった文字列のフォーマット（形式）を指定
							mf = new MessageFormat("newmtl {0}");
							
							//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
							result = mf.parse(line);
							
							//Log.d("Mtl", "newmtl: " + line);
							
							//引数で指定されたusemtlの名前を見つけた時
							if(usemtlname.equals((String)result[0]))
							{
								//Log.d("Mtl", "GET DATA!!");
								
								mtl = new MtlData();
								
								//Log.d("Mtl", "GET DATA!! 2");
								
								//usemtlが持つ名前を取得
								mtl.name = (String)result[0];
								//Log.d("Mtl", "mtl.name: " + (String)result[0]);
								//
								while(true)
								{
									line = in.readLine();
									//Log.d("Mtl", "line2: " + line);
									if(line == null)
									{
										break;
									}
									else if(line.length() > 0 && !line.equals(""))
									{
										if(line.length() >= 2)
										{
											cmpStr = line.substring(0, 6);
											if(cmpStr.equals("map_Kd"))		//map_Kd
											{
												//lineで読みとった文字列のフォーマット（形式）を指定
												mf = new MessageFormat("map_Kd {0}");
												
												//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
												result = mf.parse(line);
												
												mtl.map_kd = (String)result[0];
												
												//テクスチャ画像を開き、テクスチャIDを取得(glBindTexture()でこのIDを指定する)
												mtl.textureID = GLESUtil.loadTexture(Global.gl, context, mtl.map_kd);
												
												//Log.d("Mtl", "map_kd: " + mtl.map_kd);
												//Log.d("Mtl", "texID: " + mtl.textureID);
											}
											
											cmpStr = line.substring(0, 2);
											if(cmpStr.equals("Ka"))		//Ka
											{
												//lineで読みとった文字列のフォーマット（形式）を指定
												mf = new MessageFormat("Ka {0} {1} {2}");
												
												//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
												result = mf.parse(line);
												
												mtl.ka.x = Float.parseFloat((String)result[0]);
												mtl.ka.y = Float.parseFloat((String)result[1]);
												mtl.ka.z = Float.parseFloat((String)result[2]);
											}
											else if(cmpStr.equals("Kd"))		//Kd
											{
												//lineで読みとった文字列のフォーマット（形式）を指定
												mf = new MessageFormat("Kd {0} {1} {2}");
												
												//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
												result = mf.parse(line);
												
												mtl.kd.x = Float.parseFloat((String)result[0]);
												mtl.kd.y = Float.parseFloat((String)result[1]);
												mtl.kd.z = Float.parseFloat((String)result[2]);
											}
											else if(cmpStr.equals("Ks"))		//Ks
											{
												//lineで読みとった文字列のフォーマット（形式）を指定
												mf = new MessageFormat("Ks {0} {1} {2}");
												
												//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
												result = mf.parse(line);
												
												mtl.ks.x = Float.parseFloat((String)result[0]);
												mtl.ks.y = Float.parseFloat((String)result[1]);
												mtl.ks.z = Float.parseFloat((String)result[2]);
											}
											else if(cmpStr.equals("Ns"))		//Ns
											{
												//lineで読みとった文字列のフォーマット（形式）を指定
												mf = new MessageFormat("Ns {0}");
												
												//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
												result = mf.parse(line);
												
												mtl.ns = Float.parseFloat((String)result[0]);
												
												break;
											}											
										}
									}
									else if(line.length() == 0 && line.equals(""))
									{
										//Log.d("Obj", "continue");
										continue;
									}
								}
								
								//一番外側のwhileを抜ける
								break;
							}							
						}
					}					
				}
				else if(line.length() == 0 && line.equals(""))
				{
					//Log.d("Obj", "continue");
					continue;
				}
			}
		}
		catch (Exception e)
		{
			Log.d("MtlInfo", e.toString());
		}
	}
	
	//コンストラクタで作成したmtlオブジェクトを返す
	public MtlData getMtlData()
	{
		return mtl;
	}
}

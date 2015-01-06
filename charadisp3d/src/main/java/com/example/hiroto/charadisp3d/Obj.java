package com.example.hiroto.charadisp3d;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ContentHandler;
import java.nio.FloatBuffer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class Obj
{
	private ArrayList<TLOData> objData;
	
    //mtlファイルの情報
    private String     mtlfilename;
    //private ArrayList<MtlData> mtlArray;	//必要な部分だけ取り出す
    
    //## 以下の3つは削除してもいいかも
    //v
    ArrayList<Vector> vertices = new ArrayList<Vector>();      // 頂点座標配列 [num_vertices]

    //vn
    ArrayList<Vector>   normals = new ArrayList<Vector>();       // 法線ベクトル配列 [num_normals]

    //vt
    ArrayList<Vector>   tex_coords = new ArrayList<Vector>();    // テクスチャ座標配列 [num_tex_coords]
    
    
    //コンストラクタ。ここで引数で与えられたObjファイルを開き、値を取得する
	public Obj(Context context, String objFileName)
	{   
		BufferedReader in;
		try
		{
			//Log.d("Obj", "Constractor");
			

		    
		    objData = new ArrayList<TLOData>();
			
			//objファイルを開く
			FileInputStream inFile = context.openFileInput(objFileName);
			in = new BufferedReader( new InputStreamReader(inFile, "SJIS") );
								
			String line;
			String cmpStr;
			MessageFormat mf;
			Object[] result;
			Vector result_vec;
			
			//num_men = 0;
			//useMtlNameCnt = 0;
			TLOData objDataSeparateUsemtl = null;
			//MtlInfo info;
			
			//usemtlを初めて読み込んだらtrueにする
			boolean firstUsemtl = false;
			
			//while((line = in.readLine()) != null)
			
			while(true)
			{				
				line = in.readLine();
				if(line == null)
				{
					//Log.d("Obj", "break");
					break;
				}
				else if(line.length() > 0 && !line.equals(""))
				{
					//Log.d("Obj", line);
					
					//if(line.length() > 0)
					{						
						if(line.length() >= 6)
						{
							cmpStr = line.substring(0, 6);
							if(cmpStr.equals("mtllib"))		//mtlファイル名を保存
							{
								//lineで読みとった文字列のフォーマット（形式）を指定
								mf = new MessageFormat("mtllib {0}");
								
								//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
								result = mf.parse(line);
								
								//mtlファイル名を取得する
								mtlfilename = (String)result[0];
							}
							else if(cmpStr.equals("usemtl"))
							{
								//2回目のusemtlを読んだら
								if(firstUsemtl == true)
								{
									//Log.d("Obj", "objData len: " + objData.size());
									objData.add(objDataSeparateUsemtl);
									//Log.d("Obj", "objData2 len: " + objData.size());
								}
								
								//info = new MtlInfo();
								objDataSeparateUsemtl = new TLOData();
									
								//lineで読みとった文字列のフォーマット（形式）を指定
								mf = new MessageFormat("usemtl {0}");
								
								//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
								result = mf.parse(line);
								
								//Log.d("Obj", "line: " + line);
																
								//最初のusemtlを読んだ際、objdataにaddするのを防ぐため。以後常にtrue
								firstUsemtl = true;
								
								//Log.d("Obj", "MtlData");
								
								String usemtlStr = (String)result[0];
								
								//usemtlで指定されたマテリアル情報を取得する
								MtlInfo mtlinfo = new MtlInfo(context, mtlfilename, usemtlStr);
								objDataSeparateUsemtl.setMtl(mtlinfo.getMtlData());
								
								/* mtl情報取得テスト
								Log.d("Obj", "MtlData 2");
								
								MtlData mtldata;
								mtldata = objDataSeparateUsemtl.getMtl();
								
								
								
								Log.d("Obj", "MtlData 3");
								
								Log.d("Obj", "mtlFileName: " + mtlfilename);
								
								Log.d("Obj", "map_kd: " + mtldata.getMtlDataMap_kd());
								
								Vector v = mtldata.getMtlDataKa();
								Log.d("Obj", "Ka: " + v.x + ", " + v.y + ", " + v.z);
								
								v = mtldata.getMtlDataKs();
								Log.d("Obj", "Ks: " + v.x + ", " + v.y + ", " + v.z);
								
								v = mtldata.getMtlDataKd();
								Log.d("Obj", "Kd: " + v.x + ", " + v.y + ", " + v.z);
								
								float ns = mtldata.getMtlDataNs();
								Log.d("Obj", "Ns: " + ns);
								*/
							}
						}
						
						cmpStr = line.substring(0, 1);					
						if(cmpStr.equals("v"))
						{						
							//vnの行かどうか
							cmpStr = line.substring(1, 2);
							if(cmpStr.equals("n"))
							{						
								//lineで読みとった文字列のフォーマット（形式）を指定aaaa
								mf = new MessageFormat("vn {0} {1} {2}");
								
								//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
								result = mf.parse(line);
								
								
								//{0}の部分はresult[0]に対応。Object型なのでfloatに変換して取得
								result_vec = new Vector();
								result_vec.x = Float.parseFloat((String)result[0]);
								result_vec.y = Float.parseFloat((String)result[1]);
								result_vec.z = Float.parseFloat((String)result[2]);
								
								normals.add(result_vec);
								
								//Log.d("Obj", "result_vec:(vn) " + result_vec.x + ", " + result_vec.y + ", " + result_vec.z);
							}						
							//vtの行かどうか
							else if(cmpStr.equals("t"))
							{						
								//lineで読みとった文字列のフォーマット（形式）を指定
								mf = new MessageFormat("vt {0} {1}");
								
								//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
								result = mf.parse(line);
								
								//{0}の部分はresult[0]に対応。Object型なのでfloatに変換して取得
								result_vec = new Vector();
								result_vec.x = Float.parseFloat((String)result[0]);
								result_vec.y = Float.parseFloat((String)result[1]);
								result_vec.z = -999.0f;		//vtはz座標を持たないので-999.0として無効な数値と示す
								
								tex_coords.add(result_vec);
								
								//Vector v = vertices.get(0);
								//Log.d("Obj", "fNumData: " + v.x + ", " + v.y + ", " + v.z);
								
								//Log.d("Obj", "result_vec:(vt) " + result_vec.x + ", " + result_vec.y + ", " + result_vec.z);
							}
							//vn, vtのどちらでもなかったらvの行とみなす
							else
							{						
								//lineで読みとった文字列のフォーマット（形式）を指定
								mf = new MessageFormat("v {0} {1} {2}");
								
								//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
								result = mf.parse(line);
								
								//{0}の部分はresult[0]に対応。Object型なのでfloatに変換して取得
								result_vec = new Vector();
								result_vec.x = Float.parseFloat((String)result[0]);
								result_vec.y = Float.parseFloat((String)result[1]);
								result_vec.z = Float.parseFloat((String)result[2]);
								
								vertices.add(result_vec);
								
								//Vector v = vertices.get(vertices.size()-1);
								
								//Log.d("Obj", "result_vec:(v) " + result_vec.x + ", " + result_vec.y + ", " + result_vec.z);
								//Log.d("Obj", "get:(v) " + v.x + ", " + v.y + ", " + v.z);
							}
						}
						else if(cmpStr.equals("f"))
						{
							//Log.d("Obj", "result_f:(f) " + line);
							
							
							//lineで読みとった文字列のフォーマット（形式）を指定
							mf = new MessageFormat("f {0}/{1}/{2} {3}/{4}/{5} {6}/{7}/{8}");
							
							//読み取った文字列lineとフォーマットを比較し、{}となっている部分をresultへ格納する
							result = mf.parse(line);
							//Log.d("Obj", "result_f:(f) " + line);
							//Log.d("Obj", "result_f:(f) " + result[0] + ", " + result[1] + ", " + result[2] + ", " + result[3] + ", " + result[4] + ", " + result[5] + ", " + result[6] + ", " + result[7] + ", " + result[8]);
							//Log.d("Obj", "F Length: " + result.length);
							
							if(result.length == 9)
							{
								//int i = num_men * 3;
								for(int j = 0; j < result.length; j += 3)
								{				
									//Log.d("Obj", "j: " + j);
									objDataSeparateUsemtl.add_f_v_Number(Integer.parseInt((String)result[j]));
									objDataSeparateUsemtl.add_f_vt_Number(Integer.parseInt((String)result[j+1]));
									//objDataSeparateUsemtl.add_f_vt_Number(Integer.parseInt((String)result[7-j]));
									objDataSeparateUsemtl.add_f_vn_Number(Integer.parseInt((String)result[j+2]));
									
									//Log.d("Obj", "j2: " + j);
									
									//Log.d("Obj", "F result(j, j+3, j+6): " + result[j] + ", " + result[j+3] + ", " + result[j+6]);
									//int iv = objDataSeparateUsemtl.size_f_v_Number();
									//int ivn = objDataSeparateUsemtl.size_f_vn_Number();
									//int ivt = objDataSeparateUsemtl.size_f_vt_Number();
									//int iv = objDataSeparateUsemtl.get_f_v_Number(objDataSeparateUsemtl.size_f_v_Number()-1);
									//int ivn = objDataSeparateUsemtl.get_f_vn_Number(objDataSeparateUsemtl.size_f_vn_Number()-1);
									//int ivt = objDataSeparateUsemtl.get_f_vt_Number(objDataSeparateUsemtl.size_f_vt_Number()-1);
									//Log.d("Obj", "size: " + iv + ", " + ivn + ", " + ivn);
									
									
								}
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
			
			//Log.d("Obj", "objData len: " + objData.size());
			//最後のusemtl～ファイルの終わりまでに読んだ分をobjDataへ格納
			objData.add(objDataSeparateUsemtl);
			//Log.d("Obj", "objData2 len: " + objData.size());
			
			int count = 0;
			while(count < objData.size())
			{
				objDataSeparateUsemtl = objData.get(count);
				
				int fNum;
				
				//各objDataSeparateUsemtlが持っているfのデータをインデックスにしてv, vn, vtのデータをobjDataSeparateUsemtlが持つhashmapへ保存する。
				
				//v
				for(int i = 0; i < objDataSeparateUsemtl.size_f_v_Number(); i++)
				{
					//Log.d("Obj", "objData2 fv size: " + objDataSeparateUsemtl.size_f_v_Number());
					//hashmapのkeyを取得
					fNum = objDataSeparateUsemtl.get_f_v_Number(i);
					
					//fのインデックスなので-1する
					--fNum;
					
					//ArrayListへの保存
					objDataSeparateUsemtl.add_array_vertices(vertices.get(fNum));	
				}
				
				//Log.d("Obj", "objData2 vertices size: " + objDataSeparateUsemtl.size_vertices());
				
				//vn
				for(int i = 0; i < objDataSeparateUsemtl.size_f_vn_Number(); i++)
				{
					//hashmapのkeyを取得
					fNum = objDataSeparateUsemtl.get_f_vn_Number(i);
					
					//fのインデックスなので-1する
					--fNum;
					
					//hashmapへの保存
					objDataSeparateUsemtl.add_array_normals(normals.get(fNum));					
				}
				
				//vt（vtはVectorクラスだけど、実際には2次元座標データx, yのみ使用）
				for(int i = 0; i < objDataSeparateUsemtl.size_f_vt_Number(); i++)
				{
					//hashmapのkeyを取得
					fNum = objDataSeparateUsemtl.get_f_vt_Number(i);
					
					//Log.d("Obj", "vt Num: " + fNum);
					
					//fのインデックスなので-1する
					--fNum;
					
					//hashmapへの保存
					objDataSeparateUsemtl.add_array_tex_coords(tex_coords.get(fNum));					
				}
				
				count++;
			}
			
			
			in.close();
			//Log.d("Obj", "finish!!");
		}
		catch (Exception e)
		{
			Log.d("Obj", e.toString());
		}
		
	}
	
	//Objファイルの頂点配列を取得する。
	public float[] getVerticesArray()
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		Vector v;
		
		int count = 0;
		while(count < objData.size())
		{
			objDataSeparateUsemtl = objData.get(count);
			
			//haspmapのverticesから値(Vector)を前から順番に取得してretArrayを作成していく
			for(int i = 0; i < objDataSeparateUsemtl.size_vertices(); i++)
			{
				v = objDataSeparateUsemtl.get_array_vertices(i);
				retArray.add((float)v.x);
				retArray.add((float)v.y);
				retArray.add((float)v.z);
				
				//Log.d("Obj", "x: " + (float)v.x);
				//Log.d("Obj", "y: " + (float)v.y);
				//Log.d("Obj", "z: " + (float)v.z);
			}
			
			count++;
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
		
		/*
		//FloatBufferの作成
		FloatBuffer floatbuf = FloatBuffer.allocate(retArray.size() * 4);	//領域確保
		
		Log.d("Obj", "size: " + retArray.size());
		for(int i = 0; i < retArray.size()-1; i++)
		{
			//Log.d("Obj", "get: " + retArray.get(i));
			floatbuf.put(retArray.get(i));
			//Log.d("Obj", "floatbuf get: " + floatbuf.array()[i] + ", count: " +i);
		}
		*/
				
		//return floatbuf;
	}
	
	//Objファイルの法線配列を取得する。
	public float[] getNormalsArray()
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		Vector v;
		
		int count = 0;
		while(count < objData.size())
		{
			objDataSeparateUsemtl = objData.get(count);
			
			//haspmapのverticesから値(Vector)を前から順番に取得してretArrayを作成していく
			for(int i = 0; i < objDataSeparateUsemtl.size_normals(); i++)
			{
				v = objDataSeparateUsemtl.get_array_normals(i);
				retArray.add((float)v.x);
				retArray.add((float)v.y);
				retArray.add((float)v.z);
				
				//Log.d("Obj", "x: " + (float)v.x);
				//Log.d("Obj", "y: " + (float)v.y);
				//Log.d("Obj", "z: " + (float)v.z);
			}
			
			count++;
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
	}
	
	//Objファイルのテクスチャ配列(3次元)を取得する。
	public float[] getTexCoodsArray()
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		Vector v;
		
		int count = 0;
		while(count < objData.size())
		{
			objDataSeparateUsemtl = objData.get(count);
			
			//haspmapのverticesから値(Vector)を前から順番に取得してretArrayを作成していく
			for(int i = 0; i < objDataSeparateUsemtl.size_tex_coords(); i++)
			{
				v = objDataSeparateUsemtl.get_array_tex_coords(i);
				retArray.add((float)v.x);
				retArray.add((float)v.y);
				retArray.add((float)v.z);
				
				//Log.d("Obj", "x: " + (float)v.x);
				//Log.d("Obj", "y: " + (float)v.y);
				//Log.d("Obj", "z: " + (float)v.z);
			}
			
			count++;
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
	}
	
	//インデックスで指定された番号のobjDataの頂点配列をfloat[]を返す（objDataは、mtl適用範囲で区切られている）
	public float[] getMtlSeparateVertices(int index)
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		objDataSeparateUsemtl = objData.get(index);		
		Vector v;
		
		for(int i = 0; i < objDataSeparateUsemtl.size_vertices(); i++)
		{
			v = objDataSeparateUsemtl.get_array_vertices(i);
			retArray.add((float)v.x);
			retArray.add((float)v.y);
			retArray.add((float)v.z);
			
			//Log.d("Obj", "x: " + (float)v.x);
			//Log.d("Obj", "y: " + (float)v.y);
			//Log.d("Obj", "z: " + (float)v.z);
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
	}
	
	//インデックスで指定された番号のobjDataの法線配列をfloat[]を返す（objDataは、mtl適用範囲で区切られている）
	public float[] getMtlSeparateNormals(int index)
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		objDataSeparateUsemtl = objData.get(index);		
		Vector v;
		
		for(int i = 0; i < objDataSeparateUsemtl.size_normals(); i++)
		{
			v = objDataSeparateUsemtl.get_array_normals(i);
			retArray.add((float)v.x);
			retArray.add((float)v.y);
			retArray.add((float)v.z);
			
			//Log.d("Obj", "x: " + (float)v.x);
			//Log.d("Obj", "y: " + (float)v.y);
			//Log.d("Obj", "z: " + (float)v.z);
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
	}
	
	//インデックスで指定された番号のobjDataの頂点配列をfloat[]を返す（objDataは、mtl適用範囲で区切られている）
	public float[] getMtlSeparateTexCoods(int index)
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		TLOData objDataSeparateUsemtl = null;
		objDataSeparateUsemtl = objData.get(index);		
		Vector v;
		
		for(int i = 0; i < objDataSeparateUsemtl.size_tex_coords(); i++)
		{
			v = objDataSeparateUsemtl.get_array_tex_coords(i);
			retArray.add((float)v.x);
			retArray.add((float)v.y);
			//retArray.add((float)v.z);
			
			//Log.d("Obj", "x: " + (float)v.x);
			//Log.d("Obj", "y: " + (float)v.y);
			//Log.d("Obj", "z: " + (float)v.z);
		}
		
		//arrayListをfloat[]に変換する
		Float[] ret = (Float[])retArray.toArray(new Float[0]);
		float[] ret2 = new float[ret.length];
		
		for(int i = 0 ; i < ret.length; i++)
		{
			ret2[i] = ret[i].floatValue();
		}
		
		return ret2;
	}
	
	//インデックスで指定された番号のobjDataの頂点配列をfloat[]を返す（objDataは、mtl適用範囲で区切られている）
	public MtlData getMtlSeparateMtlData(int index)
	{
		ArrayList<Float> retArray = new ArrayList<Float>();	//返す配列（ArrayListから配列に変換して返す）
		
		TLOData objDataSeparateUsemtl = null;
		objDataSeparateUsemtl = objData.get(index);
		
		MtlData mtldata = new MtlData();
		mtldata = objDataSeparateUsemtl.getMtl();
		
		return mtldata;
	}
	
	//objDataの個数（マテリアルごとに区切られている数）を返す。この回数分ループして描画処理を行う
	public int getObjSize()
	{
		return objData.size();
	}
}

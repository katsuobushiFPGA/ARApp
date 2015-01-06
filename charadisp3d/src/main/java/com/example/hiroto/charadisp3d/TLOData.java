package com.example.hiroto.charadisp3d;

import java.util.ArrayList;
import java.util.TreeMap;

import android.util.Log;

public class TLOData
{
	private ArrayList<Vector> vertices;      // 頂点座標配列 
	private ArrayList<ArrayList<Float>> vertices_weight;	//重み。TreeMapのキーはverticesに対応。
	
	private ArrayList<Vector> normals;       // 法線ベクトル配列
	private ArrayList<Vector> tex_coords;    // テクスチャ座標配列
	
	private ArrayList<Integer>      men_v_no;				// 各頂点の頂点座標番号配列 [num * 3]
	private ArrayList<Integer>      men_vn_no;				// 各頂点の法線ベクトル番号配列 [num * 3]
	private ArrayList<Integer>      men_vt_no;				// 各頂点のテクスチャ座標番号配列 [num * 3]
	
	private MtlData useMtl;		//usemtlで指定されたマテリアルデータ
	
	public TLOData()
	{
		vertices = new ArrayList<Vector>();
		vertices_weight = new ArrayList<ArrayList<Float>>();
		normals = new ArrayList<Vector>();
		tex_coords = new ArrayList<Vector>();
		
		men_v_no = new ArrayList<Integer>();
		men_vn_no = new ArrayList<Integer>();
		men_vt_no = new ArrayList<Integer>();
	}
	
	public void setMtl(MtlData mtldata)
	{
		useMtl = mtldata;
	}
	
	public MtlData getMtl()
	{
		return useMtl;
	}
	
	//objファイルfのvの番号をmen_v_noに格納する
	public void add_f_v_Number(int vNumber)
	{
		men_v_no.add(vNumber);
	}
	
	//objファイルfのvnの番号をmen_vn_noに格納する
	public void add_f_vn_Number(int vnNumber)
	{
		men_vn_no.add(vnNumber);
	}
	
	//objファイルfのvtの番号をmen_vt_noに格納する
	public void add_f_vt_Number(int vtNumber)
	{
		men_vt_no.add(vtNumber);
	}
	
	//indexで指定した値をmen_v_noから取得する
	public Integer get_f_v_Number(int index)
	{
		return men_v_no.get(index);
	}
	
	//indexで指定した値をmen_vn_noから取得する
	public Integer get_f_vn_Number(int index)
	{
		return men_vn_no.get(index);
	}
	
	//indexで指定した値をmen_vt_noから取得する
	public Integer get_f_vt_Number(int index)
	{
		return men_vt_no.get(index);
	}
	
	//men_v_noのサイズを取得する
	public int size_f_v_Number()
	{
		return men_v_no.size();
	}
	
	//men_vn_noのサイズを取得する
	public int size_f_vn_Number()
	{
		return men_vn_no.size();
	}
	
	//men_vt_noのサイズを取得する
	public int size_f_vt_Number()
	{
		return men_vt_no.size();
	}
	
	//verticesにキーと値を格納する
	public void add_array_vertices(Vector value)
	{
		vertices.add(value);
	}
	
	//normalsにキーと値を格納する
	public void add_array_normals(Vector value)
	{
		normals.add(value);
	}
	
	//tex_coordsにキーと値を格納する
	public void add_array_tex_coords(Vector value)
	{
		tex_coords.add(value);
	}
	
	//verticesのキーに対応した値を返す
	public Vector get_array_vertices(int key)
	{
		//Vector v = vertices.get(key);
		//Log.d("Obj", "x: " + v.x);
		return vertices.get(key);
	}
	
	//verticesのキーに対応した値を返す
	public Vector get_array_normals(int key)
	{
		return normals.get(key);
	}
	
	//verticesのキーに対応した値を返す
	public Vector get_array_tex_coords(int key)
	{
		return tex_coords.get(key);
	}
	
	//verticesのサイズを取得する
	public int size_vertices()
	{
		return vertices.size();
	}
	
	//normalsのサイズを取得する
	public int size_normals()
	{
		return normals.size();
	}
	
	//tex_coordsのサイズを取得する
	public int size_tex_coords()
	{
		return tex_coords.size();
	}
}

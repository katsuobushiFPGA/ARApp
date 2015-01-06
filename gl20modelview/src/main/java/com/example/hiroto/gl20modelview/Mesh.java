package com.example.hiroto.gl20modelview;

/**
 * Created by hiroto on 2015/01/06.
 */

//メッシュ
public class Mesh {
    public VertexBuffer vertexBuffer;//頂点バッファ
    public IndexBuffer  indexBuffer; //インデックスバッファ
    public Material     material;    //マテリアル

    //描画
    public void draw() {
        material.bind();
        vertexBuffer.bind();
        indexBuffer.draw();
        vertexBuffer.unbind();
        material.unbind();
    }
}
package com.example.hiroto.gpsarapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GooglePlacesAPIから受け取ったJSONオブジェクトを解析する.
 * @author hiroto
 *
 */
public class parseJsonOfPlacesAPI {
    /**
     * GooglePlacesAPIから受け取ったJSONオブジェクトを解析する.
     * @param jsonObject jsonデータ
     * @return ルートリスト
     */
    public List<String[]> parse(JSONObject jsonObject) {
        List<String[]> list = new ArrayList<>();
        JSONArray jsonResults = null;
        // 予報情報の一覧を取得
        try {
            jsonResults = jsonObject.getJSONArray("results");// { "geometry","icon"."id","name","rating","reference","types","vicinity"}
            for(int i=0;i < jsonResults.length();i++) {
                JSONObject geometry = jsonResults.getJSONObject(0);
                JSONObject location = geometry.getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                JSONObject name = jsonResults.getJSONObject(3);
                String name_ = name.getString("name");
                list.add(new String[]{name_,lat,lng});
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
package com.example.hiroto.gpsarapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GooglePlacesAPI
 * @author hiroto
 *
 */
public class parseJsonOfPlacesAPI {
    /**
     * GooglePlacesAPI����󂯎����JSON�I�u�W�F�N�g����͂���.
     * @param jsonObject json�f�[�^
     * @return ���[�g���X�g
     */
    public List<String[]> parse(JSONObject jsonObject) {
        List<String[]> list = new ArrayList<>();
        JSONArray jsonResults = null;
        // �\����̈ꗗ���擾
        try {
            jsonResults = jsonObject.getJSONArray("results");// { "geometry","icon"."id","name","rating","reference","types","vicinity"}
            for(int i=0;i < jsonResults.length();i++) {
                JSONObject jsonElem = jsonResults.getJSONObject(i);
                JSONObject geometry = jsonElem.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                String name = jsonElem.getString("name");
                Log.d("lat",lat);
                Log.d("lng",lng);
                Log.d("name",name);
                list.add(new String[]{name,lat,lng});
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
package com.example.hiroto.gpsarapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GooglePlacesAPI����󂯎����JSON�I�u�W�F�N�g����͂���.
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
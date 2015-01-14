package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class PlacesActivity extends Activity {
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("ルート検索中...");
        progressDialog.hide();

        placesSearch(new LatLng(35.6814,139.7674));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
    //----ルート検索用メソッド----

    /**
     * 第一引数から第二引数までのルートを探索する.
     * ルート情報等は全てNavigationManagerクラスが管理する.
     * @param origin 起点
     */
    private void placesSearch(LatLng origin){
        progressDialog.show();
        String url = getDirectionsUrl(origin,"lodging");
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    /**
     * 第一引数から第二引数までのルートのURLを取得する.
     * @param origin 起点
     * @param type
     * @return 成功時 DirectionsAPIのURL
     */
    private String getDirectionsUrl(LatLng origin,String type){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String rad = "3000";
        String sensor = "sensor=false";
        String apiKey = "AIzaSyD6dulvTapDNQwpyPlbonwvg674BDV1xAw";
        //パラメータ
        String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + origin.latitude + "," +
                                        origin.longitude + "&radius=" + rad + "&types="+ type + "&language=ja" + "&" + sensor + "&key=" + apiKey;
        return url;
    }

    /**
     * URLから情報をダウンロードする.
     * @param strUrl URL
     * @return 成功時 DirectionsAPIのデータ
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("DATA",data);

        return data;
    }

    /**
     * 非同期,バックグラウンドにルート情報を取得する
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        /**
         * バックグラウンドでdataをダウンロードする.
         * @param url
         * @return
         */
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            Log.d("DATA2",data);
            return data;
        }

        /**
         * parseJsonOfDirectionsAPIにてJSONをparseする.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /**
     *  非同期に,JSONを解析する
     */
    private class ParserTask extends AsyncTask<String, Integer,List<String[]> > {
        /**
         * ルート情報を取得する
         * @param jsonData ルートデータ
         * @return 緯度経度のリスト
         */
        @Override
        protected List<String[]> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<String[]> places = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                parseJsonOfPlacesAPI parser = new parseJsonOfPlacesAPI();
                places = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("aaaa",String.valueOf(places));
            return places;
        }

        /**
         *  ルート検索で得た座標を使ってポリラインで経路表示
         * @param result
         */
        @Override
        protected void onPostExecute(List<String[]> result) {
            progressDialog.hide();
        }
    }
}

package com.example.hiroto.gpsarapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hiroto on 2014/12/31.
 */
public final class NavigationManager {
    private static boolean isNavigation;
    //ナビゲーション用
    private static LatLng target;//目的地
    private static String posinfo;//ナビゲーション,ルート情報
    private static List<List<HashMap<String,String>>> route;//ナビゲーション経路の緯度経度
    private static String travelMode="driving";//ナビゲーションモード

    //インスタンスをつくらせないようにする。
    private NavigationManager(){};

    public static void setNavigationReverse(){
        isNavigation = !isNavigation;
    }
    public static void setNavigationFlag(boolean b){
        isNavigation = b;
    }
    public static boolean getNavigationFlag(){
        return isNavigation;
    }
    public static void setPosInfo(String pos){
        posinfo = pos;
    }
    public static String getPosinfo(){
        return posinfo;
    }
    public static void setRoute(List<List<HashMap<String,String>>> r){
       route = r;
    }
    public static List<List<HashMap<String,String>>> getRoute(){
        return route;
    }
    public static void setTravelMode(String tMode){
        travelMode = tMode;
    }
    public static String getTravelMode(){
        return travelMode;
    }
    public static void setTarget(LatLng t) {
        target = t;
    }
    public static LatLng getTarget() {
        return target;
    }

}

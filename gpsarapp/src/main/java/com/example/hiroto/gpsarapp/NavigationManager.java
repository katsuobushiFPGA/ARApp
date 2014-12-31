package com.example.hiroto.gpsarapp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hiroto on 2014/12/31.
 */
public final class NavigationManager {
    private static boolean isNavigation;
    //ナビゲーション用
    private static String posinfo;
    private static List<List<HashMap<String,String>>> route;
    private static String travelMode="driving";

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
}

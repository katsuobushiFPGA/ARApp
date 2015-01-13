package com.example.hiroto.gpsarapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Navigationの情報を管理するクラス
 * @author hiroto
 */
public final class NavigationManager {
    private static boolean isNavigation;
    //ナビゲーション用
    private static LatLng target;//目的地
    private static String posinfo;//ナビゲーション,ルート情報
    private static List<List<HashMap<String, String>>> route;//ナビゲーション経路の緯度経度
    private static String travelMode = "driving";//ナビゲーションモード
    private static String info;

    //インスタンスをつくらせないようにする。
    private NavigationManager() {
    }

    /**
     * ナビゲーション状態かどうかを設定する.
     * @param b 設定する値
     */
    public static void setNavigationFlag(boolean b) {
        isNavigation = b;
    }

    /**
     * ナビゲーションの状態を取得する.
     * @return 成功時 フィールドのナビゲーションの値 true false
     */
    public static boolean getNavigationFlag() {
        return isNavigation;
    }

    /**
     * 観光地のルート情報を設定する.
     * @param pos ルート情報
     */
    public static void setPosInfo(String pos) {
        posinfo = pos;
    }

    /**
     * 観光地のルート情報を取得する
     * @return 成功時 ルート情報
     */
    public static String getPosinfo() {
        return posinfo;
    }

    /**
     * 緯度経度のルート情報を設定する.
     * @param r ルートリスト
     */
    public static void setRoute(List<List<HashMap<String, String>>> r) {
        route = r;
    }

    /**
     * 緯度経度のルート情報を設定する.
     * @return ルートリスト
     */
    public static List<List<HashMap<String, String>>> getRoute() {
        return route;
    }

    /**
     * トラベルモードを設定する.
     * driving,bicycling,walking
     * @param tMode モード
     */
    public static void setTravelMode(String tMode) {
        travelMode = tMode;
    }

    /**
     * トラベルモードを取得する.
     * @return トラベルモード
     */
    public static String getTravelMode() {
        return travelMode;
    }

    /**
     * 目的地の緯度経度を設定する.
     * @param t 緯度経度
     */
    public static void setTarget(LatLng t) {
        target = t;
    }

    /**
     * 目的地の緯度経度を取得する.
     * @return
     */
    public static LatLng getTarget() {
        return target;
    }

    /**
     * 目的地の名前を設定する.
     * @param name 目的地名
     */
    public static void setInfo(String name) {
        info = name;
    }

    /**
     * 目的地の名前を取得する.
     * @return 目的地名
     */
    public static String getInfo() {
        return info;
    }

}

package com.example.hiroto.gpsarapp;

/**
 * Created by hiroto on 2014/12/24.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;

import java.util.ArrayList;

//public class ARView extends SurfaceView implements SurfaceHolder.Callback {
public class ARView extends View {
    //
    private ARActivity arActivity;
    // カメラの画角を指定する
    private final int ANGLE = 60;
    // ARテキストの見える範囲を指定する
    // ここでは10kmほどに指定する
    private final float VIEW_LIMIT = 100000;

    // コンパスの描画位置および大きさを指定する
    private final float POS_COMPASSX = 40;
    private final float POS_COMPASSY = 40;
    private final float POS_COMPASS_SIZE = 2;

    // 方向を保持する変数
    float direction;

    // 現在地を保持する変数
    int posx, posy;

    // ARテキストの情報を保持するオブジェクト
    private ArrayList<GPSData> list;

    // ディスプレイサイズ
    private int displayX;

    public ARView(Context context, Cursor cursor) {
        super(context);
        arActivity = (ARActivity)context;
        // データベースの読み込みを行う
        readTable(cursor);

        // 画面サイズの取得
        Display disp = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displayX = disp.getWidth();
    }

    public void readTable(Cursor cursor) {
        // データベースに保存されている
        // 全てのARテキストの情報をlistに読み込む
        if (list != null)
            list.clear();
        list = new ArrayList<GPSData>();

        cursor.moveToFirst();
        do {
            GPSData data = new GPSData();
            data.info = cursor.getString(0);
            data.latitude = cursor.getInt(1);
            data.longitude = cursor.getInt(2);
            data.image = cursor.getString(3);
            data.description = cursor.getString(4);
            list.add(data);
        } while (cursor.moveToNext());
    }

    class GPSData {
        public String info;
        public int latitude;
        public int longitude;
        public String image;
        public String description;
    }

    // 描画処理
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int i = 0; i < list.size(); i++) {
            // データの読み込み
            GPSData data = list.get(i);
            String info = data.info;
            int y = data.latitude;
            int x = data.longitude;

            // ARテキストとの距離を求める
            double dx = (x - posx);
            double dy = (y - posy);
            float distance = (float) Math.sqrt(Math.pow(dy, 2)
                    + Math.pow(dx, 2));

            // ARテキストとの距離が一定以上離れていたら、処理を行わずに次のARテキストの処理を行う
            if (distance > VIEW_LIMIT) {
                continue;
            }

            // ARテキストと現在地のなす角を求めて正規化する
            double angle = Math.atan2(dy, dx);
            float degree = (float) Math.toDegrees(angle);
            degree = -degree + 90;
            if (degree < 0)
                degree = 360 + degree;

            // 端末の向きとARテキストとの角度の差を求める
            float sub = degree - direction;
            if (sub < -180.0)
                sub += 360;
            if (sub > 180.0)
                sub -= 360;

            // ARテキストが視野に存在すれば描画処理を行う
            if (Math.abs(sub) < (ANGLE / 2)) {
                // 距離によってARテキストのサイズを決める
                float textSize = 50 * (float) (VIEW_LIMIT - distance)
                        / VIEW_LIMIT;
                paint.setTextSize(textSize);

                // ARテキストの描画を描画する
                float textWidth = paint.measureText(info);
                float diff = (sub / (ANGLE / 2)) / 2;
                float left = (displayX / 2 + displayX * diff) - (textWidth / 2);
                drawBalloonText(canvas, paint, info , left, 55);
            }
        }

        // コンパスを描画する
        drawCompass(canvas, paint,POS_COMPASS_SIZE);
    }

    private void drawBalloonText(Canvas canvas, Paint paint, String text,
                                 float left, float top) {
        // 文字列の幅を取得
        float textWidth = paint.measureText(text);
        // フォント情報の取得
        FontMetrics fontMetrics = paint.getFontMetrics();

        // 文字列の5ポイント外側を囲む座標を求める
        float bLeft = left - 5;
        float bRight = left + textWidth + 5;
        float bTop = top + fontMetrics.ascent - 5;
        float bBottom = top + fontMetrics.descent + 5;

        // 吹き出しの描画
        RectF rectF = new RectF(bLeft, bTop, bRight, bBottom);
        paint.setColor(Color.LTGRAY);
        canvas.drawRoundRect(rectF, 5, 5, paint);

        // 三角形の描画
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = new Path();
        float center = left + textWidth / 2;
        float triangleSize = paint.getTextSize() / 3;
        path.moveTo(center, bBottom + triangleSize);
        path.lineTo(center - triangleSize / 2, bBottom - 1);
        path.lineTo(center + triangleSize / 2, bBottom - 1);
        path.lineTo(center, bBottom + triangleSize);
        canvas.drawPath(path, paint);

        // 文字列の描画
        paint.setColor(Color.WHITE);
        canvas.drawText(text, left, top, paint);
    }

    //タッチイベントの処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            for (int i = 0; i < list.size(); i++) {
                // データの読み込み
                GPSData data = list.get(i);
                String info = data.info;
                int y = data.latitude;
                int x = data.longitude;
                String image = data.image;
                String description = data.description;

                // ARテキストとの距離を求める
                double dx = (x - posx);
                double dy = (y - posy);
                float distance = (float) Math.sqrt(Math.pow(dy, 2)
                        + Math.pow(dx, 2));

                // ARテキストとの距離が一定以上離れていたら、処理を行わずに次のARテキストの処理を行う
                if (distance > VIEW_LIMIT) {
                    continue;
                }

                // ARテキストと現在地のなす角を求めて正規化する
                double angle = Math.atan2(dy, dx);
                float degree = (float) Math.toDegrees(angle);
                degree = -degree + 90;
                if (degree < 0)
                    degree = 360 + degree;

                // 端末の向きとARテキストとの角度の差を求める
                float sub = degree - direction;
                if (sub < -180.0)
                    sub += 360;
                if (sub > 180.0)
                    sub -= 360;

                // ARテキストが視野に存在すれば描画処理を行う
                if (Math.abs(sub) < (ANGLE / 2)) {
                    // 距離によってARテキストのサイズを決める
                    float textSize = 50 * (float) (VIEW_LIMIT - distance)
                            / VIEW_LIMIT;
                    paint.setTextSize(textSize);               // ARテキストの描画を描画する
                    float textWidth = paint.measureText(info);
                    float diff = (sub / (ANGLE / 2)) / 2;
                    float left = (displayX / 2 + displayX * diff) - (textWidth / 2);
                    if ((left <= event.getX() && event.getX() <= left + textWidth) && (0 <= event.getY() && event.getY() <= 55)) {
                        //Intentの発行および、SpotActivityの呼び出し
                        Intent intent = new Intent(arActivity, SpotActivity.class);
                        intent.putExtra("lat",y);
                        intent.putExtra("lng",x);
                        intent.putExtra("info",info);
                        intent.putExtra("image",image);//infoを送る。(観光地)
                        intent.putExtra("description",description);//説明文を送る。(観光地)
                        arActivity.startActivity(intent);
                    }
                }
            }
        }
        return true;
    }

    // コンパスの描画
    private void drawCompass(Canvas canvas, Paint paint,double size) {
        Path path = new Path();
        path.moveTo(POS_COMPASSX, POS_COMPASSY - 20 * (float)size);
        path.lineTo(POS_COMPASSX + 10 * (float)size, POS_COMPASSY + 10 * (float)size);
        path.lineTo(POS_COMPASSX - 10 * (float)size, POS_COMPASSY + 10 * (float)size);
        path.moveTo(POS_COMPASSX, POS_COMPASSY - 20 * (float)size);
        paint.setColor(Color.RED);
        canvas.rotate(-direction, POS_COMPASSX, POS_COMPASSY);
        canvas.drawPath(path, paint);
        canvas.rotate(direction, POS_COMPASSX, POS_COMPASSY);
//        paint.setTextSize(12);
//        paint.setColor(Color.WHITE);
//        String str = new String();
//        canvas.drawText(str, 5, POS_COMPASSY * 3, paint);
    }

    public void drawScreen(float preDirection, GeoPoint geoPoint) {
        // センサーの値から端末の向きを計算する
        direction = (preDirection + 450) % 360;
        // 座標情報の取得
        if (geoPoint != null) {
            posy = geoPoint.getLatitudeE6();
            posx = geoPoint.getLongitudeE6();
        }
        // onDrawを呼び出して再描画
        invalidate();
    }
}

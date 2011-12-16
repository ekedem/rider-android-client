package com.rider.view;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathStation extends Overlay {

    private GeoPoint gp1;
    private GeoPoint gp2;
    private int pathColor;

    public PathStation(GeoPoint gp1, GeoPoint gp2, int pathColor) {
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.pathColor = pathColor;
    }

    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
            long when) {
        // TODO Auto-generated method stub
        Projection projection = mapView.getProjection();
        if (shadow == false) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Point point = new Point();
            projection.toPixels(gp1, point);
            paint.setColor(this.pathColor);
            Point point2 = new Point();
            projection.toPixels(gp2, point2);
            paint.setStrokeWidth(2);
            canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,
                    (float) point2.y, paint);
        }
        return super.draw(canvas, mapView, shadow, when);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // TODO Auto-generated method stub

        super.draw(canvas, mapView, shadow);
    }

}
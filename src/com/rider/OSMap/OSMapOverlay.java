package com.rider.OSMap;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView.Projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class OSMapOverlay extends org.osmdroid.views.overlay.Overlay
{
	private Context context;
	private int icon;
	IGeoPoint point;
	
	public OSMapOverlay(Context context, int icon) {
		super(context);
		this.context = context;
		this.icon = icon;
	}
	
	public void setPoint(IGeoPoint point) {
		this.point = point;
	}
	
    @Override
    public void draw(Canvas canvas, org.osmdroid.views.MapView mapView,boolean shadow) 
    {
//        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point screenPts = new Point();
        mapView.getProjection().toPixels(point, screenPts);
//
//        //---add the marker---
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), icon);            
        canvas.drawBitmap(bmp, screenPts.x, screenPts.y- bmp.getHeight(), null); 
    	
//    	Paint lp3;
//        lp3 = new Paint();
//        lp3.setColor(Color.RED);
//        lp3.setAntiAlias(true);
//        lp3.setStyle(Style.STROKE);
//        lp3.setStrokeWidth(1);
//        lp3.setTextAlign(Paint.Align.LEFT);
//        lp3.setTextSize(12);
//        final Rect viewportRect = new Rect();
//        final Projection projection = mapView.getProjection();
//        viewportRect.set(projection.getScreenRect());
//        // Draw a line from one corner to the other
//        canvas.drawLine(viewportRect.left, viewportRect.top,
//                viewportRect.right, viewportRect.bottom, lp3);
    	
    }

//    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
//    {   
//        //---when user lifts his finger---
//        if (event.getAction() == 1) {                
//            GeoPoint p = mapView.getProjection().fromPixels(
//                (int) event.getX(),
//                (int) event.getY());
//                Toast.makeText(context, 
//                    p.getLatitudeE6() / 1E6 + "," + 
//                    p.getLongitudeE6() /1E6 , 
//                    Toast.LENGTH_SHORT).show();
//        }                            
//        return false;
//    }        
} 

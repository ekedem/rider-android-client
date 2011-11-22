package com.rider.googleMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapOverlay extends com.google.android.maps.Overlay
{
	private Context context;
	private int icon;
	GeoPoint point;
	
	public MapOverlay(Context context, int icon) {
		this.context = context;
		this.icon = icon;
	}
	
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	
    @Override
    public boolean draw(Canvas canvas, MapView mapView,boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point screenPts = new Point();
        mapView.getProjection().toPixels(point, screenPts);

        //---add the marker---
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), icon);            
        canvas.drawBitmap(bmp, screenPts.x, screenPts.y- bmp.getHeight(), null);         
        return true;
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

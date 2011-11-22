package com.rider.OSMap;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.rider.view.RiderUi;



public class MyOSMapMarkers extends org.osmdroid.views.overlay.ItemizedOverlay {


	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private RiderUi ui;
	private Drawable defaultMarker;


	public void setUi(RiderUi ui) {
		this.ui = ui;
	}

	public MyOSMapMarkers(Drawable defaultMarker, Context context) {
		super(defaultMarker, new DefaultResourceProxyImpl(context));
		this.defaultMarker = defaultMarker;
		this.context = context;
	}

	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}


	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate(); 
	}

	
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		ui.showBusDialog(item.getTitle());
//		return true;
		
//		OverlayItem item = mOverlays.get(index);
//		 AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
//		 dialog.setTitle(item.getTitle());
//		 dialog.setMessage(item.getSnippet());
//		 dialog.show();
		 return true;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false);
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, MapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}

package com.rider.googleMap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.rider.view.RiderUi;

public class MyMapMarkers extends ItemizedOverlay {


	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private RiderUi ui;
	private Drawable defaultMarker;

	public MyMapMarkers(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.defaultMarker = defaultMarker;
	}

	public void setDefaultMarker(Drawable marker){
		this.defaultMarker = defaultMarker;
	}
	
	public void setUi(RiderUi ui) {
		this.ui = ui;
	}

	public MyMapMarkers(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.defaultMarker = defaultMarker;
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}


	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void clear() {
        mOverlays.clear();
        populate();
        setLastFocusedIndex(-1);
    }

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate(); 
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		ui.showBusDialog(item.getTitle());
		//	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//	  dialog.setTitle(item.getTitle());
		//	  dialog.setMessage(item.getSnippet());
		//	  dialog.show();
		return true;
	}

	public void  draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false);
	}

}

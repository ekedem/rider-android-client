package com.rider.googleMap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapMarkers extends ItemizedOverlay {

	 private Context context;
	 private Drawable defaultMarker;
	 private ArrayList<OverlayItem> mOverlays;

	 public MapMarkers(Drawable defaultMarker, Context context) {
	      super(boundCenterBottom(defaultMarker));
	      this.defaultMarker = defaultMarker;
	      this.context = context;
	      mOverlays = new ArrayList<OverlayItem>();
	 }

	 @Override
	 protected OverlayItem createItem(int i) {
	      // TODO Auto-generated method stub
	      return mOverlays.get(i);
	 }

	 @Override
	 public boolean onTap(GeoPoint p, MapView mapView) {
	      // TODO Auto-generated method stub
	      return super.onTap(p, mapView);
	 }

	 @Override
	 protected boolean onTap(int index) {
	      // TODO Auto-generated method stub
	      Toast.makeText(this.context, mOverlays.get(index).getTitle().toString()+", Latitude: "+mOverlays.get(index).getPoint().getLatitudeE6(), Toast.LENGTH_SHORT).show();
	      return super.onTap(index);         
	 }

	 @Override
	 public int size() {
	      return mOverlays.size();
	 }

	 public void addOverlay(OverlayItem item) {
	      mOverlays.add(item);
	      setLastFocusedIndex(-1);
	      populate();
	 }

	 public void clear() {
	      mOverlays.clear();
	      setLastFocusedIndex(-1);
	      populate();
	 }
}

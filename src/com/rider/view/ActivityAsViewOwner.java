package com.rider.view;

import android.app.Activity;
import android.view.View;

/**
 * An adapter that takes an Activity object and present it as a ViewOwner exposing only methods that relates
 * to managing the view displayed by the activity and hiding the rest.
 *
 * @author Oren Levitzky
 */
public class ActivityAsViewOwner implements ViewOwner {

    // The underlying activity
    private Activity activity;

    /**
     * Constructs a ActivityAsViewOwner adapter for a given activity
     */
    public ActivityAsViewOwner(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sets the current view displayed to the user
     * @param layoutResourceId An id of a resource file that describes the layout of the screen
     */
    public void setContentView(int layoutResourceId) {
        activity.setContentView(layoutResourceId);
    }

    /**
     * Finds a view in the current displayed screen by a its id (as specified in the layout file)
     */
    public View findViewById(int id) {
        return activity.findViewById(id);
    }
    
    /**
     * returns the current activity of the application
     */
    public Activity getActivity() {
		return activity;
	}

    /**
     * Sets the current view displayed to the user
     * @param view - the view to set
     */
	public void setContentView(View view) {
		activity.setContentView(view);
	}
}

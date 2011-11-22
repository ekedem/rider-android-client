package com.rider.view;

import android.app.Activity;
import android.view.View;


/**
 * This interface connects the game ui with the activity as view owner.
 * It is an interface for exposing the functionality of android activities that relates to managing the views they display.
 * It is meant to allow to break the coupling done in android between activities and view by passing interfaces of
 * this kind to the part of the application that deals with the UI but is not concerned with other functionality
 * of activities.
 * 
 * @author Oren Levitzky & Yehonatan Segal
 */
public interface ViewOwner {

    /**
     * Sets the current view displayed to the user
     * @param layoutResourceId An id of a resource file that describes the layout of the screen
     */
    public void setContentView(int layoutResourceId);

    /**
     * Finds a view in the current displayed screen by a its id (as specified in the layout file)
     */
    public View findViewById(int id);
    
    /**
     * Returns the activity of the game
     */
    public Activity getActivity();
    
    /**
     * Sets the current view displayed to the user
     * @param view - the view to set
     */
    public void setContentView(View view);
}


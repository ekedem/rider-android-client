package com.rider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {

	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread;   
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Splash screen view
		setContentView(R.layout.startup_screen);

		final SplashScreen sPlashScreen = this;   

		// The thread to wait for splash screen events
		mSplashThread =  new Thread(){
			@Override
			public void run(){
				try {
					synchronized(this){
						// Wait given period of time or exit on touch
						wait(5000);
					}
				}
				catch(InterruptedException ex){                    
				}

				finish();

				// Run next activity
				Intent intent = new Intent();
				intent.setClass(sPlashScreen, MainActivity.class);
				startActivity(intent);
			}
		};

		mSplashThread.start();        
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
//        if(evt.getAction() == MotionEvent.ACTION_DOWN)
//        {
//            synchronized(mSplashThread){
//                mSplashThread.notifyAll();
//            }
//        }
        return true;
    }    

}

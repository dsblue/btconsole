package com.example.btconsole;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.btconsole.ControlFragment.OnControlFragmentInteractionListener;

public class ControlActivity extends Activity 
implements OnControlFragmentInteractionListener, Handler.Callback{

	/*
	 * Set a list of constants that are used to identify messages
	 */
	private static final int BT_DATA_READ = 1;
	private static final int TCPIP_DATA_READ = 2;

	/* 
	 * Create a Handler in this thread so that other threads can send messages back
	 * The messages are handled by the method: handleMessage(Message msg) ;
	 */
	final public Handler controlActivityHandler = new Handler(this);

	private ControlFragment controlFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);

		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			controlFragment = ControlFragment.newInstance(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(R.id.controlfragment, controlFragment).commit();
		} else {
			controlFragment = (ControlFragment) getFragmentManager().findFragmentById(R.id.controlfragment);
		}
	}

	@Override
	protected void onSaveInstanceState (Bundle outState){
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onControlFragmentInteraction(Uri uri) {
	}

	public void onClickButton(View v){
		controlFragment.onClickButton(v);
	}

	@Override
	public boolean handleMessage(Message msg) {

		if (msg.what == BT_DATA_READ) {
			controlFragment.handleIncommingSerialData((byte[])msg.obj, msg.arg1);
		} else if (msg.what == TCPIP_DATA_READ) {
			controlFragment.handleIncommingTCPIPData((byte[])msg.obj, msg.arg1);
		}

		return true;
	}

}

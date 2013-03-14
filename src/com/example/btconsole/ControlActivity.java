package com.example.btconsole;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.btconsole.ControlFragment.OnControlFragmentInteractionListener;

public class ControlActivity extends Activity 
	implements OnControlFragmentInteractionListener {
	
	private static final int REQUEST_ENABLE_BT = 0;
	private boolean isConnected = false;
	
	private SerialConnection	connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras.getString("type").equals("BT")) {
			// 
			Log.d("Control", "Connecting to a Bluetooth device: " + extras.getString("name") + "\n");
			
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
			    // Device does not support Bluetooth
				//return;
			}
			
			if (!mBluetoothAdapter.isEnabled()) {
			    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    //return;
			}

			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(extras.getString("address"));
			
			// Start a thread for handling the bluetooth connection
			connection = new BluetoothConnection(mBluetoothAdapter, device);
			
			connection.connect();
			
			isConnected = true;			
		} 
	}

	@Override
	public void onControlFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}
	
	public void clickConnect(View v) {
		Log.d("Control", "Click button\n");
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control, menu);
		return true;
	}
	*/

}

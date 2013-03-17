package com.example.btconsole;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.btconsole.ControlFragment.OnControlFragmentInteractionListener;

public class ControlActivity extends Activity 
implements OnControlFragmentInteractionListener, Handler.Callback{

	/*
	 * Set a list of constants that are used to identify messages
	 */
	private static final int REQUEST_ENABLE_BT = 0;
	private static final int BT_DATA_READ = 1;
	
	/* 
	 * Create a Handler in this thread so that other threads can send messages back
	 * The messages are handled by the method: handleMessage(Message msg) ;
	 */
	final Handler controlActivityHandler = new Handler(this);
	
	private SerialConnection connection;

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
			connection = new BluetoothConnection(controlActivityHandler, mBluetoothAdapter, device);

			connection.connect();
			
		} 
	}

	public String getIpAddr() {
		// First see if WIFI is enabled, if so then use it
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(
				"%d.%d.%d.%d",
				(ip & 0xff),
				(ip >> 8 & 0xff),
				(ip >> 16 & 0xff),
				(ip >> 24 & 0xff));

		return ipString;
	}

	private boolean startIPServer(){

		TCPIPServer.getInstance(5005);

		return true;		
	}

	@Override
	public void onControlFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub

	}

	public void clickStart(View v) {
		ToggleButton button = (ToggleButton) findViewById(R.id.start_button);
		if (button.isChecked()) {
			connection.sendStartString();
			/*
			*/
		} else {
			connection.sendStopString();
		}				
	}

	public void clickStartIPServer(View v) {
		ToggleButton button = (ToggleButton) findViewById(R.id.ipserver_button);
		TextView ipaddress = (TextView) findViewById(R.id.ipaddress);
		if (button.isChecked()) {
			ipaddress.setText(getIpAddr());
			if (startIPServer()) {
				// Server was started

			} else {
				// Failed to start a connection
				ipaddress.setText("Not Serving Data");

				Context context = getApplicationContext();
				CharSequence text = "Failed to start server!";
				int duration = Toast.LENGTH_SHORT;

				Toast.makeText(context, text, duration).show(); 
				button.setChecked(false);
			}
		} else {
			ipaddress.setText("Not Serving Data");
		}				
	}

	@Override
	public boolean handleMessage(Message msg) {
		byte[] buffer;
		int len;
		
		if (msg.what == BT_DATA_READ) {
			buffer = (byte[])msg.obj;
			len = msg.arg1;
			
			buffer[len] = 0;
			try {
				Log.d("BT",new String(buffer, "UTF-8").substring(0, len));
				
				/*
				 * Update the log window on the control view
				 */
				TextView log = (TextView) findViewById(R.id.logView);
				// TODO: Is there a more efficient way to do this?
				log.append(new String(buffer, "UTF-8").substring(0, len));
				
				/*
				 * If there is an open TCP IP connection to a remote client, then pass the raw data along
				 */
				TCPIPServer.getInstance(5005).send(buffer);

				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
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

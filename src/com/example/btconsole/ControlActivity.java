package com.example.btconsole;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
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
	private static final int TCPIP_PORT = 5005;
	private static final int BT_DATA_READ = 1;
	private static final int TCPIP_DATA_READ = 2;

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

			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(extras.getString("address"));

			// Start a thread for handling the bluetooth connection
			connection = new BluetoothConnection(controlActivityHandler, mBluetoothAdapter, device);

			connection.connect();

		} 
	}

	public String getIpAddr() {
		// First see if WIFI is enabled, if so then use it
		String ipString = new String();
		/*
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(
				"%d.%d.%d.%d",
				(ip & 0xff),
				(ip >> 8 & 0xff),
				(ip >> 16 & 0xff),
				(ip >> 24 & 0xff));
		 */
		// Then see what the cell IP address is
		try {
			for (Enumeration<NetworkInterface> en =	NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						Log.d("TCPIP", inetAddress.getHostAddress().toString());
						ipString += inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}

		ipString += " Port: " + String.valueOf(TCPIP_PORT);
		return ipString;
	}

	private boolean startIPServer(){

		TCPIPServer.getInstance(controlActivityHandler, TCPIP_PORT);
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
		} else {
			connection.sendStopString();
		}				
	}

	public void clickInfo(View v) {
		connection.sendInfoString();
	}

	public void clickStatus(View v) {
		connection.sendStatusString();
	}

	public void clickDebug(View v) {
		connection.sendDebugString();
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
			TCPIPServer.getInstance(controlActivityHandler, TCPIP_PORT).close();
			ipaddress.setText("Not Serving Data");
		}				
	}

	@Override
	public boolean handleMessage(Message msg) {
		byte[] buffer;
		int len;

		if (msg.what == BT_DATA_READ) {
			buffer 	= (byte[])msg.obj;
			len 	= msg.arg1;

			try {
				//Log.d("BT",new String(buffer, "UTF-8").substring(0, len));

				/*
				 * Update the log window on the control view
				 */
				TextView log = (TextView) findViewById(R.id.logView);
				// TODO: Is there a more efficient way to do this?
				log.append(new String(buffer, "UTF-8").substring(0, len));

				/*
				 * If there is an open TCP IP connection to a remote client, then pass the raw data along
				 */
				ToggleButton button = (ToggleButton) findViewById(R.id.ipserver_button);
				if (button.isChecked()) {
					TCPIPServer.getInstance(controlActivityHandler, TCPIP_PORT).send(buffer,len);
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (msg.what == TCPIP_DATA_READ) {

			/*
			 * Pass the TCP IP data along to the Serial connection here...
			 */
			connection.write((byte[])msg.obj, msg.arg1);

		}

		return true;
	}

}

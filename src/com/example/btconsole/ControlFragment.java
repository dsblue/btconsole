package com.example.btconsole;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ControlFragment extends Fragment {

	/*
	 * Set a list of constants that are used to identify messages
	 */
	private static final int TCPIP_PORT = 5005;

	private SerialConnection connection;

	private OnControlFragmentInteractionListener mListener;

	public ControlFragment() {
	}

	/**
	 * Create a new instance of DetailsFragment, initialized to
	 * show the text at 'index'.
	 */
	public static ControlFragment newInstance(Bundle extras) {
		ControlFragment f = new ControlFragment();
		f.setArguments(extras);
		return f;
	}

	public String getIpAddr() {
		// First see if WIFI is enabled, if so then use it
		String ipString = new String();

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

	public void onClickButton(View v){

		switch(v.getId()){
		case R.id.start_button:
			ToggleButton start_button = (ToggleButton) v;
			if (start_button.isChecked()) {
				connection.sendStartString();
			} else {
				connection.sendStopString();
			}	    		
			break;
		case R.id.status_button:
			connection.sendStatusString();
			break;
		case R.id.info_button:
			connection.sendInfoString();
			break;
		case R.id.debug_button:
			connection.sendDebugString();
			break;
		case R.id.ipserver_button:
			ToggleButton button = (ToggleButton) v;
			TextView ipaddress = (TextView) getView().findViewById(R.id.ipaddress);
			if (button.isChecked()) {
				ipaddress.setText(getIpAddr());

				TCPIPServer.getInstance(((ControlActivity)getActivity()).controlActivityHandler, TCPIP_PORT);
			} else {
				TCPIPServer.getInstance(((ControlActivity)getActivity()).controlActivityHandler, TCPIP_PORT).close();
				ipaddress.setText("Not Serving Data");
			}				
		}
	}
	public void handleIncommingTCPIPData(byte[] buffer, int len) {
		/*
		 * Pass the TCP IP data along to the Serial connection here...
		 */
		connection.write(buffer, len);

	}
	
	public void handleIncommingSerialData(byte[] buffer, int len){
		
		try {
			//Log.d("BT",new String(buffer, "UTF-8").substring(0, len));

			/*
			 * Update the log window on the control view
			 */
			TextView log = (TextView) getView().findViewById(R.id.logView);
			
			/*
			 * If there is an open TCP IP connection to a remote client, then pass the raw data along
			 */
			ToggleButton button = (ToggleButton) getView().findViewById(R.id.ipserver_button);
			if (button.isChecked()) {
				TCPIPServer.getInstance(((ControlActivity)getActivity()).controlActivityHandler, TCPIP_PORT).send(buffer,len);
			}

			/* Workaround to convert CR characters to NL characters so that the TextView will display the 
			 * new lines.
			 * 
			 * This should be done AFTER the data has been passed to the TCP/IP connection
			 */
			for (int i=0;i<len;i++) {
				if (buffer[i] == 13)
					buffer[i] = 10;
			}
			
			String str = new String(buffer,0,len,"UTF-8");

			log.append(str);
			// Log.d("BT",str);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		/*
		 * Disconnect the Bluetooth Device
		 */
		if (connection != null)
			connection.disconnect();
		
		/*
		 * Close any TCP/IP Connections if they exist
		 */
		TCPIPServer.getInstance(((ControlActivity)getActivity()).controlActivityHandler, TCPIP_PORT).close();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Bundle extras = getArguments();

		if (extras.getString("type").equals("BT")) {
			// 
			Log.d("Control", "Connecting to a Bluetooth device: " + extras.getString("name") + "\n");

			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
				// Device does not support Bluetooth
			}

			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(extras.getString("address"));

			// Start a thread for handling the bluetooth connection
			connection = new BluetoothConnection(((ControlActivity)getActivity()).controlActivityHandler, mBluetoothAdapter, device);
			
			connection.connect();
		} 

		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_control, container, false);
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onControlFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnControlFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnControlFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onControlFragmentInteraction(Uri uri);
	}

}

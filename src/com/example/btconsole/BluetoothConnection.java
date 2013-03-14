package com.example.btconsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BluetoothConnection extends SerialConnection {

	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private static final int REQUEST_ENABLE_BT = 0;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice;
	
	private ConnectThread connectThread;	
	
	BluetoothConnection(BluetoothAdapter adapter, BluetoothDevice device){
		super(device.getName(), device.getAddress());
		mBluetoothAdapter = adapter;
		mDevice = device;
	}
	
	BluetoothConnection(String name, String address){
		super(name, address);
	}

	@Override
	public boolean connect(){
		Log.d("BT", "Starting to connect.");
		
		if (mDevice == null) 
			return false;
		
		connectThread = new ConnectThread(mDevice);
		
		connectThread.run();
		
		return true;
	}


	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	        	Log.d("BT", "RFCOMM: " + "\n");
	            tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        Log.d("BT", "Running thread" + "\n");
	        
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	        	Log.d("BT", "Connecting" + "\n");
	            mmSocket.connect();
	            Log.d("BT", "Connected " + "\n");
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	            	Log.d("BT", "Could not connect... " + "\n");
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        //manageConnectedSocket(mmSocket);
	        
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = mmSocket.getInputStream();
	            tmpOut = mmSocket.getOutputStream();
	        } catch (IOException e) { }
	       
	        try {
				tmpOut.write("Hello World 2\n\r".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
}

package com.example.btconsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothConnection extends SerialConnection implements Handler.Callback{
	
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private static final int REQUEST_ENABLE_BT = 0;
	private static final int MESSAGE_READ = 1;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice;
	
	private ConnectThread connectThread;	
	
	final Handler mHandler = new Handler(this);
	
	private ConnectedThread connected;
	
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
		
		connectThread.start();
		
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
	        
	        connected = new ConnectedThread(mmSocket);
	        connected.start();
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	@Override
	public boolean handleMessage(Message msg) {
		byte[] buffer;
		int len;
		
		if (msg.what == MESSAGE_READ) {
			buffer = (byte[])msg.obj;
			len = msg.arg1;
			
			buffer[len] = 0;
			try {
				Log.d("BT",new String(buffer, "UTF-8").substring(0, len));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}

	@Override
	public void sendStartString() {
		connected.write(new String("<?xml ><event type=\"\" />\n\r").getBytes());
	}

	@Override
	public void sendStopString() {
		// TODO Auto-generated method stub
		connected.write(new String("Stop\n\r").getBytes());
	}
	
	
}

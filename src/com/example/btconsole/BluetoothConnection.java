package com.example.btconsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothConnection extends SerialConnection {

	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private static final int BT_DATA_READ = 1;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice;

	private ConnectThread connectThread;	

	private ConnectedThread connected;

	BluetoothConnection(Handler parentThread, BluetoothAdapter adapter, BluetoothDevice device){
		super(parentThread, device.getName(), device.getAddress());
		mBluetoothAdapter = adapter;
		mDevice = device;
	}

	BluetoothConnection(String name, String address){
		super(null, name, address);
		type = "BT";
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
		if (connected != null)
			connected.cancel();
		
		if (connectThread != null)
			connectThread.cancel();

	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;

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
					if (bytes > 0) 
						parentThread.obtainMessage(BT_DATA_READ, bytes, -1, buffer.clone()).sendToTarget();
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes, int len) {
			try {
				mmOutStream.write(bytes,0,len);
			} catch (IOException e) { }
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
	}

	private void writeString(String str){
		if (connected == null)
			return;

		try {
			connected.write(str.getBytes("UTF-8"),str.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
		}
	}

	@Override
	public void write(byte [] bytes, int len) {
		if (connected == null)
			return;

		connected.write(bytes, len);
	}

	@Override
	public void sendStartString() {
		writeString(COT_start);
	}

	@Override
	public void sendStopString() {
		writeString(COT_stop);
	}

	@Override
	public void sendInfoString() {
		writeString(COT_info);
	}

	@Override
	public void sendStatusString() {
		writeString(COT_status);
	}

	@Override
	public void sendDebugString() {
		writeString(COT_debug);
	}


}

package com.example.btconsole;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class TCPIPServerThread extends Thread {
	
	int port;
	boolean active = false;
	private ServerSocket serverSocket;

	public TCPIPServerThread(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);

			while(true) {
				Log.i("************", "Waiting...");

				Socket sock = serverSocket.accept(); // blocks until connection opened
				Log.i("************", "Accepted connection : " + sock);

				byte [] mybytearray = new String("Test 1111111111\n\r").getBytes("UTF-8");
				OutputStream os = sock.getOutputStream();
				Log.i("************", "Sending...");
				os.write(mybytearray,0,mybytearray.length);
				os.flush();
				sock.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//					
		}
	}
	
	public void disconnect(){
		
	}
}

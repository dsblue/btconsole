package com.example.btconsole;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class TCPIPServer {

	// Create a SINGLETON class for TCPIP server
	private static final TCPIPServer instance = new TCPIPServer();
	
	private static TCPIPServerThread thread;
	
	private static int instanceCount = 0;	

	public static TCPIPServer getInstance(int port){

		if (instanceCount == 0) {
			instanceCount++;
			thread = new TCPIPServerThread(port);

			thread.start();
		}

		return instance;
	}

	public void send(byte [] mybytearray){
		OutputStream os;
		try {
			os = thread.getOutputStream();
			Log.i("************", "Sending...");
			os.write(mybytearray,0,mybytearray.length);
			os.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

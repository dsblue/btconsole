package com.example.btconsole;

import java.io.IOException;
import java.io.OutputStream;

import android.os.Handler;
import android.util.Log;

public class TCPIPServer {

	// Create a SINGLETON class for TCPIP server
	private static final TCPIPServer instance = new TCPIPServer();
	
	private static TCPIPServerThread thread;
	
	private static int instanceCount = 0;	

	public static TCPIPServer getInstance(Handler parent, int port){

		if (instanceCount == 0) {
			
			instanceCount++;
			
			thread = new TCPIPServerThread(parent, port);
			thread.start();
		}

		return instance;
	}

	public void send(byte [] mybytearray, int length){
		OutputStream output;
		try {
			output = thread.getOutputStream();
			if (output != null) {
				output.write(mybytearray,0,length);
				output.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		thread.close();
		instanceCount = 0;
	}
}

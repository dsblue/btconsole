package com.example.btconsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class TCPIPServerThread extends Thread {

	int port;
	boolean active = true;
	private ServerSocket serverSocket;

	OutputStream os;
	InputStream is;
	Socket sock;

	public TCPIPServerThread(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);

			Log.i("************", "Waiting...");

			sock = serverSocket.accept(); // blocks until connection opened
			Log.i("************", "Accepted connection : " + sock);

			while (active) {}

			sock.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//					
		}
	}

	public OutputStream getOutputStream() throws IOException {
		return sock.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		return sock.getInputStream();
	}

	public synchronized void disconnect(){
		active = false;
	}
}

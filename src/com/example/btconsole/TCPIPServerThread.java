package com.example.btconsole;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.os.Handler;
import android.util.Log;

public class TCPIPServerThread extends Thread {
	
	private static final int TCPIP_DATA_READ = 2;
	
	int port;
	private ServerSocket serverSocket;
	private Handler parent;
	private OutputStream os;
	private InputStream is;
	private Socket sock;

	public TCPIPServerThread(Handler parent, int port) {
		this.parent = parent;
		this.port = port;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];  	// buffer store for the stream
		int bytes; 							// number of bytes returned from read()

		try {
			serverSocket = new ServerSocket(port);

			// Keep listening to the InputStream until an exception occurs
			Log.i("TCPIP", "Waiting...");

			sock = serverSocket.accept(); // blocks until connection opened
			Log.i("TCPIP", "Accepted connection : " + sock);
			
			while (true) {
				// Read from the InputStream
				bytes = sock.getInputStream().read(buffer);
				// Send the obtained bytes to the UI activity
				parent.obtainMessage(TCPIP_DATA_READ, bytes, -1, buffer).sendToTarget();
			}

		} catch (SocketException e) {
			// The connection was closed, this is OK
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public OutputStream getOutputStream() throws IOException {
		if (sock == null) return null;
		return sock.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		if (sock == null) return null;
		return sock.getInputStream();
	}

	public synchronized void close(){
		try {
            if (sock != null)
            	sock.close();
			if (serverSocket != null) 
				serverSocket.close();
        } catch (IOException e) { }
		serverSocket = null;
		sock = null;
	}
}

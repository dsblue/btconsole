package com.example.btconsole;


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
	
	//public InputStream getInputStream() {
		
	//}
	
}

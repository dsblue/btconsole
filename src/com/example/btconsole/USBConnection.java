package com.example.btconsole;

public class USBConnection extends SerialConnection {

	USBConnection(String name, String address) {
		super(null, name, address);
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendStartString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendStopString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(byte[] bytes, int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendInfoString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendStatusString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendDebugString() {
		// TODO Auto-generated method stub
		
	}
	
}

package com.example.btconsole;

public class ProwlConnection {

	public String deviceName;
	
	ProwlConnection () {
		deviceName = "Unconnected";
	}	
	
	
	@Override
	public String toString() {
		return deviceName;
	}
}

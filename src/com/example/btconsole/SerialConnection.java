package com.example.btconsole;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class SerialConnection {

	protected String name;
	protected String address;
	
    protected InputStream input = null;
    protected OutputStream output = null;
   
    SerialConnection() {
    }
    
    SerialConnection(String name, String address) {
    	this.name = name;
    	this.address = address;
    }
    
    public InputStream getInputStream() {
    	return input;
    }
    
    public OutputStream getOutputStream() {
    	return output;
    }
    
    public abstract boolean connect();
    public abstract void disconnect();
    
    public String getName() {
    	return name;
    }
    
    public String getAddress() {
    	return address;
    }
    
	@Override
	public String toString() {
		if (name != null) 
			return name;
		else 
			return "Unnammed";
	}
}

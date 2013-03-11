package com.example.btconsole;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class SerialConnection {

	protected String name;
	
    protected InputStream input = null;
    protected OutputStream output = null;
   
    SerialConnection() {
    }
    
    SerialConnection(String name) {
    	this.name = name;
    }
    
    InputStream getInputStream() {
    	return input;
    }
    
    OutputStream getOutputStream() {
    	return output;
    }
    
    public abstract boolean connect();
    public abstract void disconnect();
    
	@Override
	public String toString() {
		if (name != null) 
			return name;
		else 
			return "Unnammed";
	}
}

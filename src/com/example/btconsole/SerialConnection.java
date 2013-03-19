package com.example.btconsole;

import java.io.InputStream;
import java.io.OutputStream;

import android.os.Handler;

public abstract class SerialConnection {

	protected final String COT_start = new String("<?xml version='1.0' standalone='yes' ?><event type='t-x-a-e'></event>\n");
	protected final String COT_stop = new String("<?xml version='1.0' standalone='yes' ?><event type='t-x-a-d'></event>\n");
	protected final String COT_status = new String("<?xml version='1.0' standalone='yes' ?><event type='t-x-q'></event>\n");
	protected final String COT_info = new String("<?xml version='1.0' standalone='yes' ?><event type='t-x-q-i'></event>\n");
	protected final String COT_debug = new String("<?xml version='1.0' standalone='yes' ?><event type='NOT COT'></event>\n");
	
	protected final String COT_config_head = new String("<?xml version='1.0' standalone='yes' ?><event type='   '>\n");
	protected final String COT_config_tail = new String("</event>\n");
	
	protected String type;
	protected String name;
	protected String address;
	
	/*
	 * Maintain a link to the parent thread's handler so that messages can
	 * be sent back if necessary (including from new thread created by this class)
	 */
	Handler parentThread;
    
	protected InputStream input = null;
    protected OutputStream output = null;
   
    SerialConnection() {
    }
    
    SerialConnection(Handler thread, String name, String address) {
    	this.parentThread = thread;
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
    
    public String getType() {
    	return type;
    }
    
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
	public abstract void write(byte [] bytes, int len);
	public abstract void sendStartString();
	public abstract void sendStopString();
	public abstract void sendInfoString();
	public abstract void sendStatusString();
	public abstract void sendDebugString();
}

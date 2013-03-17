package com.example.btconsole;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class AddDeviceDialogFragment extends DialogFragment {

	private static final int REQUEST_ENABLE_BT = 0;

	private AddDeviceDialogListener mListener;

	private ArrayAdapter<SerialConnection> devicesAdapter;
	
	private BluetoothAdapter mBluetoothAdapter;
	
	public AddDeviceDialogFragment() {
		// Required empty public constructor
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		devicesAdapter = new ArrayAdapter<SerialConnection>(getActivity(),
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, 
				new ArrayList<SerialConnection>()) ;
	
		// Build the list of Bluetooth Devices and allow the user to select one
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	devicesAdapter.add(new BluetoothConnection(device.getName(), device.getAddress()));
		    }
		} 			
		
		// Build a list of USB OTG serial ports and add them to the list
		if (true) {
			devicesAdapter.add(new USBConnection("USB0","/dev/ttyUSB0"));
		}

		if (devicesAdapter.isEmpty()) {
			// No devices...
			builder.setMessage("No Devices Found!")
				.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
			return builder.create();
		}
		
		builder.setTitle(R.string.device_select_string)
				.setAdapter(devicesAdapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onAddDevicePositiveClick(AddDeviceDialogFragment.this, devicesAdapter.getItem(id));
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						mListener.onAddDeviceNegativeClick(AddDeviceDialogFragment.this);
					}
				});
		
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (AddDeviceDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface AddDeviceDialogListener {
		public void onAddDevicePositiveClick(DialogFragment dialog, SerialConnection connection );
		public void onAddDeviceNegativeClick(DialogFragment dialog);
	}

}

package com.example.btconsole;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity 
	implements DeviceListFragment.OnDeviceListInteractionListener, AddDeviceDialogFragment.AddDeviceDialogListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
    		
    		// Create and display a FragmentList 
    		FragmentManager fragmentManager = getFragmentManager();
    		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    		DeviceListFragment fragment = new DeviceListFragment();
    		
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());
            
    		fragmentTransaction.add(R.id.fragment_container, fragment);
    		fragmentTransaction.commit();		

        }		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSelectDevice(String type, String name, String address) {
		//DeviceListFragment fragment = (DeviceListFragment)
        //        getFragmentManager().findFragmentById(R.id.fragment_container);
		
		Intent intent = new Intent(this, ControlActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
	    startActivity(intent);

	}
	
	public void addConnection(View v){		
	     DeviceListFragment fragment = (DeviceListFragment)
	                getFragmentManager().findFragmentById(R.id.fragment_container);
	     
	     showDeviceListDialog();
	}

    public void showDeviceListDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddDeviceDialogFragment();
        dialog.show(getFragmentManager(), "deviceListDialog");
    }

    @Override
    public void onAddDevicePositiveClick(DialogFragment dialog, SerialConnection connection) {

    	// connection.connect();
    	DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
    	fragment.addConnection(connection.getName(), connection.getAddress());
    	
    }	

    @Override
    public void onAddDeviceNegativeClick(DialogFragment dialog) {
        // User touched the dialog's cancel button

    }

}

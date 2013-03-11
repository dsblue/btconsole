package com.example.btconsole;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity 
	implements ProwlDeviceFragment.OnFragmentInteractionListener, DeviceListFragment.NoticeDialogListener {

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

    		//ProwlDeviceFragment.newInstance("T1", "T2");
    		ProwlDeviceFragment fragment = new ProwlDeviceFragment();
    		
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());
            
    		fragmentTransaction.add(R.id.fragment_container, fragment);
    		fragmentTransaction.commit();		

        }		

	}
	
	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.main, menu);
	//	return true;
	//}

	@Override
	public void onFragmentInteraction(String id) {
		ProwlDeviceFragment fragment = (ProwlDeviceFragment)
                getFragmentManager().findFragmentById(R.id.fragment_container);

	}
	
	public void addConnection(View v){		
	     ProwlDeviceFragment fragment = (ProwlDeviceFragment)
	                getFragmentManager().findFragmentById(R.id.fragment_container);

	     //Intent intent = new Intent(this, ConfigureActivity.class);
	     //startActivity(intent);
	     
	     showDeviceListDialog();
	}

    public void showDeviceListDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new DeviceListFragment();
        dialog.show(getFragmentManager(), "deviceListDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, SerialConnection connection) {
        // User touched the dialog's positive button
    	connection.connect();    	
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

}

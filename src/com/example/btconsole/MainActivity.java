package com.example.btconsole;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity 
	implements ProwlDeviceFragment.OnFragmentInteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create and display a FragmentList 
		//FragmentManager fragmentManager = getFragmentManager();
		//FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		//ProwlDeviceFragment fragment = new ProwlDeviceFragment();
		//fragmentTransaction.add(R.id.main_layout, fragment);
		//fragmentTransaction.commit();
		
		ProwlDeviceFragment.newInstance("T1", "T2");
		
		
		// Example of a Toast notification
		Context context = getApplicationContext();
		CharSequence text = "Starting!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onFragmentInteraction(String id) {
		
	}
	
	public void addConnection(View v){
		Session.CONNECTIONS.add(new ProwlConnection());
	}

}

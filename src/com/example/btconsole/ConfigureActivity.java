package com.example.btconsole;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

public class ConfigureActivity extends Activity 
	implements ConfigureFragment.OnFragmentInteractionListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure);
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

	
}

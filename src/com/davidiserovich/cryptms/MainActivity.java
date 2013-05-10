package com.davidiserovich.cryptms;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuInflater;
import android.app.ActionBar;

public class MainActivity extends Activity implements ActionBar.TabListener{
	
	String SEND_MESSAGE_FRAGMENT_TAG = "sendMessageFragment";
	String READ_MESSAGE_FRAGMENT_TAG = "readMessageFragment";
	
	SendMessageFragment sendMessageFragment;
	ReadMessageFragment readMessageFragment;
	EncryptionManager encryptionManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
		encryptionManager = new EncryptionManager(getPreferences(Activity.MODE_PRIVATE), "testpassword");
	    
	    FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		sendMessageFragment = new SendMessageFragment();
		sendMessageFragment.setEncryptionManager(encryptionManager);
		
		readMessageFragment = new ReadMessageFragment();
		readMessageFragment.setEncryptionManager(encryptionManager);
		
		fragmentTransaction.add(R.id.fragment_container, sendMessageFragment);
		fragmentTransaction.add(R.id.fragment_container, readMessageFragment);
		fragmentTransaction.commit();
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		Tab tab = actionBar.newTab()
				.setText("SEND")
	            .setTabListener(this)
	            .setTag(SEND_MESSAGE_FRAGMENT_TAG);
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
				.setText("READ")
	            .setTabListener(this)
	            .setTag(READ_MESSAGE_FRAGMENT_TAG);
	    actionBar.addTab(tab);
	    
	    actionBar.show();

	}
	
	/* The following are each of the ActionBar.TabListener callbacks */

	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
		String tag = (String)tab.getTag();
		if (tag.equals(SEND_MESSAGE_FRAGMENT_TAG)){
			ft.attach(sendMessageFragment);
		}
		else if (tag.equals(READ_MESSAGE_FRAGMENT_TAG)){
			ft.attach(readMessageFragment);
		}
    }

	@Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        
		String tag = (String)tab.getTag();
		
        if (tag.equals(SEND_MESSAGE_FRAGMENT_TAG)){
            ft.detach(sendMessageFragment);
		}
		else if (tag.equals(READ_MESSAGE_FRAGMENT_TAG)){
			ft.detach(readMessageFragment);
		}
    }

	@Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	  } 


}

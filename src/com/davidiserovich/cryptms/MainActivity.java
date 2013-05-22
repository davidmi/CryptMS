package com.davidiserovich.cryptms;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class MainActivity extends Activity implements ActionBar.TabListener{

	public static String SEND_MESSAGE_FRAGMENT_TAG = "sendMessageFragment";
	public static String READ_MESSAGE_FRAGMENT_TAG = "readMessageFragment";

	private SendMessageFragment sendMessageFragment;
	private ReadMessageFragment readMessageFragment;
	private EncryptionManager encryptionManager;

	public static String SENT = "SMS_SENT";
	public static String DELIVERED = "SMS_DELIVERED";

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

		final Context c = this;


		//---when the SMS has been sent---
		this.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(c, "SMS sent", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(c, "Generic failure", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(c, "No service", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(c, "Null PDU", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(c, "Radio off", 
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		//---when the SMS has been delivered---
		this.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(c, "SMS delivered", 
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(c, "SMS not delivered", 
							Toast.LENGTH_SHORT).show();
					break;                        
				}
			}
		}, new IntentFilter(DELIVERED)); 

	}

	/**
	 * Utility method for checking if an app to fulfill an Intent is available.
	 * Thanks https://developer.android.com/training/camera/photobasics.html
	 *
	 * @param context
	 * @param action
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
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

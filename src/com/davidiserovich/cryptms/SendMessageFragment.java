package com.davidiserovich.cryptms;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageFragment extends Fragment {
	TextView phoneNumberField;
	TextView textField;
	
	String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    
    private Activity currentActivity;
    
    short CRYPTMS_PORT = 6688;
	
	private void sendSMS(String phoneNumber, String message){
		
        PendingIntent sentPI = PendingIntent.getBroadcast(currentActivity, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(currentActivity, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        currentActivity.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(currentActivity.getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(currentActivity.getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(currentActivity.getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(currentActivity.getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(currentActivity.getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        currentActivity.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(currentActivity.getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(currentActivity.getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        sms.sendDataMessage(phoneNumber, null, CRYPTMS_PORT, message.getBytes(), sentPI, deliveredPI);
    }
	
	public void sendButtonClick(View v){
		
		String phoneNumber = phoneNumberField.getText().toString();
        String message = textField.getText().toString();
        
        if (phoneNumber.length()>0 && message.length()>0)                
            sendSMS(phoneNumber, message);                
        else
            Toast.makeText(currentActivity.getBaseContext(), 
                "Please enter both phone number and message.", 
                Toast.LENGTH_SHORT).show();
        
        String msg = "secret msg";
        EncryptionManager enc = new EncryptionManager(currentActivity.getPreferences(Activity.MODE_PRIVATE), "testpassword");
        
        byte[] crypted = enc.encrypt(msg.getBytes(), enc.getPublicModulus(), enc.getPublicExponent());
        String cryptBase64 = Base64.encodeToString(crypted, Base64.DEFAULT);
        Toast.makeText(currentActivity, Integer.toString(cryptBase64.length()), Toast.LENGTH_SHORT).show();
        String dec = new String(enc.decrypt(crypted));
        Toast.makeText(currentActivity, dec, Toast.LENGTH_SHORT).show();

        
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sendsms_fragment,
                                     container, false);

		currentActivity = getActivity();
		
		phoneNumberField = (TextView)view.findViewById(R.id.recipient);
		textField = (TextView)view.findViewById(R.id.message);

        return view;
    }

}

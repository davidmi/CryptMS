package com.davidiserovich.cryptms;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageFragment extends Fragment {
	TextView phoneNumberField;
	TextView textField;
	

    
    private Activity currentActivity;
    
    short CRYPTMS_PORT = 6688;
    
    private EncryptionManager enc;
    
    public void setEncryptionManager(EncryptionManager encryptionManager){
    	enc = encryptionManager;
    }
	
	private void sendSMS(String phoneNumber, byte[] message){
		
        PendingIntent sentPI = PendingIntent.getBroadcast(currentActivity, 0,
            new Intent(MainActivity.SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(currentActivity, 0,
            new Intent(MainActivity.DELIVERED), 0);
 
               
 
        SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        sms.sendDataMessage(phoneNumber, null, CRYPTMS_PORT, message, sentPI, deliveredPI);
    }
	
	public void sendButtonClick(View v){
		
		String phoneNumber = phoneNumberField.getText().toString();
        String message = textField.getText().toString();
        
        byte[] crypted = enc.encrypt(message.getBytes(), enc.getPublicModulus(), enc.getPublicExponent());
        
        if (crypted.length != 256){
        	Toast.makeText(currentActivity, "WRONG CRYPTED MESSAGE LENGTH " + Integer.toString(crypted.length), Toast.LENGTH_SHORT);
        }
        
        byte[] part1 = new byte[129];
        byte[] part2 = new byte[129];
        
        part1[0] = 1;
        part2[0] = 2;
        
        System.arraycopy(crypted, 0, part1, 1, 128);
        System.arraycopy(crypted, 128, part2, 1, 128);
        
        if (phoneNumber.length()>0 && message.length()>0){                
            sendSMS(phoneNumber, part1);
            sendSMS(phoneNumber, part2);
        }
        	
        else
            Toast.makeText(currentActivity.getBaseContext(), 
                "Please enter both phone number and message.", 
                Toast.LENGTH_SHORT).show();

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
		
		Button sendButton = (Button)view.findViewById(R.id.send_button);
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendButtonClick(v);
			}
		});

        return view;
    }

}

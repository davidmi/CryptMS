package com.davidiserovich.cryptms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context c, Intent intent){
		//---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";            
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += "SMS from " + msgs[i].getOriginatingAddress();                     
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";        
            }
            //---display the new SMS message---
            Toast.makeText(c, str, Toast.LENGTH_SHORT).show();
        }
        /** Check the message's header. If it's good, then put this message in the message queue in
         *  sharedpreferences
        SharedPreferences sp = c.getSharedPreferences("MainActivity", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        */

	}
}

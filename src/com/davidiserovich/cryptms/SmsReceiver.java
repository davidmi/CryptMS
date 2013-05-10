package com.davidiserovich.cryptms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class SmsReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context c, Intent intent){
		//---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        
        /** Check the message's header. If it's good, then put this message in the message list in sharedpreferences */
        SharedPreferences sp = c.getSharedPreferences("MainActivity", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                Toast.makeText(c, "Got " + Integer.toString(msgs[i].getUserData().length) + " bytes", Toast.LENGTH_SHORT).show();
                
	            //First, since the data comes in two pieces, check for the first.
	            String firstmsg = sp.getString("part1", "");
	            if (firstmsg.equals("")){
	            	editor.putString("part1", Base64.encodeToString(msgs[i].getUserData(), Base64.DEFAULT));
	            	editor.commit();
	            }
	            else{
	            	byte[] firstBytes = Base64.decode(firstmsg, Base64.DEFAULT);
	            	byte[] fulldata = new byte[firstBytes.length + msgs[i].getUserData().length];
	            	byte[] current = msgs[i].getUserData();
	            	System.arraycopy(firstBytes, 0, fulldata, 0, firstBytes.length);
	            	System.arraycopy(current, 0, fulldata, firstBytes.length, current.length);
	            	
	        		String messagesJSON = sp.getString("messages", "[]");
	        		JSONArray messageArray = null;
	        		try {
	        			messageArray = new JSONArray(messagesJSON);
	        		} catch (JSONException e1) {
	        			// TODO Auto-generated catch block
	        			Toast.makeText(c, "JSON encoding exception?? " + e1.toString(), Toast.LENGTH_LONG).show();
	        		}
	        		
	        		messageArray.put(Base64.encodeToString(fulldata, Base64.DEFAULT));
	        		
	        		editor.putString("messages", messageArray.toString());
	        		editor.putString("part1", "");
	        		
	        		Toast.makeText(c, "Inserted message!", Toast.LENGTH_SHORT).show();
	        		
	        		editor.commit();
	        		
	        		Toast.makeText(c, sp.getString("messages", "[]"), Toast.LENGTH_SHORT).show();
	            }
                
            }
        }

	}
}

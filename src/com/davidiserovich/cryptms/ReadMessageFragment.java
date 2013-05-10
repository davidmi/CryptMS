package com.davidiserovich.cryptms;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReadMessageFragment extends Fragment {
	
	String[] messages;
	Activity currentActivity;
	ListView messageListView;
	EncryptionManager encryptionManager;
	SharedPreferences prefs;
	
	public void setEncryptionManager(EncryptionManager encryptionManager){
		this.encryptionManager = encryptionManager; 
	}
	
	public void refreshMessageList(){
		
		String messagesJSON = prefs.getString("messages", "[]");
		Toast.makeText(currentActivity, messagesJSON, Toast.LENGTH_SHORT).show();
		JSONArray messageArray = null;
		
		//XXX: EVERYTHING BEYOND HERE IS A HACK. FIX LATER 
		
		int length = 0;
		try {
			messageArray = new JSONArray(messagesJSON);
			length = messageArray.length();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		messages = new String[length];
		for (int i = 0; i < length; i++){
			try {
				messages[i] = messageArray.getString(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ArrayAdapter<String> messageListAdapter = new ArrayAdapter<String>(currentActivity, R.id.msglist, messages){
			@Override
		    public View getView(int position, View convertView, ViewGroup parent) {
				
		    	TextView v = (TextView)convertView;
		    	if (v == null) {
		            //LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            //v = vi.inflate(R.layout.file_list_item, null);
		    		v = new TextView(currentActivity);
		    		
		        }
		    	if (v != null) {
		    			try{
		    				v.setText(new String(encryptionManager.decrypt(Base64.decode(messages[position], Base64.DEFAULT))));
		    			}
		    			catch (Exception e){
		    				v.setText("FAILED TO DECRYPT");
		    			}

		        }
		        return v;
		    	
		    }
			
		};
		
		messageListView.setAdapter(messageListAdapter);
		
		Toast.makeText(currentActivity, "Set the adapter", Toast.LENGTH_SHORT).show();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.readsms_fragment,
                                     container, false);
        

		currentActivity = getActivity();
		messageListView = (ListView)view.findViewById(R.id.msglist);
		
		prefs = currentActivity.getPreferences(Activity.MODE_PRIVATE);
		
		refreshMessageList();

        return view;
    }
}

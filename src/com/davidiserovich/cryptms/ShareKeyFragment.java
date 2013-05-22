package com.davidiserovich.cryptms;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ShareKeyFragment extends Fragment {
	Activity currentActivity;
	
	// TODO: Move this stuff into a CameraManager class, maybe begin a
	// little personal utility library with things like this

	/** Activity call code for taking a photo with the camera */
	private final static int CAMERA_IMAGE_CODE = 102;
	
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

	

	/**
	 * Utility method
	 * Sees if a directory exists and is writable, and tries to create it if it isn't
	 *
	 * @param path The path to the desired directory
	 * @return true if the directory exists and is ready to be written to, false otherwise
	 */
	public boolean checkDirectory(String path){
		File f = new File(path);
		if (!f.exists()){
			if (!f.mkdirs()){
				// This directory is no good
				return false;
			}
		}
		else if (!f.isDirectory()){
			// We don't want to get rid of the existing file either
			return false;
		}
		else if (!f.canWrite()){
			return false;
		}
		return true;	
	}

	/**
	 * Click handler of buttonSnapPhoto
	 * @param v the button
	 */
	public void takePhoto(View v){
		Intent intent;
		if (isIntentAvailable(currentActivity, MediaStore.ACTION_IMAGE_CAPTURE))
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		else{
			Toast.makeText(currentActivity, "Camera unavailable", Toast.LENGTH_SHORT).show();
			return;
		}
		if (checkDirectory(Environment.getExternalStorageDirectory()+"/Cryptagram/tmp")){
			String outFilename = "tmp.jpg";
			File tmpOutfile = new File(Environment.getExternalStorageDirectory()+outFilename);


			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpOutfile));

			startActivityForResult(intent, CAMERA_IMAGE_CODE);
		}
		else
			Toast.makeText(currentActivity, "Failed to create file", Toast.LENGTH_SHORT).show();
		return;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.readsms_fragment,
				container, false);
		
		currentActivity = getActivity();

		return view;
	}	
}

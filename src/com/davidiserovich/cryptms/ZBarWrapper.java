package com.davidiserovich.cryptms;

public class ZBarWrapper {
	static {
		System.loadLibrary("zbar");
	}
	
	public native String decode(int width, int height, byte[] data);
}

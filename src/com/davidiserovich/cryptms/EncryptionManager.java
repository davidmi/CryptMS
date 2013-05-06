package com.davidiserovich.cryptms;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Base64;

public class EncryptionManager {
	
	/* Return parameters for aesEncrypt() */	
	private byte[] iv;
	private byte[] encrypted;
	
	/** The password for the AES encrypt and decrypt, so the device doesn't
	 *  store the private key in plaintext on disk. The app should prompt for it onCreate
	 */
	private String password;
	
	private SharedPreferences s;
	
	public EncryptionManager(SharedPreferences s, String password){
		this.s = s;
		this.password = password;
		
		/** Generate a keypair if the SharedPreferences does not yet contain one */
		if (s.getString("private_key", null) == null){
			generateKeypair();
		}
	}
	
	public BigInteger getPublicExponent(){
		return new BigInteger(s.getString("public_key", null));
	}
	
	public BigInteger getPublicModulus(){
		return new BigInteger(s.getString("public_modulus", null));
	}
	
	/**
	 * Encrypt a string with AES-256 with the given password
	 * @param password
	 * @param message
	 */
	private boolean aesEncrypt(String password, String message){
		try{
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKeySpec k = new SecretKeySpec(password.getBytes(), "AES");
			c.init(Cipher.ENCRYPT_MODE, k);
			iv = c.getIV();
			encrypted = c.doFinal(message.getBytes());
		
		}
		catch(Exception e){
			Log.e("CryptMS", "AES encrypt error: " + e.toString());
			return false;
		}
		return true;
	}
	
	/** Decrypt using AES */
	private byte[] aesDecrypt(String password, byte[] iv, byte[] message){
		try{
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKeySpec k = new SecretKeySpec(password.getBytes(), "AES");
			c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
			
			return c.doFinal(message);
		}
		catch (Exception e){
			Log.e("CryptMS", "AES decrypt error: " + e.toString());
			return null;
		}
		
	}
	
	/**
	 * Encrypt the given data using RSA
	 * @param data the byte array to encrypt
	 * @param modulus the public key's modulus
	 * @param exponent the public key's exponent
	 * @return byte[] of the encrypted data or null if the encryption failed
	 */
	public byte[] encrypt(byte[] data, BigInteger modulus, BigInteger publicExponent){
		try{
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyfactory.generatePublic(keySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(data);
			return cipherData;
		}
		catch (Exception e){
			Log.e("CryptMS", "RSA encryption failed");
			return null;
		}
	}
	
	/**
	 * Decrypts the data with the private key stored in the EncryptionManager's SharedPreferences
	 * @param data the data to decrypt
	 * @return the byte[] of the decrypted data, or null if the decryption failed
	 */
	public byte[] decrypt(byte[] data){
		try{
			BigInteger modulus = new BigInteger(s.getString("private_modulus", null));
			BigInteger privateExponent = new BigInteger(s.getString("private_key", null));
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, privateExponent);
			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyfactory.generatePublic(keySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(data);
			return cipherData;
		}
		catch (Exception e){
			Log.e("CryptMS", "RSA decryption failed");
			return null;
		}
	}
	
	/**
	 * Generate an RSA keypair
	 */
	public void generateKeypair(){
		try{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
			  RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
			  RSAPrivateKeySpec.class);
			
			SharedPreferences.Editor e = s.edit();
			e.putString("public_modulus", pub.getModulus().toString());
			e.putString("public_key", pub.getPublicExponent().toString());
			e.putString("private_modulus", priv.getModulus().toString());
			
			// XXX: password-protect with AES after testing that this works
			e.putString("private_key", priv.getPrivateExponent().toString());
			while (!e.commit());
		
		} catch (Exception e) {
			Log.e("CryptMS", "KEYGEN ERROR " + e.toString());
			throw new RuntimeException("RSA KEYGEN ERROR " + e.toString());
		}	
	}

}

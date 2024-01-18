package com.oradnata.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PublicUtility  {
	
	private static final Logger log = LogManager.getLogger(PublicUtility.class);

	private String key_="45&dfrgYutQA3$#ZxcFGLoUi";

	private byte[] sharedvector_ = { 0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11 };

	public int operateBinary(int a, int b, IntegerMath op) {
		return op.operation(a, b);
	}

	private interface IntegerMath {
		int operation(int a, int b);
	}	

	public String EncryptText(String RawText) {
		String EncText = "";
		byte[] keyArray = new byte[24];
		byte[] temporaryKey;
		byte[] toEncryptArray = null;
		try {
			toEncryptArray = RawText.getBytes("UTF-8");
			MessageDigest m = MessageDigest.getInstance("MD5");
			temporaryKey = m.digest(key_.getBytes("UTF-8"));
			if (temporaryKey.length < 24) {
				int index = 0;
				for (int i = temporaryKey.length; i < 24; i++) {
					keyArray[i] = temporaryKey[index];
				}
			}
			Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedvector_));
			byte[] encrypted = c.doFinal(toEncryptArray);
			EncText = Base64.encodeBase64String(encrypted);
		} catch (NoSuchAlgorithmException NoEx) {
			log.error("Error",NoEx);			
		}

		catch (UnsupportedEncodingException ex) {
			log.error("Error",ex);		
		} catch (NoSuchPaddingException ex) {
			log.error("Error",ex);
		} catch (InvalidKeyException ex) {
			log.error("Error",ex);
		} catch (InvalidAlgorithmParameterException ex) {
			log.error("Error",ex);
		} catch (IllegalBlockSizeException ex) {
			log.error("Error",ex);
		} catch (BadPaddingException ex) {
			log.error("Error",ex);
		}
		return EncText;
	}

	public String DecryptText(String EncText) {
		String RawText = "";
		byte[] keyArray = new byte[24];
		byte[] temporaryKey;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			temporaryKey = m.digest(key_.getBytes("UTF-8"));
			if (temporaryKey.length < 24) 
			{
				int index = 0;
				for (int i = temporaryKey.length; i < 24; i++) {
					keyArray[i] = temporaryKey[index];
				}
			}
			Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedvector_));
			byte[] decrypted = c.doFinal(Base64.decodeBase64(EncText));
			RawText = new String(decrypted, "UTF-8");
		} catch (NoSuchAlgorithmException ex) {
			log.error("Error",ex);
		} catch (UnsupportedEncodingException ex) {
			log.error("Error",ex);
		} catch (NoSuchPaddingException ex) {
			log.error("Error",ex);
		} catch (InvalidKeyException ex) {
			log.error("Error",ex);
		} catch (InvalidAlgorithmParameterException ex) {
			log.error("Error",ex);
		} catch (IllegalBlockSizeException ex) {
			log.error("Error",ex);
		} catch (BadPaddingException ex) {
			log.error("Error",ex);
		}
		return RawText;
	}

		public static void main(String args[]) {
			PublicUtility utility  = new PublicUtility();
			String enc = utility.EncryptText("Hello World");			
			System.out.println(enc);
		}
}
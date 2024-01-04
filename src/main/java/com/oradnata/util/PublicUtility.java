package com.oradnata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author pradheep.p
 *
 */
public class PublicUtility implements InitializingBean {

	private long serialVersionID = 934993400923l;

	private HashMap propertiesMap = new HashMap<String, String>();

	private String basePath = "/home/dnata/properties/";

	private String securityPropertiesFile = new String("security.properties");
	
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
			NoEx.printStackTrace();
		}

		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}
		return EncText;
	}

	public String DecryptText(String EncText) {

		String RawText = "";
		byte[] keyArray = new byte[24];
		byte[] temporaryKey;

		byte[] toEncryptArray = null;

		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			temporaryKey = m.digest(key_.getBytes("UTF-8"));

			if (temporaryKey.length < 24) // DESede require 24 byte length key
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
		} catch (NoSuchAlgorithmException NoEx) {
			NoEx.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}
		return RawText;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String fileName = basePath + securityPropertiesFile;
		System.out.println("Loading :" + fileName);
		HashMap map = getProperties(fileName);
		String key = (String) map.get("app-sec-key");
		this.key_ = key;
	}

	private HashMap getProperties(String fileName) {
		Properties properties = new Properties();
		File file = new File(fileName);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Exception occured in getProperties() : FileNotFoundException ");
		}
		try {
			properties.load(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (final String name : properties.stringPropertyNames()) {
			propertiesMap.put(name, properties.getProperty(name));
		}
		return propertiesMap;
	}
	
	public static void main(String args[]){
		PublicUtility publicUtility = new PublicUtility();
		//System.out.println(publicUtility.DecryptText("rEetCL2ZVm4mp7gQEBnWoQ=="));
		System.out.println(publicUtility.EncryptText("KG#9t518fa$br"));
		System.out.println(publicUtility.EncryptText("Dnata#7sftp"));
		String test = "Hello World.123";
		System.out.println(test.replace(" ", "-").replace(".", ""));
	}	
}
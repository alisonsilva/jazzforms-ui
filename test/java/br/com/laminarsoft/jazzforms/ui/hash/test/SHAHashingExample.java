package br.com.laminarsoft.jazzforms.ui.hash.test;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public class SHAHashingExample {
	public static void main(String[] args) throws Exception {
		String password = "123455";

		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		System.out.println("not hex: " + Base64.encodeBase64String(byteData));

		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		System.out.println("Hex format : " + sb.toString());

		// convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		System.out.println("Hex format : " + hexString.toString());
	}
}

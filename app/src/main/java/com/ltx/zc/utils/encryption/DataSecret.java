package com.ltx.zc.utils.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataSecret {

	private static String keyCode = "beiXiang"; // 秘钥可以任意改，只要总长度是8个字节就行

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	public static String encryptDES(String encryptString){
		try {
			IvParameterSpec zeroIv = new IvParameterSpec(iv);

			SecretKeySpec key = new SecretKeySpec(keyCode.getBytes(), "DES");

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);

			byte[] encryptedData = cipher.doFinal(encryptString.getBytes("utf-8"));

			return GateWayBase64.encode(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} 

	}

	public static String decryptDES(String decryptString) {

		try {
			byte[] byteMi = GateWayBase64.decode(decryptString);

			IvParameterSpec zeroIv = new IvParameterSpec(iv);

			SecretKeySpec key = new SecretKeySpec(keyCode.getBytes(), "DES");

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);

			byte decryptedData[] = cipher.doFinal(byteMi);

			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * 
	 * @return String
	 */

	public static String parseByte2HexStr(byte buf[]) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < buf.length; i++) {

			String hex = Integer.toHexString(buf[i] & 0xFF);

			if (hex.length() == 1) {

				hex = '0' + hex;

			}

			sb.append(hex.toUpperCase());

		}

		return sb.toString();

	}

}

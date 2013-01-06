package cn.edaijia.android.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/***
 * MD5º”√‹
 * @author CaoZheng
 *
 */
public class MD5 {
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };

	//String to byte
	public static String toHexString(byte[] b) {  
		StringBuilder sb = new StringBuilder(b.length * 2);  
		for (int i = 0; i < b.length; i++) {  
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
			sb.append(HEX_DIGITS[b[i] & 0x0f]);  
		}
		return sb.toString();
	}

	public static String md5(String str) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(str.getBytes());
			byte messageDigest[] = digest.digest();	//º”√‹

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}
}

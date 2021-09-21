package project_package.main.service;

import java.nio.ByteBuffer;

/**
 * Servis koji konvertira između tipova po potrebi, uglavnom byte[] u hexadecimal i long
 *
 * @author Iva
 */
public class TypeConverterService {

	//Konvertira niz bitova u hexadecimalnu reprezentaciju
	public static String bytesToHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			int byteValue = (int) buf[i] & 0xff;
			if (byteValue <= 15) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toString(byteValue, 16));
		}
		return strBuf.toString();
	}

	// Metoda koja konvertirahexadecimalni String u niz bitova
	public static byte[] hexToBytes(String hexString) {
		int size = hexString.length();
		byte[] buf = new byte[size / 2];
		int j = 0;
		for (int i = 0; i < size; i++) {
			String a = hexString.substring(i, i + 2);
			int valA = Integer.parseInt(a, 16);
			i++;
			buf[j] = (byte) valA;
			j++;
		}
		return buf;
	}
	
	//metoda koja konvertira long u niz byteova
	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}

}

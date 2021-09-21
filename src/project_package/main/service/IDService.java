package project_package.main.service;

import project_package.main.network.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Servis koji generira ID-eve i Key-eve
 *
 * @author Iva
 */
public class IDService {

	//generiraj ID
	public static int generateID() {
		int max = (int) Math.pow(2, Config.ID_BIT_RANGE);
		return generateRandInt(0,max);
	}

	//generiral ključ od File-a (storage key)
	public static String generateFileKey(File file) {
		String md5Str = null;
		try {
			FileInputStream	fis = new FileInputStream(file);

			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((bytesCount = fis.read(byteArray)) != -1) {
				md.update(byteArray, 0, bytesCount);
			}
			fis.close();

			md5Str = TypeConverterService.bytesToHex(md.digest()).substring(0, Config.FILE_KEY_RANGE);
//			fileKey = Integer.parseInt(md5Str.substring(0,Config.FILE_KEY_RANGE), 16);
		} catch (FileNotFoundException e) {
			LoggerService.error(IDService.class, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LoggerService.error(IDService.class, e.getMessage());
		} catch (IOException e) {
			LoggerService.error(IDService.class, e.getMessage());
		}
		return md5Str;
	}

	//generiraj rendom int
	public static int generateRandInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	//pomoćne funkcije

	//get adresu od host-a
	public static InetAddress getHostAddress() throws UnknownHostException {
		InetAddress inetAddr = InetAddress.getLocalHost();
		return inetAddr;
	}

	//get ime od host-a
	public static String getHostName() throws UnknownHostException {
		String inetName = InetAddress.getLocalHost().getHostName();
		return inetName;
	}

	//konvertiraj id u kružni ID u mreži
	public static int convertToCircleID(int id){
		if(id < 0){
			return (int) (id + Math.pow(2, Config.ID_BIT_RANGE) );
		}
		else{
			return (int) (id%Math.pow(2, Config.ID_BIT_RANGE));
		}
		
	}
}












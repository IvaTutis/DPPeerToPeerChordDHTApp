package project_package.main.network.config;

/**
 * klasa sa konfiguracijskim elementima za DHT
 *
 * @author Iva
 */
public class Config {
	public static final int ID_BIT_RANGE = 16;
	public static final int FILE_KEY_RANGE = 4;
	public static final int FINGER_TABLE_SIZE = ID_BIT_RANGE;
	public static String DIS_HOST = "localhost";
	public static int DIS_PORT = 8080;
	public static int interval = 1000;
}

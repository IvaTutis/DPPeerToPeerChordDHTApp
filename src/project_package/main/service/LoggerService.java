package project_package.main.service;

/**
 * Servis koji služi kao logger
 *
 * @author Iva
 */
public class LoggerService {
	public static void info(Class logClass, String message){
		if (message != null) {
			System.out.println("\n[INFO]" + logClass.getName() + ": " + message);
		}
	}

	public static void error(Class logClass, String message){
		if (message != null) {
			System.err.println("\n[ERROR]" + logClass.getName() + ": " + message);
		}
	}
}

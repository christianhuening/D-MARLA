package Exceptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for accessing the stored error messages.
 */
public class ErrorMessages {

    private static Properties errorCodes;

    private static void loadProperties() {
        if(errorCodes == null) {
        try {
                errorCodes = new Properties();
                errorCodes.load(new FileInputStream("./cfg/errorCodes.properties"));
            } catch (IOException ex) {
                System.err.println("Error code file was not found. Please ensure that the errorCodes.properties is present and that the application's start user has sufficient rights to access it.");
            }
        }
    }

    public static String get(String errorCode) {
        loadProperties();
        return errorCodes.getProperty(errorCode);
    }
}

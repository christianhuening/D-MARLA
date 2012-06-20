package Settings;

import Exceptions.ErrorMessages;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class can be used for reading the applications's settings.
 */
public class AppSettings {

    //since we're about to handle a lot of errors, load error description file
    private static Properties appSettings = getAppSettings();

    private static Properties getAppSettings() {
        try {
            Properties tmp = new Properties();
            tmp.load(new FileInputStream("./cfg/appSettings.properties"));
            appSettings = tmp;
        } catch (IOException ex) {
            System.err.print("'Application settings file was not found. Please ensure that './cfg/appSettings.properties' is present and that the application's start user has sufficient rights to access it");
        }

        return appSettings;
    }

    public static String getString(String key) throws SettingException {
        String result = getAppSettings().getProperty(key);
        if (result == null) {
            throw new SettingException(ErrorMessages.get("missingAppSetting"));
        }
        return result;
    }

    public static int getInt(String key) throws SettingException {
        try {
            return Integer.parseInt(getAppSettings().getProperty(key));
        } catch (NumberFormatException ex) {
            throw new SettingException(ErrorMessages.get("noIntForSetting"));
        }
    }
}

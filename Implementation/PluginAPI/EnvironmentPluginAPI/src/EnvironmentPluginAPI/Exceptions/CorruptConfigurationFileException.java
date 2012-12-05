package EnvironmentPluginAPI.Exceptions;

/**
 * Error that is thrown when there was a corrupt map file in the maps directory
 */
public class CorruptConfigurationFileException extends Exception {

    private final String path;

    public CorruptConfigurationFileException(String path, String message) {
        super(message);

        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

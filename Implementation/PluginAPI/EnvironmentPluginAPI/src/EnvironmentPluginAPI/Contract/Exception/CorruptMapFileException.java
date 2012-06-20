package EnvironmentPluginAPI.Contract.Exception;

/**
 * Error that is thrown when there was a corrupt map file in the maps directory
 */
public class CorruptMapFileException extends Exception {

    private final String path;

    public CorruptMapFileException(String path, String message) {
        super(message);

        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

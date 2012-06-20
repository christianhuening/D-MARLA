package PluginLoader.Interface.Exceptions;

/**
 * This class describes the error, that the program was unable to read a provided agent system plugin jar.
 * <p/>
 * This may be caused by either contract violation or drive read errors
 */
public class PluginNotReadableException extends Exception {

    private final String pathToJar;

    public PluginNotReadableException(String message, String pathToJar) {
        super(message);
        this.pathToJar = pathToJar;
    }

    public String getPathToJar() {
        return pathToJar;
    }
}

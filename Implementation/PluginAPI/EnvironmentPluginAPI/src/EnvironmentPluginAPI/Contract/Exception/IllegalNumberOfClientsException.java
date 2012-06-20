package EnvironmentPluginAPI.Contract.Exception;

/**
 * This error may be thrown when the system (MARLA-Server-GUI) is trying to start a game with an incorrect number of
 * clients to work correctly.
 * <br/><br/>
 * It's of course up to the environment which range of players is 'correct'.
 */
public class IllegalNumberOfClientsException extends Exception {
    private final String message;

    /**
     * This error text passed to this constructor will be shown to the user.
     * @param message
     */
    public IllegalNumberOfClientsException(String message) {

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

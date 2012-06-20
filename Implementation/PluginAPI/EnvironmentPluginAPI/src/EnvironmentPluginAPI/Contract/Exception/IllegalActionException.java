package EnvironmentPluginAPI.Contract.Exception;

/**
 * This exception is thrown, when the error occurs, that a MARLA-Client makes an illegal action.
 * <br/><br/>
 * The definition of "legal" is of course completely defined by the environment's logic.
 */
public class IllegalActionException extends RuntimeException {

    public IllegalActionException(String message) {
        super(message);
    }
}

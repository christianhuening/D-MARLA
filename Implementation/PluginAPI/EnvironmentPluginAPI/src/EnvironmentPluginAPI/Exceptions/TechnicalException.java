package EnvironmentPluginAPI.Exceptions;

/**
 * This exception encapsulates severe underlying technical exceptions, that cause application shutdown.
 */
public class TechnicalException extends Exception {
	public TechnicalException(String message) {
		super(message);
	}
}

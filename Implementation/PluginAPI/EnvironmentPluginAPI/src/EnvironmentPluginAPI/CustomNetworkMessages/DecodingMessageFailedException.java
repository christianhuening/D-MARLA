package EnvironmentPluginAPI.CustomNetworkMessages;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 28.05.12
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public class DecodingMessageFailedException extends RuntimeException {
    public DecodingMessageFailedException(String message) {
        super(message);
    }
}

package NetworkAdapter.Interface.Exceptions;

/**
 * Describes the error, that the connection to the remote client was lost unexpectedly.
 */
public class ConnectionLostException extends Exception {

    private int clientId;

    public ConnectionLostException(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}

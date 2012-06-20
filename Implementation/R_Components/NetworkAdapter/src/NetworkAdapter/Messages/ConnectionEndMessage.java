package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * this message signals that an established connection is about to be closed by the sending side.
 */
public class ConnectionEndMessage extends NetworkMessage {

    private String messageCode;

    public ConnectionEndMessage(int clientId, String messageCode) {
        super(clientId);

        this.messageCode = messageCode;
    }
}

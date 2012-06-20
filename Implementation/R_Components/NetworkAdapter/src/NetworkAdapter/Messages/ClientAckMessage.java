package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * This message is sent by the server to signal an accepted player join.
 */
public class ClientAckMessage extends NetworkMessage {

    public ClientAckMessage(int clientId) {
        super(clientId);
    }

}
package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * This message signals clients, that their active session has ended.
 */
public class SessionEndsMessage extends NetworkMessage {

    public SessionEndsMessage(int clientId) {
        super(clientId);
    }

}

package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * This message is sent, if a message was not accepted .
 */
public class NACKMessage extends NetworkMessage {

    String reason;

    public NACKMessage(int clientId, String reason) {
        super(clientId);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
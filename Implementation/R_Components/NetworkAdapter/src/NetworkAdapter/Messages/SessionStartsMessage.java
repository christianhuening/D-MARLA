package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * This message signals a client, that it is about to be used in a session.
 */
public class SessionStartsMessage extends NetworkMessage {

    private int gamesToBePlayed;

    public SessionStartsMessage(int clientId, int gamesToBePlayed) {
        super(clientId);
        this.gamesToBePlayed = gamesToBePlayed;
    }

    public int getGamesToBePlayed() {
        return gamesToBePlayed;
    }

}

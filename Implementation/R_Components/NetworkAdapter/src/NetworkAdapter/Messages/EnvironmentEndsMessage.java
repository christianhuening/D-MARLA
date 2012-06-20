package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

/**
 * This message is sent by the server, to inform clients, that a running game ends.
 */
public class EnvironmentEndsMessage extends NetworkMessage {

	private boolean gameWon;

	public EnvironmentEndsMessage(boolean won, int clientId) {
		super(clientId);
		this.gameWon = won;
	}

	/**
	 * True, if the receiving client won the game that ended.
	 * @return
	 */
    public boolean getGameWon() {
	    return gameWon;
	}
}
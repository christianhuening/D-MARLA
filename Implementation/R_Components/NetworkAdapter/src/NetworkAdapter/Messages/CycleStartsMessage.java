package NetworkAdapter.Messages;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

/**
 * This message is sent by the server to inform clients about the game start.
 */
public class CycleStartsMessage extends NetworkMessage {

    private IEnvironmentConfiguration environmentConfiguration;

    public CycleStartsMessage(int clientId, IEnvironmentConfiguration environmentConfiguration) {
        super(clientId);
        this.environmentConfiguration = environmentConfiguration;
    }

    /**
     * @return Arbitrary info that defines the environment configuration. May be null
     */
    public IEnvironmentConfiguration getEnvironmentConfiguration() {
        return environmentConfiguration;
    }
}
package NetworkAdapter.Messages;


import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

import java.net.InetAddress;

public class ClientJoinMessage extends NetworkMessage {
    private String agentName;
    private InetAddress address;

    public String getAgentName() {
        return agentName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public ClientJoinMessage( int clientId, String agentName) {
        super(clientId);
        this.agentName = agentName;
        this.address = null;
    }

    public ClientJoinMessage(int clientId, String agentName, InetAddress address) {
        super(clientId);
        this.agentName = agentName;
        this.address = address;
    }
}
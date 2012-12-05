package NetworkAdapter.Interface;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;

import java.security.InvalidParameterException;

/**
 * This interface defines the possibilities for AI Clients to control their connection to the GameServer.
 */
public interface IClientNetworkAdapter {
    /**
     * True, if an active connection to a GameServer is established.
     * @return
     */
    public boolean isConnected();

    /**
     * Tries to connect to the given IP or hostname.
     * @param hostname the host to connect to (IPv4, IPv6 address or hostname), != null
     * @param port the server's main port to connect to, != null
     * @param clientName not empty, != null
     * @throws InvalidParameterException if the hostname or the port weren't valid
     */
    public void connectToServer(String hostname, int port, String clientName) throws HostUnreachableException, InvalidParameterException, TechnicalException;

    /**
     * Returns the id that was assigned by the server.
     * @return >= 0
     * @throws NetworkAdapter.Interface.Exceptions.NotConnectedException if no server connection was established.
     */
    public int getClientId() throws NotConnectedException;

    /**
     * This method can be used to subscribe for events (in the form of INetworkMessages) from the network.
     * @param eventHandler - A class implementing the INetworkMessageReceivedEventHandler interface
     * @param messageType the class object of the message type
     */
    public <T extends NetworkMessage> void subscribeForNetworkMessageReceivedEvent(INetworkMessageReceivedEventHandler<T> eventHandler, Class messageType);

    /**
     * Sends a message via the network.
     * @param message != null
     * @param channel the network channel on which to send the message
     * @throws NotConnectedException if no active connection was in place
     * @throws NetworkAdapter.Interface.Exceptions.ConnectionLostException if the connection to the server was aborted unexpectedly
     */
    public void sendNetworkMessage(NetworkMessage message, MessageChannel channel) throws NotConnectedException, ConnectionLostException;

    /**
     * Disconnects from the server, if a connection is established. Does nothing else.
     */
    public void disconnect();
}

package NetworkAdapter.Implementation;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Messages.ConnectionEndMessage;

import java.net.InetAddress;

/**
 * This class represents a channel connection. It runs in an own thread and allows the sending and event based receiving
 * of network messages.
 */
class NetworkChannel<M extends NetworkMessage> extends Thread {
    private NetworkAccessProtocol networkAccessProtocol;
    private NetworkMessageReceivedHandle handle;
    private ClassLoader parentThreadClassLoader;

    /**
     * Creates a thread that receives and sends messages on the given network protocol.
     *
     * @param networkAccessProtocol an established protocol !=  null
     * @param handle the handle for the event of an incoming network message
     * @param contextClassLoader
     */
    public NetworkChannel(NetworkAccessProtocol networkAccessProtocol, NetworkMessageReceivedHandle handle, ClassLoader contextClassLoader) {
        super("NetworkChannel");
        this.networkAccessProtocol = networkAccessProtocol;
        this.handle = handle;
        this.parentThreadClassLoader = contextClassLoader;
    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(parentThreadClassLoader);

        while (!isInterrupted()) {
            try {
                NetworkMessage message = networkAccessProtocol.readMessage();
                deliverMessage(message);
            } catch (ConnectionLostException e) {
                if (!isInterrupted()) {
                    deliverException(e);
                }
            } catch (NotConnectedException e) {
                deliverException(e);
            }
        }
    }

    /**
     * Encodes the network message and tries to send it via the socket.
     *
     * @param message the message to send via the network
     * @throws ConnectionLostException if the connection to the remote end was not reachable.
     */
    public void sendNetworkMessage(NetworkMessage message) throws ConnectionLostException {
        networkAccessProtocol.writeMessage(message);
    }

    /**
     * calls listener, if present, and delivers message
     *
     * @param message the NetworkMessage that is to distribute != null
     */
    protected void deliverMessage(NetworkMessage message) throws NotConnectedException, ConnectionLostException {
        if (handle != null) {
            handle.handleMessage(message);
        }
    }

    protected void deliverException(Exception exception) {
        if (handle != null) {
            handle.handleException(exception);
        }
    }

    /**
     * The remote end's IP address
     *
     * @return != null
     */
    public InetAddress getRemoteAddress() {
        return networkAccessProtocol.getClientAddress();
    }

    /**
     * sends the connectionEndMessage and closes the connection.
     *
     * @param message
     * @throws ConnectionLostException
     */
    public void close(ConnectionEndMessage message) throws ConnectionLostException {
        interrupt();
        networkAccessProtocol.closeConnection(message);
    }

    /**
     * hardly closes the channel. Doesn't wait for anything.
     */
    public void forceClose() {
        interrupt();

        networkAccessProtocol.forceClose();
    }

    /**
     * AIRunner.Interface for users of a network channel.
     */
    public interface NetworkMessageReceivedHandle<M> {
        /**
         * Handles incoming messages from the network.
         *
         * @param message the message that was received
         */
        public void handleMessage(M message) throws NotConnectedException, ConnectionLostException;

        /**
         * Handles the exceptions that can occur when dealing with (mostly) network errors.
         * Note: the exception is not thrown, but given as parameter!
         *
         * @param exception the exception to handle. Will be one of: ConnectionLostException, TechnicalException
         */
        public void handleException(Exception exception);
    }
}

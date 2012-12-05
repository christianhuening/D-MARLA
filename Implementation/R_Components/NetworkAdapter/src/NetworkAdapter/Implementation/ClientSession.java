package NetworkAdapter.Implementation;

import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Messages.ConnectionEndMessage;

import java.util.Date;

/**
 * This class represents a connected client. It holds a control and a data channel.
 */
class ClientSession {
    private int clientId;
    private String clientName;
    public NetworkChannel controlChannel;
    public NetworkChannel dataChannel;
    private Date connectionTime;

    public ClientSession(int clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
        connectionTime = new Date();
    }

    /**
     * the client's session id
     *
     * @return int > 0
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * the name, the client provided
     *
     * @return may be null
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * the creation time of this session.
     *
     * @return != null
     */
    public Date getConnectionTime() {
        return connectionTime;
    }

    /**
     * sends the connection end message via the data- and control channels and closes the connections.
     *
     * @param message
     * @throws ConnectionLostException
     */
    public void close(ConnectionEndMessage message) throws ConnectionLostException {
        controlChannel.close(message);

        if (dataChannel != null) {
            dataChannel.close(message);
        }
    }

    /**
     * Forcibly closes all related connections without abiding the protocol.
     */
    public void forceClose() {
        controlChannel.forceClose();

        if (dataChannel != null) {
            dataChannel.forceClose();
        }
    }
}

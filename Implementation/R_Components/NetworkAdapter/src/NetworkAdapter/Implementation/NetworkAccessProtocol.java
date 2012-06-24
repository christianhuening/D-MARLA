package NetworkAdapter.Implementation;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import Exceptions.TypeIsNotSerializableException;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Messages.ConnectionEndMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This object offers read and write access to the network in the form of CustomNetworkMessages.
 */
class NetworkAccessProtocol {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int clientId = -1;

    /**
     * Creates the NetworkAccessProtocol object.
     *
     * @param socket an open socket != null
     */
    NetworkAccessProtocol(Socket socket) throws IOException {
        this.socket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ContextAwareObjectInputStream(socket.getInputStream());
    }

    /**
     * Writes a network message to the stream.
     *
     * @param message the message to send
     * @throws ConnectionLostException if socket not writable.
     */
    public void writeMessage(NetworkMessage message) throws ConnectionLostException {

        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

        } catch (NotSerializableException ne) {
            throw new TypeIsNotSerializableException(ne);
        } catch (IOException e) {
            throw new ConnectionLostException(message.getClientId());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * reads raw byte stream from network and creates a NetworkMessage from it.
     *
     * @return the message, that was read
     * @throws ConnectionLostException if unable to read from socket.
     */
    public NetworkMessage readMessage() throws ConnectionLostException {
        NetworkMessage message = null;
        String messageType = null;

        // try to read the raw data from the network and construct a message object from it
        try {

            message = (NetworkMessage)objectInputStream.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ConnectionLostException(clientId);
        } catch (ClassNotFoundException ex) {

            System.err.println("Unable to find class. Make sure the same version of all types are known on both server and client ends.");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(NetworkAccessProtocol.class.getName()).log(Level.SEVERE, "An exception occurred while invoking the contract constructor of '" + message.getClass() + "'.\nReason:", ex);
        }

        return message;
    }

    public InetAddress getClientAddress() {
        return socket.getInetAddress();
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * informs the remote end about the connection closing und closes the socket.
     *
     * @param message
     * @throws ConnectionLostException
     */
    public void closeConnection(ConnectionEndMessage message) throws ConnectionLostException {
        writeMessage(message);
        try {
            socket.close();
        } catch (IOException e) {
            throw new ConnectionLostException(clientId);
        }
    }

    public void forceClose() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}

package NetworkAdapter.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import Exceptions.ErrorMessages;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import NetworkAdapter.Interface.MessageChannel;
import NetworkAdapter.Interface.NetworkEventType;
import NetworkAdapter.Messages.ClientAckMessage;
import NetworkAdapter.Messages.ClientJoinMessage;
import NetworkAdapter.Messages.ConnectionEndMessage;
import NetworkAdapter.Messages.NACKMessage;
import Settings.AppSettings;
import Settings.SettingException;
import TransportTypes.TNetworkClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

class ServerNetworkAdapterUseCase
        extends AbstractNetworkAdapterUseCase
        implements IServerNetworkAdapter,
        NetworkChannel.NetworkMessageReceivedHandle<NetworkMessage> {

    private ReentrantLock clientsAccess = new ReentrantLock(true);
    private BlockingQueue<NetworkMessage> messages = new LinkedBlockingQueue<NetworkMessage>();
    private BlockingQueue<Exception> exceptions = new LinkedBlockingQueue<Exception>();
    private int controlListeningPort = 0;
    private int dataListeningPort = 0;

    private ServerSocket controlSocket; //socket for client control communication
    private SessionCreator sessionCreator; // extra thread for waiting on client control connections
    private List<ClientSession> clients = new ArrayList<ClientSession>();

    private ServerSocket dataSocket; //socket for data transfer
    private DataChannelCreator dataChannelCreator; // extra thread for waiting on client data channel connections

    /**
     * Sets up a tcp host, listening on the ports specified in the settings.properties.
     *
     * @throws Settings.SettingException,     if a problem with the application's settings file occurs
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException, if a problem with the connection occurs
     */
    public ServerNetworkAdapterUseCase() throws SettingException, TechnicalException {

        //try to read the port settings from the application's settings file
        controlListeningPort = AppSettings.getInt("controlListeningPort");
        dataListeningPort = AppSettings.getInt("dataListeningPort");
    }

    public ServerNetworkAdapterUseCase(int port) {
        controlListeningPort = port;
        dataListeningPort = controlListeningPort + 1;
    }

    /**
     * @throws NetworkAdapter.Interface.Exceptions.ConnectionLostException if any application settings were unreachable or configured incorrectly
     * @throws TechnicalException if any severe technical exceptions occur, i.e. the port is blocked.
     */
    public void startHosting() throws TechnicalException, ConnectionLostException {

        //try setting up the listening ports
        try {
            if (controlSocket == null) {
                controlSocket = new ServerSocket(controlListeningPort);
            }

        } catch (IOException ex) {
            throw new TechnicalException(ErrorMessages.get("unableToOpenListenPort") + controlListeningPort + "\n" + ex.getLocalizedMessage());
        }

        try {
            if (dataSocket == null) {
                dataSocket = new ServerSocket(dataListeningPort);
            }
        } catch (IOException ex) {
            throw new TechnicalException(ErrorMessages.get("unableToOpenListenPort") + dataListeningPort + "\n" + ex.getLocalizedMessage());
        }
        // starts a thread, that listens for client control connections and starts control sessions for them
        if (sessionCreator == null) {
            sessionCreator = new SessionCreator();
            sessionCreator.start();
        }

        // starts a thread, that listens for client connections and starts data channel sessions for them
        if (dataChannelCreator == null) {
            dataChannelCreator = new DataChannelCreator();
            dataChannelCreator.start();
        }
    }

    public void stopHosting() {

        try {
            // stop session and data channel creators
            dataChannelCreator.interrupt();
            sessionCreator.interrupt();

            // stop listening for new connections
            controlSocket.close();
            dataSocket.close();

            // stop all waiting Threads
            for (ClientSession c : clients) {
                try {
                    c.close(new ConnectionEndMessage(c.getClientId(), "serverShuttingDown"));
                } catch (ConnectionLostException e) {
                } // server is shutting down anyway
            }
        } catch (IOException ex) {
        }
    }

    /**
     * puts the message in the message queue of the main process
     *
     * @param message
     */
    @Override
    public void handleMessage(NetworkMessage message) {
        informSubscribers(message);
    }

    /**
     * puts the exception in the exception queue of the main process
     *
     * @param exception the exception to handle. Will be one of: ConnectionLostException, TechnicalException
     */
    public void handleException(Exception exception) {
        if (exception instanceof ConnectionLostException) {
            int clientId = ((ConnectionLostException) exception).getClientId();
            if (clientId > -1 && clientId < clients.size()) {
                ClientSession tmp = clients.get(clientId);
                tmp.forceClose();
                clients.set(clientId, null);
                fireNetworkEvent(NetworkEventType.ConnectionLost, clientId);
            }
        }


        exception.printStackTrace();
    }

    @Override
    public void sendNetworkMessage(NetworkMessage message, MessageChannel channel) throws NotConnectedException, ConnectionLostException {

        if (isValidClientId(message.getClientId())) {
            ClientSession clientSession = clients.get(message.getClientId());

            if (channel == MessageChannel.DATA) {
                clientSession.dataChannel.sendNetworkMessage(message);
            } else {
                clientSession.controlChannel.sendNetworkMessage(message);
            }
        } else {
            throw new NotConnectedException();
        }

    }

    @Override
    public List<TNetworkClient> getConnectedClients() {
        List<TNetworkClient> result = new LinkedList<TNetworkClient>();

        for (ClientSession session : clients) {
            if (session != null) {
                result.add(new TNetworkClient(session.getClientId(), session.getClientName(), session.controlChannel.getRemoteAddress(), session.getConnectionTime()));
            }
        }

        return result;
    }

    /**
     * @param i
     * @return Determines if a clientId is valid. Valid if i is an existing array index and there is no client session
     *         in that slot.
     */
    private boolean isValidClientId(int i) {
        if (i < 0
                || i >= clients.size()
                || clients.isEmpty()) {
            return false;
        }

        return clients.get(i) != null;
    }

    /**
     * finds an index in the clients list that is not occupied and stores a new client session on the
     * given port in there. (thread safe)
     */
    private int insertControlSession(NetworkAccessProtocol networkAccessProtocol, String clientName) throws ConnectionLostException {

        //remember information for ack answer and session creation
        boolean freeId = false;
        int id = -1;

        clientsAccess.lock(); //prevent our client list from being changed while we rely on the structure

        //find and id that is not occupied with a session
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) == null) {
                freeId = true;
                id = i;
                break;
            }
        }

        // create a client session in the right spot
        ClientSession session;

        if (freeId) {
            session = new ClientSession(id, clientName);
            clients.set(id, session);
        } else {
            id = clients.size();
            session = new ClientSession(id, clientName);
            clients.add(session);
        }

        clientsAccess.unlock();

        //start listening on the control connection
        session.controlChannel = new NetworkChannel<NetworkMessage>(networkAccessProtocol, this, Thread.currentThread().getContextClassLoader());
        session.controlChannel.start();

        return id;
    }

    private boolean attachDataChannel(NetworkAccessProtocol networkAccessProtocol, int clientId) {

        boolean result = false;

        clientsAccess.lock();

        if (isValidClientId(clientId)) {
            ClientSession clientSession = clients.get(clientId);
            clientSession.dataChannel = new NetworkChannel<NetworkMessage>(networkAccessProtocol, this, Thread.currentThread().getContextClassLoader());
            clientSession.dataChannel.start();
            result = true;
        }

        clientsAccess.unlock();

        return result;
    }

    /**
     * extra thread for waiting (blocked) for new client control connections and establishing them.
     */
    private class SessionCreator extends Thread {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    /*
                     * Read first message from the socket. If it is a protocol-conform ClientJoin Message, create client
                     * session and send an ack. If not, notice about protocol violation and close socket.
                     */
                    Socket tmp = controlSocket.accept();

                    if (isInterrupted()) {
                        break;
                    }

                    NetworkAccessProtocol networkAccessProtocol = new NetworkAccessProtocol(tmp);

                    NetworkMessage message = networkAccessProtocol.readMessage();

                    if (message instanceof ClientJoinMessage) {
                        int clientId = insertControlSession(networkAccessProtocol, ((ClientJoinMessage) message).getAgentName());
                        networkAccessProtocol.setClientId(clientId);
                        networkAccessProtocol.writeMessage(new ClientAckMessage(clientId));
                    } else {
                        networkAccessProtocol.writeMessage(new NACKMessage(-1, "networkProtocolViolated"));
                    }
                }
            } catch (IOException ex) {
                exceptions.add(new TechnicalException("unableToReadControlSocket"));
            } catch (ConnectionLostException e) {
                exceptions.add(e);
            }
        }
    }

    /**
     * extra thread for waiting (blocked) for new client data channel connections and attaching them to their clientConnection.
     */
    private class DataChannelCreator extends Thread {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    /*
                    * Read first message from the socket. If it is a protocol-conform ClientJoin Message, and the
                    * clientId has a running connection, attach the data connection to the session and confirm via ack.
                    * If something goes wrong, notice about protocol violation or clientId inconsistency and close
                    * socket.
                    */
                    Socket socket = dataSocket.accept();

                    if (isInterrupted()) {
                        break;
                    }

                    NetworkAccessProtocol networkAccessProtocol = new NetworkAccessProtocol(socket);

                    NetworkMessage message = networkAccessProtocol.readMessage();
                    if (message instanceof ClientJoinMessage) {

                        int clientId = message.getClientId();
                        if (attachDataChannel(networkAccessProtocol, clientId)) {
                            networkAccessProtocol.writeMessage(new ClientAckMessage(clientId));
                            networkAccessProtocol.setClientId(clientId);
                            informSubscribers(message);
                            fireNetworkEvent(NetworkEventType.Connected, clientId);
                        } else {
                            networkAccessProtocol.writeMessage(new NACKMessage(clientId, "invalidClientId"));
                        }

                    } else {
                        networkAccessProtocol.writeMessage(new NACKMessage(message.getClientId(), "networkProtocolViolated"));
                    }
                }
            } catch (IOException ex) {
                exceptions.add(new TechnicalException("unableToReadControlSocket"));
            } catch (ConnectionLostException e) {
                exceptions.add(e);
            }
        }


    }
}
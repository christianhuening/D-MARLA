package NetworkAdapter.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.MessageChannel;

import java.security.InvalidParameterException;

/**
 * Represents the implementation of the IClientNetworkAdapterInterface.
 */
public class ClientNetworkAdapterComponent implements IClientNetworkAdapter {

    private ClientNetworkAdapterUseCase clientNetworkAdapterUseCase;

    public ClientNetworkAdapterComponent() {
        clientNetworkAdapterUseCase = new ClientNetworkAdapterUseCase();
    }

    @Override
    public boolean isConnected() {
        return clientNetworkAdapterUseCase.isConnected();
    }

    @Override
    public <T extends NetworkMessage> void subscribeForNetworkMessageReceivedEvent(INetworkMessageReceivedEventHandler<T> eventHandler, Class messageType) {
        clientNetworkAdapterUseCase.subscribeForNetworkMessageReceivedEvent(eventHandler, messageType);
    }

    @Override
    public void sendNetworkMessage(NetworkMessage message, MessageChannel channel) throws NotConnectedException, ConnectionLostException {
        clientNetworkAdapterUseCase.sendNetworkMessage(message, channel);
    }

    @Override
    public void connectToServer(String address, int port, String clientName) throws HostUnreachableException, InvalidParameterException, TechnicalException {
        clientNetworkAdapterUseCase.connectToServer(address, port, clientName);
    }

    @Override
    public int getClientId() throws NotConnectedException {
        return clientNetworkAdapterUseCase.getClientId();
    }

    @Override
    public void disconnect() {
        clientNetworkAdapterUseCase.disconnect();
    }
}

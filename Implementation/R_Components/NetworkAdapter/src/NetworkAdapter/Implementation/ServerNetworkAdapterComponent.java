package NetworkAdapter.Implementation;

import TransportTypes.TNetworkClient;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import NetworkAdapter.Interface.MessageChannel;
import Settings.SettingException;

import java.util.List;

public class ServerNetworkAdapterComponent implements IServerNetworkAdapter {

    ServerNetworkAdapterUseCase serverNetworkAdapterUseCase;

    public ServerNetworkAdapterComponent() throws TechnicalException, SettingException {
        serverNetworkAdapterUseCase = new ServerNetworkAdapterUseCase();
    }

    /**
     * <b>For unit testing ONLY!!!</b>
     * @param port
     */
    public ServerNetworkAdapterComponent(int port) {
        serverNetworkAdapterUseCase = new ServerNetworkAdapterUseCase(port);
    }

    @Override
    public <T extends NetworkMessage> void subscribeForNetworkMessageReceivedEvent(INetworkMessageReceivedEventHandler<T> eventHandler, Class messageType) {
        serverNetworkAdapterUseCase.subscribeForNetworkMessageReceivedEvent(eventHandler, messageType);
    }

    @Override
    public void startHosting() throws TechnicalException, ConnectionLostException {
        serverNetworkAdapterUseCase.startHosting();
    }

    @Override
    public void sendNetworkMessage(NetworkMessage message, MessageChannel channel) throws NotConnectedException, ConnectionLostException {
        serverNetworkAdapterUseCase.sendNetworkMessage(message, channel);
    }

    @Override
    public List<TNetworkClient> getConnectedClients() {
        return serverNetworkAdapterUseCase.getConnectedClients();
    }

    @Override
    public void stopHosting() {
        serverNetworkAdapterUseCase.stopHosting();
    }
}
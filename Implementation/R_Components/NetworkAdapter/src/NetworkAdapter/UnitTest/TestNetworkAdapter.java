import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Implementation.ClientNetworkAdapterComponent;
import NetworkAdapter.Implementation.ServerNetworkAdapterComponent;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.MessageChannel;
import NetworkAdapter.Interface.NetworkEventType;
import NetworkAdapter.Messages.ClientAckMessage;
import NetworkAdapter.Messages.NACKMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the basic functionality of the network adapter component.
 * <p/>
 * TODO: remove factory game specific types, make all tests executable
 */
public class TestNetworkAdapter implements INetworkMessageReceivedEventHandler {

    static ServerNetworkAdapterComponent server;
    static ClientNetworkAdapterComponent client;
//    private TGameState actual;
//    private  TGameState transmitted;

    // before each test
    @Before
    public void setUp() {
        server = new ServerNetworkAdapterComponent(20000);
    }

    // after each test
    @After
    public void tearDown() {
        server.stopHosting();
    }

    @Test
    public void TestServerHosting() throws TechnicalException, ConnectionLostException, HostUnreachableException, NotConnectedException, InterruptedException {
        server.subscribeForNetworkMessageReceivedEvent(new INetworkMessageReceivedEventHandler() {
            @Override
            public void onMessageReceived(NetworkMessage message) {
                System.out.println("server: " + message);
            }

            @Override
            public void onNetworkEvent(NetworkEventType type, int clientId) {

            }
        }, NetworkMessage.class);

        server.startHosting();
        client = new ClientNetworkAdapterComponent();


        int nrOfMessages = 500000;
        TestNetworkMessageHandler clientHandler = new TestNetworkMessageHandler("client1", nrOfMessages);

        client.subscribeForNetworkMessageReceivedEvent(clientHandler, NetworkMessage.class);
        client.connectToServer("localhost", 20000, "TestServerHosting");

        Assert.assertTrue("Client should have been connected, but it wasn't.", server.getConnectedClients().size() > 0);


        for (int i = 0; i < nrOfMessages; i++) {
            server.sendNetworkMessage(new NACKMessage(0, "test"), MessageChannel.DATA);
        }

        clientHandler.start();
        clientHandler.join();

        Assert.assertEquals(nrOfMessages, clientHandler.getMessageCount());

        server.stopHosting();
    }

    @Override
    public void onMessageReceived(NetworkMessage message) {
        //Assert.assertTrue(actual.equals(transmitted));

        synchronized (this) {
            notify();
        }
    }

    @Override
    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMessageReceived(ClientAckMessage message) {
        Assert.assertTrue("Falscher MessageType übertragen.", message instanceof ClientAckMessage);
    }
}

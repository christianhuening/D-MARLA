import NetworkAdapter.Implementation.ClientNetworkAdapterComponent;
import NetworkAdapter.Implementation.ServerNetworkAdapterComponent;
import org.junit.After;
import org.junit.Before;

/**
 * Tests the basic functionality of the network adapter component.
 *
 * TODO: remove factory game specific types, make all tests executable
 */
public class TestNetworkAdapter //implements INetworkMessageReceivedEventHandler<GameStateMessage>
{

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

//    @Test
//    public void TestServerHosting() throws HostUnreachableException, ConnectionLostException, TechnicalException, NotConnectedException, InterruptedException {
//        server.subscribeForNetworkMessageReceivedEvent(new INetworkMessageReceivedEventHandler<NetworkMessage>() {
//            @Override
//            public void onMessageReceived(NetworkMessage message) {
//                System.out.println("server: " + message);
//            }
//
//            @Override
//            public void onNetworkEvent(NetworkEventType type, int clientId) {
//
//            }
//        }, NetworkMessage.class);
//        server.startHosting();
//        client = new ClientNetworkAdapterComponent();
//        client.subscribeForNetworkMessageReceivedEvent(new INetworkMessageReceivedEventHandler<NetworkMessage>() {
//            @Override
//            public void onMessageReceived(NetworkMessage message) {
//                System.out.println("client: " + message);
//            }
//
//            @Override
//            public void onNetworkEvent(NetworkEventType type, int clientId) {
//
//            }
//        }, GameStartsMessage.class);
//
//        client.connectToServer("localhost", 20000, "TestServerHosting");
//
//        Assert.assertTrue("Client should have been connected, but it wasn't.", server.getConnectedClients().size() > 0);
//
//        client.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//        server.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.CONTROL);
//        client.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.CONTROL);
//        server.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//        client.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//        server.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.CONTROL);
//        client.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//        server.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//        client.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.CONTROL);
//        server.sendNetworkMessage(new GameStartsMessage(0, Faction.BLUE), MessageChannel.DATA);
//
//        server.stopHosting();
//    }
//
//    @Override
//    public void onMessageReceived(GameStateMessage message) {
//        transmitted = message.getMapState();
//
//        Assert.assertTrue("active player wrong", actual.getActivePlayer().equals(transmitted.getActivePlayer()));
//        Assert.assertTrue("turn wrong", actual.getTurn() == transmitted.getTurn());
//        Assert.assertTrue("round wrong", actual.getRound() == transmitted.getRound());
//        Assert.assertTrue("time wrong", actual.getGameStartedAt().equals(transmitted.getGameStartedAt()));
//        Assert.assertTrue("factories wrong", actual.getFactories().equals(transmitted.getFactories()));
//
//        Assert.assertTrue(actual.equals(transmitted));
//
//        synchronized (this) {
//            notify();
//        }
//    }

//    @Override
//    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void onMessageReceived(GameStartsMessage message) {
//        Assert.assertTrue("Falscher MessageType übertragen.", message instanceof GameStartsMessage);
//        Assert.assertTrue("(Farb)Daten korrupt.", ((GameStartsMessage) message).getFaction().equals(Faction.BLUE));
//    }
}

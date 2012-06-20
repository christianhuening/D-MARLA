//import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
//import EnvironmentPluginAPI.CustomNetworkMessages.DecodingMessageFailedException;
//import Factory.CustomMessages.GameStateMessage;
//import Factory.GameLogic.Enums.Faction;
//import Factory.GameLogic.Exceptions.ConsistencyFaultException;
//import Factory.GameLogic.TransportTypes.*;
//import NetworkAdapter.Implementation.ClientNetworkAdapterComponent;
//import NetworkAdapter.Implementation.ServerNetworkAdapterComponent;
//import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
//import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
//import NetworkAdapter.Interface.Exceptions.NotConnectedException;
//import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
//import NetworkAdapter.Interface.MessageChannel;
//import NetworkAdapter.Interface.NetworkEventType;
//import org.joda.time.DateTime;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//
///**
//* TODO: fix an document me!!!
//*/
//public class TestCustomNetworkMessages implements INetworkMessageReceivedEventHandler<GameStateMessage> {
//
//    static ServerNetworkAdapterComponent server;
//    static ClientNetworkAdapterComponent client;
//    private TGameState actual;
//    private TGameState transmitted;
//
//    // before each test
//    @Before
//    public void setUp() {
//
//    }
//
//    // after each test
//    @After
//    public void tearDown() {
//
//    }
//
//    @Override
//    public void onMessageReceived(GameStateMessage message) {
////        transmitted = message.getEnvironmentState();
////
////        Assert.assertTrue("active player wrong", actual.getActivePlayer().equals(transmitted.getActivePlayer()));
////        Assert.assertTrue("turn wrong", actual.getTurn() == transmitted.getTurn());
////        Assert.assertTrue("round wrong", actual.getRound() == transmitted.getRound());
////        Assert.assertTrue("time wrong", actual.getGameStartedAt().equals(transmitted.getGameStartedAt()));
////        Assert.assertTrue("factories wrong", actual.getFactories().equals(transmitted.getFactories()));
////
////        Assert.assertTrue(actual.equals(transmitted));
////
////        synchronized (this) {
////            notify();
////        }
//    }
//
//    @Override
//    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//
//    @Test
//    public void GameStateMessageEncodeDecodeOverNetworkTest() throws DecodingMessageFailedException, ConnectionLostException, TechnicalException, NotConnectedException, InterruptedException, HostUnreachableException, ConsistencyFaultException {
//
//        server = new ServerNetworkAdapterComponent(20000);
//        server.startHosting();
//        client = new ClientNetworkAdapterComponent();
//        client.connectToServer("localhost", 20000, "GameStateMessageEncodeDecodeOverNetworkTest");
//
//        client.subscribeForNetworkMessageReceivedEvent(this, GameStateMessage.class);
//
//        for (int i = 0; i < 500; i++) {
//
//            synchronized (this) {
//                actual = generateNewGameState();
//                server.sendNetworkMessage(new GameStateMessage(0, actual), MessageChannel.DATA);
//                wait();
//            }
//        }
//
//        server.stopHosting();
//    }
//
//    @Test
//    public void ActionListMessageEncodeDecodeTest() throws ConsistencyFaultException, ClassNotFoundException, IOException {
//
////        ObjectOutputStream output;
////        ObjectInputStream input;
////        ByteArrayOutputStream outputStream;
////
////        Random random = new Random();
////
////        for (int j = 0; j < 500; j++) {
////            int numberOfUnits = random.nextInt(100);
////
////            List<TAction> actionList = new LinkedList<TAction>();
////
////            for (int i = 0; i < numberOfUnits; i++) {
////                TUnit unit = new TUnit(UUID.randomUUID(), Faction.values()[random.nextInt(Faction.values().length)]);
////
////                actionList.add(new TAction(unit, Direction.values()[random.nextInt(Direction.values().length)]));
////            }
////
////            TActionsInTurn actionsInTurn = new TActionsInTurn(actionList);
////
////            ActionListMessage msg = new ActionListMessage(0, actionsInTurn);
////            outputStream = new ByteArrayOutputStream();
////            output = new ObjectOutputStream(outputStream);
////            output.writeObject(msg);
////            output.flush();
////
////            input = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
////            msg = (ActionListMessage) input.readObject();
////
////            Assert.assertEquals(msg.getActions(), actionsInTurn);
////        }
//    }
//
//    @Test
//    public void GameStateMessageEncodeDecodeTest() throws DecodingMessageFailedException, ConsistencyFaultException, IOException, ClassNotFoundException {
//        TGameState actual;
//        ObjectOutputStream output;
//        ObjectInputStream input;
//        ByteArrayOutputStream outputStream;
//        for (int i = 0; i < 500; i++) {
//            actual = generateNewGameState();
//
//            GameStateMessage msg = new GameStateMessage(0, actual);
//            outputStream = new ByteArrayOutputStream();
//            output = new ObjectOutputStream(outputStream);
//            output.writeObject(msg);
//            output.flush();
//
//            input = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
//            msg = (GameStateMessage) input.readObject();
//
//            TGameState transmitted = msg.getEnvironmentState();
//
//            //Assert.assertTrue("active player wrong", actual.getActivePlayer().equals(transmitted.getActivePlayer()));
//            Assert.assertTrue("turn wrong", actual.getTurn() == transmitted.getTurn());
//            Assert.assertTrue("round wrong", actual.getRound() == transmitted.getRound());
//            Assert.assertTrue("time wrong", actual.getGameStartedAt().equals(transmitted.getGameStartedAt()));
//            Assert.assertTrue("factories wrong", actual.getFactories().equals(transmitted.getFactories()));
//
//            Assert.assertTrue(actual.equals(transmitted));
//        }
//    }
//
//    public static TGameState generateNewGameState() {
//        Random random = new Random();
//
//        TPlayer winningPlayer = null;
//        TPlayer activePlayer;
//
//        if (random.nextInt(2) == 1) {
//            winningPlayer = new TPlayer("asedasfwergrg", Faction.values()[random.nextInt(Faction.values().length)]);
//        }
//
//        activePlayer = new TPlayer("", Faction.values()[random.nextInt(Faction.values().length)]);
//
//
//        List<TFactory> factories = new ArrayList<TFactory>();
//        for (int i = 0; i < random.nextInt(10); i++) {
//            factories.add(new TFactory(random.nextInt(10), random.nextInt(20), Faction.values()[random.nextInt(Faction.values().length)], i));
//        }
//
//        int width = 10 + random.nextInt(11);
//        int height = 10 + random.nextInt(11);
//        TAbstractField[][] map = new TAbstractField[width][height];
//
//        Faction faction;
//        TUnit unit;
//        TAbstractField field;
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//
//                if (random.nextInt(3) == 2) {
//                    faction = Faction.values()[random.nextInt(Faction.values().length)];
//                    unit = new TUnit(UUID.randomUUID(), faction);
//                } else {
//                    unit = null;
//                }
//
//                int fieldType = random.nextInt(4);
//                if (fieldType == 0) {
//                    map[i][j] = new TNormalField(unit);
//                } else if (fieldType == 1) {
//                    map[i][j] = new TInfluenceField(unit, random.nextInt(20));
//                } else {
//                    map[i][j] = new TFactoryField(unit, random.nextInt(3));
//                }
//            }
//        }
//
//        return new TGameState(winningPlayer !=  null, activePlayer, random.nextInt(50000), random.nextInt(50000), new DateTime(), factories, map);
//    }
//}

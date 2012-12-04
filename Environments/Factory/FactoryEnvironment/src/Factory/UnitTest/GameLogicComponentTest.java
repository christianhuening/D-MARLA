package Factory.UnitTest;

import EnvironmentPluginAPI.Contract.Exception.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.GameLogicComponent;
import Factory.GameLogic.TransportTypes.*;
import Factory.GameLogic.Utility.GameInfos;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* Created with IntelliJ IDEA.
* User: TwiG
* Date: 27.05.12
* Time: 18:14
* To change this template use File | Settings | File Templates.
*/
public class GameLogicComponentTest {
    GameLogicComponent gameLogic20;
    GameLogicComponent gameLogic21;

    GameLogicComponent gameLogic6;
    TMapMetaData mapMetaData20;
    TMapMetaData mapMetaData21;
    TMapMetaData mapMetaData6;

    TMARLAClientInstance TMARLAClientInstance1;
    TMARLAClientInstance TMARLAClientInstance2;
    List<TMARLAClientInstance> TMARLAClientInstanceList = new ArrayList<TMARLAClientInstance>();
    int roundsToSpawn = 5;
    ISaveGameStatistics statistics = new DummyStatistics();

    @Before
    public void setUp() throws Exception {
        //null because replay saving is not to be tested
        gameLogic20 = new GameLogicComponent(statistics);
        gameLogic21 = new GameLogicComponent(statistics);
        gameLogic6 = new GameLogicComponent(statistics);


        mapMetaData6 = new TMapMetaData("SmallestMap", 0, 1, 6, 1, 1, 1);
        mapMetaData20 = new TMapMetaData("map1", 0, 1, 20, 20, 10, 3);
        mapMetaData21 = new TMapMetaData("map2", 0, 1, 21, 100, 100, 5);
        TMARLAClientInstance1 = new TMARLAClientInstance("Peter", Faction.BLUE.ordinal());
        TMARLAClientInstance2 = new TMARLAClientInstance("Alberto", Faction.RED.ordinal());
        TMARLAClientInstanceList.add(TMARLAClientInstance1);
        TMARLAClientInstanceList.add(TMARLAClientInstance2);


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testWinCondition() throws TechnicalException, IllegalNumberOfClientsException {
        System.out.println("EXPLANATION:\nFIELDS:\nN = NormalField\nI = InfluenceField\nF = FactoryField\nUNITS:\nB = Unit of the blue faction\nR = Unit of the red faction\n");

        TGameState start = (TGameState) gameLogic6.start(TMARLAClientInstanceList, mapMetaData6);
        print(gameLogic6);
        TAbstractField[][] board = start.getMapFields();
        TUnit unitBlue = board[1][1].getOccupant();
        TUnit unitRed = board[4][1].getOccupant();

        gameLogic6.moveUnit(unitBlue, Direction.RIGHT);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.moveUnit(unitRed, Direction.UP);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.moveUnit(unitBlue, Direction.RIGHT);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.moveUnit(unitRed, Direction.UP);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.moveUnit(unitBlue, Direction.LEFT);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.moveUnit(unitRed, Direction.RIGHT);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.endTurn();
        gameLogic6.moveUnit(unitRed, Direction.UP);
        gameLogic6.endTurn();
        print(gameLogic6);
        gameLogic6.endTurn();
        gameLogic6.endTurn();
        gameLogic6.endTurn();
        gameLogic6.endTurn();
        gameLogic6.endTurn();
        gameLogic6.endTurn();
        print(gameLogic6);

        List<TFactory> factories = ((TGameState)gameLogic6.getCurrentGameState()).getFactories();

        assert (gameLogic6.getCurrentGameState().hasClientMetGoal() == true);

        assert (((TGameState)gameLogic6.getCurrentGameState()).getActivePlayer().getFaction() == Faction.RED);
    }

    @Test
    public void testEndGame() throws Exception {

        /*TGameState start = gameLogic20.start(TMARLAClientInstanceList, mapMetaData20);

        gameLogic20.endTurn();

        gameLogic20.endTurn();
        TGameState beforeEndGame = gameLogic20.endTurn();

        boolean gameWasActive = gameLogic20.end();

        assert (gameWasActive);

        DummyStatistics myStatistics = (DummyStatistics) statistics;

        List<AbstractReplay> replays = myStatistics.getReplays();

        List<TGameState> gameStateList = replays.get(0).getTransportType().getEnvironmentStatesPerTurn();
        assert (gameStateList.get(0).getTurn() == start.getTurn());
        assert (gameStateList.get(3).getTurn() == beforeEndGame.getTurn());
        assert (gameStateList.size() == 4);*/

        //we could test if there was movement recorded, but i assume if the turns go right
        //and the TestMove is green it cant be wrong
    }


    @Test
    public void testExecuteActions() throws Exception {
        TGameState before = (TGameState)gameLogic20.start(TMARLAClientInstanceList, mapMetaData20);


        List<TUnit> units = GameInfos.getUnitsForFaction(before, before.getActivePlayer().getFaction());
        List<TAction> actions = new ArrayList<TAction>();
        List<TPosition> unitPosition = new ArrayList<TPosition>();


        for (TUnit unit : units) {
            actions.add(new TAction(unit, Direction.UP));
            unitPosition.add(GameInfos.getPositionForUnit(before, unit));
        }
        TActionsInTurn actionsInTurn = new TActionsInTurn(actions);
        TGameState after = (TGameState)gameLogic20.executeAction(actionsInTurn);

        // TODO: This test seems to be wrong. Executing an action list should not automatically end the turn afterwards.
        //assert (before.getTurn() + 1 == after.getTurn());

        for (int i = 0; i < unitPosition.size(); i++) {
            UUID newOccupantID = GameInfos.getNeighborFieldForPosition(after, unitPosition.get(i), Direction.UP).getOccupant().getUnitId();
            assert (newOccupantID.equals(actions.get(i).getUnit().getUnitId()));
        }

    }

    @Test
    public void testMoveUnit() throws Exception {

        TGameState before = (TGameState)gameLogic20.start(TMARLAClientInstanceList, mapMetaData20);
        List<TUnit> unitList = new ArrayList<TUnit>();
        unitList = GameInfos.getUnitsForFaction(before, before.getActivePlayer().getFaction());


        TAbstractField[][] board = before.getMapFields();
        TUnit testUnit = board[6][7].getOccupant();

        gameLogic20.endTurn();

        //Should be denied because not your turn
        gameLogic20.moveUnit(testUnit, Direction.UP);

        gameLogic20.endTurn();

        //Should be denied because moving on friendly unit
        gameLogic20.moveUnit(testUnit, Direction.DOWN);


        gameLogic20.moveUnit(testUnit, Direction.UP);
        //Should be denied because of exhaustion
        gameLogic20.moveUnit(testUnit, Direction.UP);

        //Should be denied because of illegal move
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.DOWN);


        System.out.print("Testing Upper Border\n\n");
        //move to the upper edge
        for (int i = 6; i > 0; i--) {
            gameLogic20.endTurn();
            gameLogic20.endTurn();
            gameLogic20.moveUnit(testUnit, Direction.UP);
        }
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.UP_RIGHT);
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.UP_LEFT);
        System.out.print("Testing Left Border\n\n");
        //move into left border
        for (int i = 8; i > 0; i--) {
            gameLogic20.endTurn();
            gameLogic20.endTurn();
            gameLogic20.moveUnit(testUnit, Direction.LEFT);
        }

        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.UP_LEFT);
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.DOWN_LEFT);


        System.out.print("Testing bottom Border\n\n");
        //move into bottom border
        for (int i = 20; i > 0; i--) {
            gameLogic20.endTurn();
            gameLogic20.endTurn();
            gameLogic20.moveUnit(testUnit, Direction.DOWN);
        }

        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.DOWN_LEFT);
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.DOWN_RIGHT);

        System.out.print("Testing right Border\n\n");
        //move into bottom border
        for (int i = 20; i > 0; i--) {
            gameLogic20.endTurn();
            gameLogic20.endTurn();
            gameLogic20.moveUnit(testUnit, Direction.RIGHT);
        }

        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.UP_RIGHT);
        gameLogic20.endTurn();
        gameLogic20.endTurn();
        gameLogic20.moveUnit(testUnit, Direction.DOWN_RIGHT);
    }

    @Test
    public void testEndTurn() throws Exception {
        TGameState before = (TGameState)gameLogic20.start(TMARLAClientInstanceList, mapMetaData20);

        int turnsBeforePlayerBlueWasActive = 0;

        while(before.getActivePlayer().getFaction() != Faction.BLUE) {
            gameLogic20.endTurn();
            before = (TGameState)gameLogic20.getCurrentGameState();
            turnsBeforePlayerBlueWasActive++;
        }

        System.out.println(before.getActivePlayer().getFaction());

        System.out.println(before.toString());

        List<TUnit> unitList = GameInfos.getUnitsForFaction(before, before.getActivePlayer().getFaction());

        for (TUnit unit : unitList) {
            TPosition positionOfDoom = GameInfos.getPositionForUnit(before, unit);

            if(positionOfDoom.getX() == 7 && positionOfDoom.getY() == 6) {
                System.out.println("MOVE!");
                gameLogic20.moveUnit(unit, Direction.UP);
            }
        }

        System.out.println(((TGameState)gameLogic20.getCurrentGameState()).toString());

        gameLogic20.endTurn();

        unitList = GameInfos.getUnitsForFaction(before, before.getActivePlayer().getFaction());

        for (TUnit unit : unitList) {
            gameLogic20.moveUnit(unit, Direction.LEFT);
        }

        TGameState afterTurn1 = (TGameState)gameLogic20.getCurrentGameState();

        assert (!afterTurn1.getMapFields()[6][7].isOccupied());



        //Round and Turn Test

        assert (before.getTurn() + 1 == afterTurn1.getTurn());

        gameLogic20.endTurn();
        TGameState afterTurn2 = (TGameState)gameLogic20.getCurrentGameState();

        assert (before.getRound() + 1 == afterTurn2.getRound());

        //Spawn test

        TGameState spawn = null;
        for (int i = 0; i < (roundsToSpawn * 2) - 3 - turnsBeforePlayerBlueWasActive; i++) {
            gameLogic20.endTurn();
        }

        spawn = (TGameState)gameLogic20.getCurrentGameState();

        System.out.println("turn: " + spawn.getTurn());
        System.out.println(spawn.toString());

        TAbstractField[][] board = spawn.getMapFields();
        assert (!board[6][7].isOccupied());

        gameLogic20.endTurn();
        spawn = (TGameState)gameLogic20.getCurrentGameState();

        board = spawn.getMapFields();

        assert (board[6][7].isOccupied());
    }

    @Test
    public void testStartGame() throws Exception {
        gameLogic20.start(TMARLAClientInstanceList, mapMetaData20);
        TGameState start = (TGameState)gameLogic20.getCurrentGameState();
        /*
        System.out.println(gameLogic20);
        System.out.println(start.getActivePlayer());
        System.out.println(start.getMapFields());
        System.out.println(start.getTurn());
        System.out.println(start.getFactories());
        System.out.println(start.getGameStartedAt());
        System.out.println(start.getRound());
        System.out.println(start.getWinningClient());
        */
    }

    void print(IEnvironment gameLogic) {
        System.out.println(gameLogic + "\n");
    }
}

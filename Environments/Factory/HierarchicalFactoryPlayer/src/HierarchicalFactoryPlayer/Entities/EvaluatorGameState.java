package HierarchicalFactoryPlayer.Entities;

import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;
import Factory.GameLogic.Utility.GameInfos;
import HierarchicalFactoryPlayer.Enums.FieldType;
import HierarchicalFactoryPlayer.Enums.FriendFoe;
import HierarchicalFactoryPlayer.Exceptions.MapIsNoSquareException;
import Varunpat.QuadTree.QuadTree;

import java.util.concurrent.ForkJoinPool;

/**
 * This class evaluates the game state in a recursive manner. Reinforcement Learning is used on a 4 by 4 grid environment
 * which gets singled out by means of a recursive algorithm which breaks up the whole environment into a quad-tree like
 * structure
 */
public class EvaluatorGameState {

    private IAgent evaluator;
    private QuadTree quadTree;
    private boolean firstrun;
    private Faction myFaction;
    private Faction enemyFaction;

    public EvaluatorGameState(IAgent evaluator){
        this.evaluator = evaluator;
        this.firstrun = true;
    }

    /**
     * Evaluates the new game state
     * @param gameState The game state to be evaluated
     */
    public void evaluateNewGameState(TGameState gameState, Faction myFaction, float reward) throws MapIsNoSquareException, TechnicalException {
        this.myFaction = myFaction;
        if (myFaction == Faction.RED) {
            enemyFaction = Faction.BLUE;
        } else {
            enemyFaction = Faction.RED;
        }

        TAbstractField[][] mapFields = gameState.getMapFields();
        int length = mapFields.length;
        if(length % 2 != 0 || mapFields[0].length % 2 != 0){
            throw new MapIsNoSquareException("Map needs to be a square for this to work. Sorry...");
        }

        quadTree = new QuadTree(0,0,length-1,length-1);

        // first calculate interval evaluations
        calculateIntervalEvaluations(length, myFaction, gameState, quadTree);


        // now let the agent to his magic
        applyAgentActions(length, 0, 0, length-1, length-1);
        // apply reward
        evaluator.endEpisode(new StateAction("11"), reward);

        firstrun = true;
    }

    private void calculateIntervalEvaluations(int length, Faction myFaction, TGameState gameState, QuadTree quadTree) {
        evaluate(gameState, myFaction, quadTree, length, 0, 0, length-1, length-1);
    }

    private void evaluate(TGameState gameState, Faction myFaction, QuadTree quadTree, int mapLength, int minX, int minY, int maxX, int maxY){
        // if map is only a 4by4 square, compute
        if(mapLength < 4){
            try {
                throw new TechnicalException("Map is not a quadTreeStructure. Use other edgelength.");
            } catch (TechnicalException e) {
                e.printStackTrace();
            }
        }
        if(mapLength == 4){
            int myUnits = 0;
            int enemyUnits = 0;
            int influenceFields = 0;
            int normalFields = 0;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {

                    TAbstractField field = GameInfos.getFieldForPosition(gameState, new TPosition(x,y));
                    if(field.isOccupied()) {
                        if (field.getOccupant().getControllingFaction().equals(myFaction)) {
                            myUnits++;
                        } else if (field.getOccupant().getControllingFaction().equals(Faction.NEUTRAL)) {
                            // do nothing
                        } else {
                            enemyUnits++;
                        }
                    }

                    if(field instanceof TInfluenceField){
                        influenceFields++;
                    } else {
                        normalFields++;
                    }

                }
            }

            // calculate ratios
            int unitRatio = 5 - enemyUnits;
            int fieldRatio = 5 - normalFields;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    // now store information
                    try {
                        quadTree.set(x, y, new QuadTreeTuple(unitRatio, fieldRatio));
                    } catch ( Exception ex){
                        System.err.println("Error for x=" + x + " and y=" + y + ". Stacktrace was: " + ex.getCause().getMessage());
                        throw ex;
                    }
                }
            }
            return;
        }

        evaluate(gameState, myFaction, quadTree, mapLength / 2, minX, minY, maxX / 2, maxY / 2);                 // left top
        evaluate(gameState, myFaction, quadTree, mapLength / 2, maxX / 2 + 1, minY, maxX, maxY / 2);         // right top
        evaluate(gameState, myFaction, quadTree, mapLength / 2, maxX / 2 + 1, maxY / 2 + 1, maxX, maxY); // right bottom
        evaluate(gameState, myFaction, quadTree, mapLength / 2, minX, maxY / 2 + 1, maxX / 2, maxY);          // left bottom
    }

    /**
     * Executes an agent step for each low level field in the quadTree
     * @param mapLength
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @throws TechnicalException
     */
    private void applyAgentActions(int mapLength, int minX, int minY, int maxX, int maxY) throws TechnicalException {
        if(mapLength < 2){
            try {
                throw new TechnicalException("Something went wrong while going down the tree structure.");
            } catch (TechnicalException e) {
                e.printStackTrace();
            }
        }

        if(mapLength == 2){

            StateAction action;

            // ratios are stored in the QuadTree for all field of the lowest level in each corresponding parent level, so just get any of them
            System.err.println("Trying to read from: ("+minX+"/"+minY+")");
            int unitRatio = ((QuadTreeTuple)quadTree.get(minX, minY, null)).getUnitRatio();
            int fieldRatio = ((QuadTreeTuple)quadTree.get(minX, minY, null)).getFieldRatio();

            if(firstrun){
                action = evaluator.startEpisode(new StateAction(Integer.toString(unitRatio) + Integer.toString(fieldRatio)));
                firstrun = false;
            } else {
                action = evaluator.step(0, new StateAction(Integer.toString(unitRatio) + Integer.toString(fieldRatio)));
            }


            int strPos = 0;
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    // writes '+', '-' or '=' into the quadtree on the lowest level
                    System.out.println("Writing to ("+x+"/"+y+")");
                    quadTree.set(x,y, action.getActionDescription().substring(strPos,strPos+1));
                }
            }

            return;
        }

        // make recursive calls
        applyAgentActions(mapLength / 2, minX, minY, maxX / 2, maxY / 2);                 // left top , correct :)
        applyAgentActions(mapLength / 2, maxX / 2 + 1, minY, maxX, maxY / 2);         // right top,
        applyAgentActions(mapLength / 2, maxX / 2 + 1, maxY / 2 + 1, maxX, maxY); // right bottom
        applyAgentActions(mapLength / 2, minX, maxY / 2 + 1, maxX / 2, maxY);         // left bottom

        /*
            Irgendwas stimmt noch nicht. Felder werden immer noch doppelt durchgegangen. Fehler in Zweile 148 Cast von String auf QuadTreeTuple
         */
    }




    /**
     * Retrieves the evaluation for a specific field and unitPosition
     * @param unit The unit for which to look up the state
     * @return A RawState object holding a readable representation of the current State
     */
    public RawState getEvaluation(TGameState tGamestate, TUnit unit) {
        TPosition pos = GameInfos.getPositionForUnit(tGamestate, unit);

        // calc border indes
        int border = tGamestate.getMapFields().length-1;

        // create wall field for encountered walls
        RawField wallField = new RawField();
        wallField.setFieldController(FriendFoe.FRIEND);
        wallField.setFieldType(FieldType.FACTORY);
        wallField.setRemainingTimeToSpawn(100);
        wallField.setUnit(FriendFoe.NONE);

        // create new rawState to fill
        RawState rawState = new RawState();
        RawField rawfield;

        // middle field
        rawfield = generateRawField(
                tGamestate,
                GameInfos.getFieldForPosition(tGamestate, new TPosition(pos.getX(),pos.getY())),
                (String) quadTree.get(pos.getX(), pos.getY(), null)
        );
        rawState.setMiddle(rawfield);

        // Left
        if(pos.getX() > 0){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.LEFT),
                    (String) quadTree.get(pos.getX() - 1, pos.getY(), null)
            );
            rawState.setLeft(rawfield);
        } else {
            rawState.setLeft(wallField);
        }

        // Right
        if(pos.getX() < border){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.RIGHT),
                    (String) quadTree.get(pos.getX() + 1, pos.getY(), null)
            );
            rawState.setRight(rawfield);
        } else {
            rawState.setRight(wallField);
        }

        // Down
        if(pos.getY() < border){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.DOWN),
                    (String) quadTree.get(pos.getX(), pos.getY() + 1, null)
            );
            rawState.setDown(rawfield);
        } else {
            rawState.setDown(wallField);
        }

        // Down Left
        if (pos.getY() < border && pos.getX() > 0){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.DOWN_LEFT),
                    (String) quadTree.get(pos.getX() - 1, pos.getY() + 1, null)
            );
            rawState.setLeftDown(rawfield);
        } else {
            rawState.setLeftDown(wallField);
        }

        // Down Right
        if (pos.getY() < border && pos.getX() < border){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.DOWN_RIGHT),
                    (String) quadTree.get(pos.getX() + 1, pos.getY() + 1, null)
            );
            rawState.setRightDown(rawfield);
        } else {
            rawState.setRightDown(wallField);
        }

        // Up Left
        if (pos.getY() > 0 && pos.getX() > 0){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.UP_LEFT),
                    (String) quadTree.get(pos.getX() - 1, pos.getY() - 1, null)
            );
            rawState.setLeftTop(rawfield);
        } else {
            rawState.setLeftTop(wallField);
        }

        // Up Right
        if (pos.getY() > 0 && pos.getX() < border){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.UP_RIGHT),
                    (String) quadTree.get(pos.getX() + 1, pos.getY() - 1, null)
            );
            rawState.setRightTop(rawfield);
        } else {
            rawState.setRightTop(wallField);
        }

        // Up
        if (pos.getY() > 0){
            rawfield = generateRawField(
                    tGamestate,
                    GameInfos.getNeighborFieldForPosition(tGamestate, new TPosition(pos.getX(), pos.getY()), Direction.UP),
                    (String) quadTree.get(pos.getX(), pos.getY() - 1, null)
            );
            rawState.setTop(rawfield);
        } else {
            rawState.setTop(wallField);
        }

        TPosition posOfField = new TPosition(pos.getX(),pos.getY());
        TAbstractField field = GameInfos.getFieldForPosition(tGamestate, posOfField);

        // set next target as signal
        rawState.setSignal(findNextTarget(tGamestate, field, posOfField));

        return rawState;
    }

    private RawField generateRawField(TGameState gameState, TAbstractField field, String evaluation) {
        RawField rawField = new RawField();

        rawField.setEvaluation(evaluation);

        if (field.isOccupied()) {
            if (field.getOccupant().getControllingFaction() == myFaction) {
                if (field.getOccupant().getExhaustedForTurn() == gameState.getTurn()) {
                    rawField.setUnit(FriendFoe.EXHAUSTED_FRIEND);
                } else {
                    rawField.setUnit(FriendFoe.FRIEND);
                }
            } else {
                rawField.setUnit(FriendFoe.FOE);
            }
        } else {
            rawField.setUnit(FriendFoe.NONE);
        }

        if (field instanceof TNormalField) {
            rawField.setFieldType(FieldType.NORMAL);
            rawField.setFieldController(FriendFoe.NONE);
        } else {
            int ID = 0;
            if (field instanceof TFactoryField) {
                rawField.setFieldType(FieldType.FACTORY);
                ID = ((TFactoryField) field).getFactoryID();
            } else if (field instanceof TInfluenceField) {
                rawField.setFieldType(FieldType.INFLUENCE);
                ID = ((TInfluenceField) field).getFactoryID();
            }

            TFactory factory = getFactoryForID(gameState, ID);
            rawField.setRemainingTimeToSpawn(factory.getRemainingRoundsForRespawn());
            if (factory.getOwningFaction() == myFaction) {
                rawField.setFieldController(FriendFoe.FRIEND);
            } else {
                rawField.setFieldController(FriendFoe.FOE);
            }
        }
        return rawField;
    }

    private TFactory getFactoryForID(TGameState gameState, int ID) {
        for (TFactory factory : gameState.getFactories()) {
            if (factory.getFactoryID() == ID) {
                return factory;
            }
        }
        return null;
    }

    private Direction findNextTarget(TGameState gameState, TAbstractField field, TPosition positionOfField) {
        int minimumDistance = Integer.MAX_VALUE;
        int currentDistance;
        Direction direction = null;
        TAbstractField[][] board = gameState.getMapFields();
        int topBorder = 0;
        int leftBorder = 0;
        int rightBorder = board.length - 1;
        int bottomBorder = board.length - 1;


        int x = positionOfField.getX();
        int y = positionOfField.getY();


        currentDistance = 0;
        //Looking Right ========================================================================
        for (int i = x; i <= rightBorder; i++) {
            currentDistance++;
            for (int j = 0; j <= bottomBorder; j++) {
                if (board[j][i].isOccupied()) {
                    if (board[j][i].getOccupant().getControllingFaction() == enemyFaction) {
                        if (currentDistance < minimumDistance) {
                            minimumDistance = currentDistance;
                            direction = Direction.RIGHT;
                            break;
                        }
                    }
                }
            }
        }

        if (minimumDistance == 1) {
            return direction;
        }


        currentDistance = 0;
        //Looking Left ========================================================================
        for (int i = x; i >= leftBorder; i--) {
            currentDistance++;
            for (int j = 0; j <= bottomBorder; j++) {
                //check auf Aktives influence feld eifügen

                if (board[j][i].isOccupied()) {
                    if (board[j][i].getOccupant().getControllingFaction() == enemyFaction) {
                        if (currentDistance < minimumDistance) {
                            minimumDistance = currentDistance;
                            direction = Direction.RIGHT;
                            break;
                        }
                    }
                }
            }
        }

        if (minimumDistance == 1) {
            return direction;
        }


        currentDistance = 0;
        //Looking Down  ========================================================================
        for (int i = y; i <= bottomBorder; i++) {
            currentDistance++;
            for (int j = 0; j <= rightBorder; j++) {
                //check auf Aktives influence feld eifügen

                if (board[i][j].isOccupied()) {
                    if (board[i][j].getOccupant().getControllingFaction() == enemyFaction) {
                        if (currentDistance < minimumDistance) {
                            minimumDistance = currentDistance;
                            direction = Direction.RIGHT;
                            break;
                        }
                    }
                }
            }
        }

        if (minimumDistance == 1) {
            return direction;
        }


        currentDistance = 0;
        //Looking UP  ========================================================================
        for (int i = y; i >= topBorder; i--) {
            currentDistance++;
            for (int j = 0; j <= rightBorder; j++) {
                //check auf Aktives influence feld eifügen

                if (board[i][j].isOccupied()) {
                    if (board[i][j].getOccupant().getControllingFaction() == enemyFaction) {
                        if (currentDistance < minimumDistance) {
                            minimumDistance = currentDistance;
                            direction = Direction.RIGHT;
                            break;
                        }
                    }
                }
            }
        }

        return direction;
    }

}

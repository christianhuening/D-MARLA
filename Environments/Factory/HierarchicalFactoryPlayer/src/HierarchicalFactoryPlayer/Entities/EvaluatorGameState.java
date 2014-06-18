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
        TAbstractField[][] mapFields = gameState.getMapFields();
        int length = mapFields.length;
        if(length % 2 != 0 || mapFields[0].length % 2 != 0){
            throw new MapIsNoSquareException("Map needs to be a square for this to work. Sorry...");
        }

        quadTree = new QuadTree(0,0,length,length);

        // first calculate interval evaluations
        calculateIntervalEvaluations(mapFields, myFaction, gameState, quadTree);


        // now let the agent to his magic
        applyAgentActions(length, 0, 0, length-1, length-1);
        // apply reward
        evaluator.endEpisode(new StateAction("11"), reward);

        firstrun = true;
    }

    private void calculateIntervalEvaluations(TAbstractField[][] mapFields, Faction myFaction, TGameState gameState, QuadTree quadTree) {
        int length = mapFields.length;
        ForkEvaluator fe = new ForkEvaluator(gameState, myFaction, quadTree, length, 0, 0, length-1, length-1);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(fe);
    }

    /**
     * Executes an agent step for each low level field in the quadTree
     * @param mapLength
     * @param startX
     * @param startY
     * @param stopX
     * @param stopY
     * @throws TechnicalException
     */
    private void applyAgentActions(int mapLength, int startX, int startY, int stopX, int stopY) throws TechnicalException {
        if(mapLength == 2){

            StateAction action;

            // ratios are stored in the QuadTree for all field of the lowest level in each corresponding parent level, so just get any of them
            int unitRatio = ((QuadTreeTuple)quadTree.get(startX, startY, null)).getUnitRatio();
            int fieldRatio = ((QuadTreeTuple)quadTree.get(startX, startY, null)).getFieldRatio();

            if(firstrun){
                action = evaluator.startEpisode(new StateAction(Integer.toString(unitRatio) + Integer.toString(fieldRatio)));
                firstrun = false;
            } else {
                action = evaluator.step(0, new StateAction(Integer.toString(unitRatio) + Integer.toString(fieldRatio)));
            }


            int strPos = 0;
            for (int x = startX; x <= stopX; x++) {
                for (int y = startY; y <= stopY; y++) {
                    // writes '+', '-' or '=' into the quadtree on the lowest level
                    quadTree.set(x,y, action.getActionDescription().substring(strPos,strPos+1));
                }
            }

            return;
        }

        // split quad-wise otherwise, split value is always an index in the right hand part of the array
        int split = mapLength / 2;

        // make recursive calls
        applyAgentActions(mapLength / 2, 0, 0, split-1, split-1);                 // left top
        applyAgentActions(mapLength / 2, split, 0, mapLength-1, split-1);         // right top
        applyAgentActions(mapLength / 2, split, split, mapLength-1, mapLength-1); // right bottom
        applyAgentActions(mapLength / 2, 0, split, split-1, mapLength-1);         // left bottom
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

}

package HierarchicalFactoryPlayer.Entities;

import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.GameLogic.TransportTypes.TInfluenceField;
import Factory.GameLogic.TransportTypes.TPosition;
import Factory.GameLogic.Utility.GameInfos;
import Varunpat.QuadTree.QuadTree;

import java.util.concurrent.RecursiveAction;


public class ForkEvaluator extends RecursiveAction {

    private int mapLength;
    private final TGameState gameState;
    private final Faction myFaction;
    private final QuadTree quadTree;
    private final int startX;
    private final int startY;
    private final int stopX;
    private final int stopY;


    public ForkEvaluator(TGameState gameState, Faction myFaction, QuadTree quadTree, int mapLength, int startX, int startY, int stopX, int stopY){
        this.gameState = gameState;
        this.myFaction = myFaction;
        this.quadTree = quadTree;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        this.mapLength = mapLength;
    }

    protected void computeDirectly(){
        int myUnits = 0;
        int enemyUnits = 0;
        int influenceFields = 0;
        int normalFields = 0;

        for (int x = startX; x <= stopX; x++) {
            for (int y = startY; y <= stopY; y++) {

                TAbstractField field = GameInfos.getFieldForPosition(gameState, new TPosition(x,y));

                if(field.getOccupant().getControllingFaction().equals(myFaction)){
                    myUnits++;
                } else if(field.getOccupant().getControllingFaction().equals(Faction.NEUTRAL)){
                    // do nothing
                } else {
                    enemyUnits++;
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

        for (int x = startX; x <= stopX; x++) {
            for (int y = startY; y <= stopY; y++) {
                // now store information
                quadTree.set(x, y, new QuadTreeTuple(unitRatio, fieldRatio));
            }
        }
    }

    @Override
    protected void compute() {
        // if map is only a 4by4 square, compute
        if(mapLength == 4){
            computeDirectly();
            return;
        }

        // split quad-wise otherwise, split value is always and index in the right hand part of the array
        int split = mapLength / 2;

        invokeAll(
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, 0, 0, split-1, split-1),                 // left top
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, split, 0, mapLength-1, split-1),         // right top
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, split, split, mapLength-1, mapLength-1), // right bottom
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, 0, split, split-1, mapLength-1)          // left bottom
        );

    }


}

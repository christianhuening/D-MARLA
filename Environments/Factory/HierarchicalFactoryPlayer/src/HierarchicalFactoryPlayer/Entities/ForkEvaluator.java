package HierarchicalFactoryPlayer.Entities;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
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
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;


    public ForkEvaluator(TGameState gameState, Faction myFaction, QuadTree quadTree, int mapLength, int minX, int minY, int maxX, int maxY){
        this.gameState = gameState;
        this.myFaction = myFaction;
        this.quadTree = quadTree;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.mapLength = mapLength;
    }

    protected void computeDirectly(){
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
    }

    @Override
    protected void compute() {
        // if map is only a 4by4 square, compute
        if(mapLength < 4){
            try {
                throw new TechnicalException("Map is not a quadTreeStructure. Use other edgelength.");
            } catch (TechnicalException e) {
                e.printStackTrace();
            }
        }
        if(mapLength == 4){
            computeDirectly();
            return;
        }

        invokeAll(
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, minX, minY, maxX / 2, maxY / 2),                 // left top
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, maxX / 2 + 1, minY, maxX, maxY / 2),         // right top
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2,  maxX / 2 + 1, maxY / 2 + 1, maxX, maxY), // right bottom
                new ForkEvaluator(gameState, myFaction, quadTree, mapLength / 2, minX, maxY / 2 + 1, maxX / 2, maxY)          // left bottom
        );

        /*
        Fehler hier! Die rekursiven Aufrufe bearbeiten nur den oberen linken Quadranten. Liegt
        an mapLength-1 im 3. und 4. Aufruf, die kommen nicht mehr über 16 hinaus.
         */

    }


}

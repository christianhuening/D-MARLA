package HierarchicalFactoryPlayer.Entities;

import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.GameLogic.TransportTypes.TPosition;
import Factory.GameLogic.Utility.GameInfos;

import java.util.concurrent.RecursiveAction;

/**
 * Created by Chris on 16.06.2014.
 */
public class ForkEvaluator extends RecursiveAction {

    private final int mapLength;
    private final String[][] map;
    private TGameState gameState;
    private final int startX;
    private final int startY;
    private final int stopX;
    private final int stopY;


    public ForkEvaluator(TGameState gameState, String[][] map, int startX, int startY, int stopX, int stopY){
        this.gameState = gameState;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        mapLength = map.length;
        this.map = map;
    }

    protected void computeDirectly(){
        for (int x = startX; x <= stopX; x++) {
            for (int y = startY; y <= stopY; y++) {
                TAbstractField field = GameInfos.getFieldForPosition(gameState, new TPosition(x, y));

                // first value is ratio of influence fields / other fields
                // second value is ratio of own units / enemy units
                map[x][y] = "";
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
                new ForkEvaluator(gameState, map, 0, 0, split-1, split-1),                 // left top
                new ForkEvaluator(gameState, map, split, 0, mapLength-1, split-1),         // right top
                new ForkEvaluator(gameState, map, split, split, mapLength-1, mapLength-1), // right bottom
                new ForkEvaluator(gameState, map, 0, split, split-1, mapLength-1)          // left bottom
        );

    }
}

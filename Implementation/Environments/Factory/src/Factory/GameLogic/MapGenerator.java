package Factory.GameLogic;

import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.GameActors.*;
import Factory.GameLogic.TransportTypes.TPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 23.05.12
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class MapGenerator implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private AbstractField[][] board;
    private TMapMetaData metaData;
    private List<Factory> factories;
    private Random randomGenerator;

    private int upperMirrorEdge;
    private int lowerMirrorEdge;
    private int topBorder;
    private int leftBorder;
    private int bottomBorder;
    private int rightBorder;
    private List<Factory> blueSideFactories;
    private List<Factory> redSideFactories;
    private Factory redStartFactory;
    private Factory blueStartFactory;
    private int factoryID = 0;

    private TPosition factoryDirection;
    private TPosition factoryMirrorDirection;

// --------------------------- CONSTRUCTORS ---------------------------

    public MapGenerator(TMapMetaData metaData, List<Factory> factoryList) {
        this.metaData = metaData;
        this.factories = factoryList;
        randomGenerator = new Random(metaData.getSeed());
        this.board = new AbstractField[metaData.getEdgeLength()][metaData.getEdgeLength()];



        upperMirrorEdge = (metaData.getEdgeLength() / 2) - 1;
        lowerMirrorEdge = upperMirrorEdge + metaData.getEdgeLength() % 2;
        topBorder = 0;
        leftBorder = 0;
        bottomBorder = metaData.getEdgeLength() - 1;
        rightBorder = metaData.getEdgeLength() - 1;


        blueSideFactories = new ArrayList<Factory>();
        redSideFactories = new ArrayList<Factory>();
    }

// -------------------------- OTHER METHODS --------------------------

    public AbstractField[][] generateMap() {
        //1. initializing board
        board = new AbstractField[metaData.getEdgeLength()][metaData.getEdgeLength()];
        for (int i = 0; i < metaData.getEdgeLength(); i++) {
            for (int j = 0; j < metaData.getEdgeLength(); j++) {
                board[i][j] = new NormalField();
            }
        }

        //2. spawning Factories
        if (metaData.getSymmetry() == 0) {
            //horizontal Symmetry
            factoryDirection = new TPosition(1, 1);
            factoryMirrorDirection = new TPosition(1, -1);


            if (upperMirrorEdge == lowerMirrorEdge) {
                //horizontal symmetry wont spawn factories in the middle
                upperMirrorEdge -= 1;
                lowerMirrorEdge += 1;
            }

            //2.1. generate up to upperMirrorEdge
            //2.2. generate factory on Mirrored position


            for (int x = leftBorder; x <= rightBorder; x++) {
                for (int y = topBorder; y <= upperMirrorEdge; y++) {
                    if (randomGenerator.nextInt(999) + 1 <= metaData.getFactoryNumberFactor()) {
                        if (board[y][x] instanceof NormalField) {
                            generateFactories(new TPosition(x, y));
                        }
                    }
                }
            }
        } else {
            //TODO horizontal and vertical Symmetry
        }

        //3. Set Starting Factories and units


        if (blueSideFactories.isEmpty()) {
            blueStartFactory = spawnFactory(new TPosition(0, 0), 1, factoryDirection, blueSideFactories);
            redStartFactory = spawnFactory(new TPosition(leftBorder, bottomBorder), 1, factoryMirrorDirection, redSideFactories);
        } else {
            int theChosenOne = randomGenerator.nextInt(blueSideFactories.size());
            blueStartFactory = blueSideFactories.get(theChosenOne);
            redStartFactory = redSideFactories.get(theChosenOne);
        }

        blueStartFactory.setController(Faction.BLUE);
        redStartFactory.setController(Faction.RED);
        blueStartFactory.totalInfluence=blueStartFactory.size;
        redStartFactory.totalInfluence=-blueStartFactory.size;
        blueStartFactory.spawn();
        redStartFactory.spawn();

        factories.addAll(blueSideFactories);
        factories.addAll(redSideFactories);

        return board;
    }

    private void generateFactories(TPosition position) {
        int xFactoryCheck = position.getX();
        int yFactoryCheck = position.getY();

        // 1. calculate Size
        int actualFactorySize;
        for (actualFactorySize = metaData.getMaximumFactorySize(); actualFactorySize > 1; actualFactorySize--) {
            if (randomGenerator.nextInt(99) + 1 <= metaData.getFactorySizeFactor()) {
                break;
            }
        }

        // 2. reduce Size if there is a factory in the way

        //checking only on static symmetric size;
        // Improvement : Make a method that checks in direction

        //checking if there is a factory in the way ; -1 because of the influenceEdge
        //if there is not a NormalField actualFactorySize will be lowered
        //actualFactorySize will then serve as a Sentinel finding the smaller size from the direction x and y
        int x = xFactoryCheck;
        int y = yFactoryCheck;

        for (int i = -1; i <= actualFactorySize; i++) {
            if (!(board[y][x] instanceof NormalField)) {
                actualFactorySize = i;
            }
            y = yFactoryCheck;
            for (int j = -1; j <= actualFactorySize; j++) {
                if (!(board[y][x] instanceof NormalField)) {
                    actualFactorySize = j;
                }
                if (y == upperMirrorEdge) {
                    actualFactorySize = j;
                } else {
                    y += factoryDirection.getY();
                }
            }
            if (x == rightBorder) {
                actualFactorySize = i;
            } else {
                x += factoryDirection.getX();
            }
        }

        //3. spawning factories if they have a size

        if (actualFactorySize > 0) {
            spawnFactory(position, actualFactorySize, factoryDirection, blueSideFactories);
            spawnFactory(getHorizontalMirrorPosition(position), actualFactorySize, factoryMirrorDirection, redSideFactories);
        }
    }

    TPosition getHorizontalMirrorPosition(TPosition position) {
        int x = position.getX();
        int y = position.getY();
        return new TPosition(x, bottomBorder - y);
    }

    private Factory spawnFactory(TPosition position, int actualFactorySize, TPosition direction, List<Factory> factorySideList) {
        int factorySizeWithInfluenceFields = actualFactorySize + 2;
        int factoryX = position.getX();
        int factoryY = position.getY();

        List<InfluenceField> influenceFields = new ArrayList<InfluenceField>();
        List<FactoryField> factoryFields = new ArrayList<FactoryField>();


        //Iterate Size x Size times. factoryX and Y represent the position of the factory on the board
        //adding direction on in beforehand because of the influence edge
        //adding FactoryFields

        int x = factoryX;
        int y = factoryY;

        for (int i = 0; i < actualFactorySize; i++) {
            x += direction.getX();

            y = factoryY;
            for (int j = 0; j < actualFactorySize; j++) {
                y += direction.getY();

                FactoryField factoryField = new FactoryField(factoryID);
                board[y][x] = factoryField;
                factoryFields.add(factoryField);
            }
        }
        //adding InfluenceFields
        //this thing is not performance optimized.
        //Checks everything in factorySizeWithInfluenceFields and replace normal fields with influence fields
        x = factoryX;
        y = factoryY;

        for (int i = 0; i < factorySizeWithInfluenceFields; i++) {
            y = factoryY;
            for (int j = 0; j < factorySizeWithInfluenceFields; j++) {
                if (board[y][x] instanceof NormalField) {
                    InfluenceField influenceField = new InfluenceField(factoryID);
                    board[y][x] = influenceField;
                    influenceFields.add(influenceField);
                }
                y += direction.getY();
            }
            x += direction.getX();
        }

        Factory factory = new Factory(actualFactorySize, factoryFields, influenceFields, factoryID);
        factorySideList.add(factory);

        factoryID++;
        return factory;
    }

    TPosition getDiagonalMirrorPosition(TPosition position) {
        int x = position.getX();
        int y = position.getY();
        return new TPosition(rightBorder - x, bottomBorder - y);
    }
}

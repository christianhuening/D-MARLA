package HierarchicalFactoryPlayer;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;
import Factory.GameLogic.Utility.GameInfos;
import HierarchicalFactoryPlayer.Entities.EvaluatorGameState;
import HierarchicalFactoryPlayer.Entities.RawField;
import HierarchicalFactoryPlayer.Entities.RawState;
import HierarchicalFactoryPlayer.Enums.FieldType;
import HierarchicalFactoryPlayer.Enums.FriendFoe;
import HierarchicalFactoryPlayer.Exceptions.MapIsNoSquareException;
import HierarchicalFactoryPlayer.StateActionGenerators.EvaluatorStateActionGenerator;
import HierarchicalFactoryPlayer.StateActionGenerators.MoverStateActionGenerator;
import java.util.ArrayList;
import java.util.List;


public class HierarchicalFactoryPlayerSystem implements IAgentSystem<IEnvironmentConfiguration, TGameState, TActionsInTurn> {
    private final IAgent mover;
    private final EvaluatorGameState evaluatorGameState;
    private final int BasicTurnReward;
    private final int KillEnemyReward;
    private final int MoveToAnotherFieldReward;
    private final int GainInfluenceReward;
    private final MoverStateActionGenerator moverStateActionGenerator;
    private final IAgent evaluator;
    private List<IAgent> agentlist;
    private Faction myFaction;
    private Faction enemyFaction;
    private boolean firstturn;
    private float rewardForLastTurn;


    public HierarchicalFactoryPlayerSystem(IPluginServiceProvider provider) throws TechnicalException {
        agentlist = new ArrayList<>();

        evaluator = provider.getTableAgent("Evaluator", LearningAlgorithm.SARSALambda, new EvaluatorStateActionGenerator());
        evaluator.setAlpha(Float.parseFloat(provider.getAgentSystemSetting("Alpha")));
        evaluator.setEpsilon(Float.parseFloat(provider.getAgentSystemSetting("Epsilon")));
        evaluator.setGamma(Float.parseFloat(provider.getAgentSystemSetting("Gamma")));
        evaluator.setLambda(Float.parseFloat(provider.getAgentSystemSetting("Lambda")));
        agentlist.add(evaluator);

        mover = provider.getTableAgent("Mover", LearningAlgorithm.SARSALambda, new MoverStateActionGenerator());
        mover.setAlpha(Float.parseFloat(provider.getAgentSystemSetting("Alpha")));
        mover.setEpsilon(Float.parseFloat(provider.getAgentSystemSetting("Epsilon")));
        mover.setGamma(Float.parseFloat(provider.getAgentSystemSetting("Gamma")));
        mover.setLambda(Float.parseFloat(provider.getAgentSystemSetting("Lambda")));
        agentlist.add(mover);

        evaluatorGameState = new EvaluatorGameState(evaluator);
        moverStateActionGenerator = new MoverStateActionGenerator();

        BasicTurnReward = Integer.parseInt(provider.getAgentSystemSetting("BasicTurnReward"));
        KillEnemyReward = Integer.parseInt(provider.getAgentSystemSetting("KillEnemyReward"));
        MoveToAnotherFieldReward = Integer.parseInt(provider.getAgentSystemSetting("MoveToAnotherFieldReward"));
        GainInfluenceReward = Integer.parseInt(provider.getAgentSystemSetting("GainInfluenceReward"));
    }

    @Override
    public void start(IEnvironmentConfiguration metaData) throws TechnicalException {
        myFaction = Faction.valueOf(metaData.toString());
        if (myFaction == Faction.RED) {
            enemyFaction = Faction.BLUE;
        } else {
            enemyFaction = Faction.RED;
        }
        firstturn = true;
    }

    @Override
    public TActionsInTurn getActionsForEnvironmentStatus(TGameState currentGameState) throws TechnicalException {
        List<TUnit> myUnits = GameInfos.getUnitsForFaction(currentGameState, myFaction);

        // the action list holding all actions for the next step
        List<TAction> actionList = new ArrayList<TAction>();

        // Now use this evaluated game state
        for (TUnit unit : myUnits) {

            TPosition unitPosition = GameInfos.getPositionForUnit(currentGameState, unit);
            TAbstractField field = GameInfos.getFieldForPosition(currentGameState, unitPosition);


            // First evaluate the current game state
            try {
                evaluatorGameState.evaluateNewGameState(currentGameState, myFaction, rewardForLastTurn);
            } catch (MapIsNoSquareException e) {
                e.printStackTrace();
                throw new TechnicalException(e.getMessage());
            }

            // get evaluation by field and unit
            RawState rawState = evaluatorGameState.getEvaluation(currentGameState, unit);

            StateAction action;

            // encrypt rawState to String encryption
            String encryptedState = moverStateActionGenerator.encryptState(rawState);

            // start Episode or compute next step depending on where in the simulation we are
            if (firstturn) {
                action = mover.startEpisode(new StateAction(encryptedState));
                firstturn = false;
            } else {
                action = mover.step(rewardForLastTurn, new StateAction(encryptedState));
            }


            Direction directionOfAction = moverStateActionGenerator.decryptDirection(action.getActionDescription());

            TAction moveOrder = new TAction(unit, directionOfAction);
            if (directionOfAction != null) {
                actionList.add(moveOrder);
            }

            rewardForLastTurn = calculateReward(currentGameState, moveOrder, unitPosition, field);

            currentGameState = refreshGameState(currentGameState, moveOrder, unitPosition);
        }

        return new TActionsInTurn(actionList);
    }

    @Override
    public void end() throws TechnicalException {

    }


    public List<IAgent> getInternalAgents() {
        return this.agentlist;
    }

// -------------------------- PRIVATE METHODS --------------------------

    /**
     * Calculates the reward for the current StateAction pair. Uses values from the settings.properties file colocated
     * with the plugin jar.
     * Basically the rewards are given for killing enemies, moving to another field and/or moving to an influence field.
     * @param tGameState The current new game state
     * @param moveOrder The action leading to this gameState
     * @param position the current position of the unit
     * @param fieldComeFrom the last field the unit was on
     * @return The calculated reward as a float
     */
    private float calculateReward(TGameState tGameState, TAction moveOrder, TPosition position, TAbstractField fieldComeFrom) {
        float reward = BasicTurnReward; //-10;
        TUnit unit = moveOrder.getUnit();
        TAbstractField fieldMoveTo = null;
        if (moveOrder.getDirection() == null) {
            fieldMoveTo = fieldComeFrom;
        } else {
            fieldMoveTo = GameInfos.getNeighborFieldForPosition(tGameState, position, moveOrder.getDirection());
            if (fieldMoveTo.isOccupied() && !fieldMoveTo.getOccupant().getControllingFaction().equals(myFaction)) {
                reward += KillEnemyReward; //5;
            }
            if (!fieldMoveTo.equals(fieldComeFrom)) {
                reward += MoveToAnotherFieldReward; //1;
            }
        }

        if (fieldMoveTo instanceof TInfluenceField) {
            TFactory factory = getFactoryByID(tGameState, ((TInfluenceField) fieldMoveTo).getFactoryID());
            if (factory.getOwningFaction() != myFaction || factory.getOwningFaction() == Faction.NEUTRAL) {
                reward += GainInfluenceReward; //3;
            }
        }

        return reward;
    }

    private TFactory getFactoryByID(TGameState gameState, int ID) {
        for (TFactory factory : gameState.getFactories()) {
            if (factory.getFactoryID() == ID) {
                return factory;
            }
        }
        return null;
    }

    private RawState generateRawState(TGameState gameState, TAbstractField field, TPosition positionOfField) {
        RawField rawField;
        RawState rawState = new RawState();
        RawField wallField = new RawField();
        wallField.setFieldController(FriendFoe.FRIEND);
        wallField.setFieldType(FieldType.FACTORY);
        wallField.setRemainingTimeToSpawn(100);
        wallField.setUnit(FriendFoe.NONE);
        int border = gameState.getMapFields().length - 1;


        rawField = generateRawField(gameState, field);
        rawState.setMiddle(rawField);

        if (positionOfField.getX() > 0) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.LEFT));
            rawState.setLeft(rawField);
        } else {
            rawState.setLeft(wallField);
        }

        if (positionOfField.getX() < border) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.RIGHT));
            rawState.setRight(rawField);
        } else {
            rawState.setRight(wallField);
        }

        if (positionOfField.getY() < border) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.DOWN));
            rawState.setDown(rawField);
        } else {
            rawState.setDown(wallField);
        }

        if (positionOfField.getY() < border && positionOfField.getX() > 0) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.DOWN_LEFT));
            rawState.setLeftDown(rawField);
        } else {
            rawState.setLeftDown(wallField);
        }

        if (positionOfField.getY() < border && positionOfField.getX() < border) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.DOWN_RIGHT));
            rawState.setRightDown(rawField);
        } else {
            rawState.setRightDown(wallField);
        }

        if (positionOfField.getY() > 0 && positionOfField.getX() > 0) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.UP_LEFT));
            rawState.setLeftTop(rawField);
        } else {
            rawState.setLeftTop(wallField);
        }

        if (positionOfField.getY() > 0 && positionOfField.getX() < border) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.UP_RIGHT));
            rawState.setRightTop(rawField);
        } else {
            rawState.setRightTop(wallField);
        }

        if (positionOfField.getY() > 0) {
            rawField = generateRawField(gameState, GameInfos.getNeighborFieldForPosition(gameState, positionOfField, Direction.UP));
            rawState.setTop(rawField);
        } else {
            rawState.setTop(wallField);
        }

        Direction direction = findNextTarget(gameState, field, positionOfField);

        rawState.setSignal(direction);


        return rawState;
    }

    private RawField generateRawField(TGameState gameState, TAbstractField field) {
        RawField rawField = new RawField();
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

            TFactory factory = getFactoryByID(gameState, ID);
            rawField.setRemainingTimeToSpawn(factory.getRemainingRoundsForRespawn());
            if (factory.getOwningFaction() == myFaction) {
                rawField.setFieldController(FriendFoe.FRIEND);
            } else {
                rawField.setFieldController(FriendFoe.FOE);
            }
        }
        return rawField;
    }


    /**
     * Finds next target around the current position
     * @param gameState
     * @param field
     * @param positionOfField
     * @return The Direction in which the next target resides
     */
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

    /**
     * Refreshes the game state with the last State-Action combination
     * @param gameState
     * @param action
     * @param unitPosition
     * @return The updated GameState
     */
    private TGameState refreshGameState(TGameState gameState, TAction action, TPosition unitPosition) {
        //================= replacing the field the unit came from ======================

        TAbstractField[][] board = gameState.getMapFields();
        TAbstractField from = board[unitPosition.getY()][unitPosition.getX()];
        if (board[unitPosition.getY()][unitPosition.getX()] instanceof TNormalField) {
            board[unitPosition.getY()][unitPosition.getX()] = new TNormalField(null);
        } else if (board[unitPosition.getY()][unitPosition.getX()] instanceof TInfluenceField) {
            TInfluenceField fromINF = (TInfluenceField) from;
            board[unitPosition.getY()][unitPosition.getX()] = new TInfluenceField(null, fromINF.getFactoryID());
        } else if (board[unitPosition.getY()][unitPosition.getX()] instanceof TFactoryField) {
            TFactoryField fromFAC = (TFactoryField) from;
            board[unitPosition.getY()][unitPosition.getX()] = new TFactoryField(null, fromFAC.getFactoryID());
        }

        //====================== replacing the field the unit is going too ============================
        // ------------------ getting x y of the field the unit is going to -----------
        int x = unitPosition.getX();
        int y = unitPosition.getY();

        if (action.getDirection() == Direction.RIGHT || action.getDirection() == Direction.UP_RIGHT || action.getDirection() == Direction.DOWN_RIGHT) {
            x++;
        }
        if (action.getDirection() == Direction.LEFT || action.getDirection() == Direction.UP_LEFT || action.getDirection() == Direction.DOWN_LEFT) {
            x--;
        }

        if (action.getDirection() == Direction.DOWN || action.getDirection() == Direction.DOWN_LEFT || action.getDirection() == Direction.DOWN_RIGHT) {
            y++;
        }
        if (action.getDirection() == Direction.UP || action.getDirection() == Direction.UP_LEFT || action.getDirection() == Direction.UP_RIGHT) {
            y--;
        }

        // -------------------------- actual replace ---------------------------
        TAbstractField to = board[y][x];
        TUnit oldUnit = action.getUnit();
        TUnit newUnit = new TUnit(oldUnit.getUnitId(), oldUnit.getControllingFaction(), gameState.getTurn());
        if (board[y][x] instanceof TNormalField) {
            board[y][x] = new TNormalField(newUnit);
        } else if (board[y][x] instanceof TInfluenceField) {
            TInfluenceField fromINF = (TInfluenceField) to;
            board[y][x] = new TInfluenceField(newUnit, fromINF.getFactoryID());
        } else if (board[y][x] instanceof TFactoryField) {
            TFactoryField fromFAC = (TFactoryField) to;
            board[y][x] = new TInfluenceField(newUnit, fromFAC.getFactoryID());
        }

        //================== replacing the board ============================

        TGameState newState = new TGameState(gameState.hasClientMetGoal(), gameState.getActivePlayer()
                , gameState.getTurn(), gameState.getRound(), gameState.getGameStartedAt(), gameState.getFactories(), board);


        return newState;
    }

}

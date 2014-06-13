package SimpleFactoryPlayer.Implementation;

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
import SimpleFactoryPlayer.Implementation.Entities.RawField;
import SimpleFactoryPlayer.Implementation.Entities.RawState;
import SimpleFactoryPlayer.Implementation.Enums.FieldType;
import SimpleFactoryPlayer.Implementation.Enums.FriendFoe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 06.06.12
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFactoryPlayerVolume2System implements IAgentSystem<IEnvironmentConfiguration, TGameState, TActionsInTurn> {
// ------------------------------ FIELDS ------------------------------

    private IPluginServiceProvider provider;
    private IAgent agent;
    private List<IAgent> agentList = new ArrayList<IAgent>();
    private Faction myFaction;
    private Faction enemyFaction;
    private StateActionGenerator stateActionGenerator = new StateActionGenerator();
    private float rewardForLastTurn = 0;
    private boolean firstturn = false;

    // REWARDS
    private int BasicTurnReward;
    private int KillEnemyReward;
    private int MoveToAnotherFieldReward;
    private int GainInfluenceReward;

// --------------------------- CONSTRUCTORS ---------------------------

    public SimpleFactoryPlayerVolume2System(IPluginServiceProvider provider) throws TechnicalException {
        this.provider = provider;

        BasicTurnReward = Integer.parseInt(provider.getAgentSystemSetting("BasicTurnReward"));
        KillEnemyReward = Integer.parseInt(provider.getAgentSystemSetting("KillEnemyReward"));
        MoveToAnotherFieldReward = Integer.parseInt(provider.getAgentSystemSetting("MoveToAnotherFieldReward"));
        GainInfluenceReward = Integer.parseInt(provider.getAgentSystemSetting("GainInfluenceReward"));

        agent = provider.getTableAgent("SimpleFactoryUnitController", LearningAlgorithm.QLearning, new StateActionGenerator());
        agentList.add(agent);
        agent.setAlpha(Float.parseFloat(provider.getAgentSystemSetting("Alpha")));
        agent.setEpsilon(Float.parseFloat(provider.getAgentSystemSetting("Epsilon")));
        agent.setGamma(Float.parseFloat(provider.getAgentSystemSetting("Gamma")));
        agent.setLambda(Float.parseFloat(provider.getAgentSystemSetting("Lambda")));
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IAgentSystem ---------------------


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
    public TActionsInTurn getActionsForEnvironmentStatus(TGameState tGameState) throws TechnicalException {
        TGameState currentGameState = tGameState;
        List<TUnit> myUnits = GameInfos.getUnitsForFaction(currentGameState, myFaction);
        List<TAction> actionList = new ArrayList<TAction>();


        for (TUnit unit : myUnits) {
            RawState rawState;
            // Randomizen wäre besser

            TPosition unitPosition = GameInfos.getPositionForUnit(currentGameState, unit);
            TAbstractField field = GameInfos.getFieldForPosition(currentGameState, unitPosition);
            rawState = generateRawState(currentGameState, field, unitPosition);
            StateAction action = null;
            String encryptedState = stateActionGenerator.encryptState(rawState);

            if (firstturn) {
                action = agent.startEpisode(new StateAction(encryptedState));
                firstturn = false;
            } else {
                action = agent.step(rewardForLastTurn, new StateAction(encryptedState));
            }

            Direction directionOfAction = stateActionGenerator.decryptDirection(action.getActionDescription());


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
        agent.endEpisode(new StateAction(""), rewardForLastTurn);
    }


    public List<IAgent> getInternalAgents() {
        return agentList;
    }

// -------------------------- PRIVATE METHODS --------------------------

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
            TFactory factory = getFactoryForID(tGameState, ((TInfluenceField) fieldMoveTo).getFactoryID());
            if (factory.getOwningFaction() != myFaction || factory.getOwningFaction() == Faction.NEUTRAL) {
                reward += GainInfluenceReward; //3;
            }
        }

        return reward;
    }

    private TFactory getFactoryForID(TGameState gameState, int ID) {
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

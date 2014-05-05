package Factory.GameLogic;


import EnvironmentPluginAPI.Exceptions.IllegalActionException;
import EnvironmentPluginAPI.Exceptions.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Exceptions.NoUnitFoundException;
import Factory.GameLogic.GameActors.AbstractField;
import Factory.GameLogic.GameActors.Factory;
import Factory.GameLogic.GameActors.Player;
import Factory.GameLogic.GameActors.Unit;
import Factory.GameLogic.TransportTypes.*;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */

public class Game extends Observable implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private Player activePlayer;

    public Player getActivePlayer() {
        return activePlayer;
    }

    private Player winningPlayer = null;

    public Player getWinningPlayer() {
        return winningPlayer;
    }

    private DateTime gameStartedAt;

    private List<Factory> factories = new ArrayList<Factory>();

    public List<Factory> getFactories() {
        return factories;
    }

    private AbstractField[][] board;

    public AbstractField[][] getBoard() {
        return board;
    }

    private RuleBook ruleBook = new RuleBook();

    private List<Player> players;

    private TMapMetaData mapMetaData;

    public TMapMetaData getMapMetaData() {
        return mapMetaData;
    }

    private int turn = 1;

    public int getTurn() {
        return turn;
    }

    private int round = 1;

    public int getRound() {
        return round;
    }

    private MapGenerator mapGenerator;

    private List<TGameState> turns;

// --------------------------- CONSTRUCTORS ---------------------------

    public Game(TMapMetaData mapMetaData, List<Player> players) throws IllegalNumberOfClientsException {
        if (players.size() < 2) {
            throw new IllegalNumberOfClientsException("There were less than 2 players in the list. Please choose exactly two.");
        } else if(players.size() > 2) {
            throw new IllegalNumberOfClientsException("There were more than 2 players in the list. Please choose exactly two.");
        }

        Random rand = new Random();

        this.players = players;
        this.mapMetaData = mapMetaData;
        mapGenerator = new MapGenerator(mapMetaData, factories);
        activePlayer = players.get(rand.nextInt(players.size()));
        this.board = mapGenerator.generateMap();
        gameStartedAt = new DateTime();

        this.turns = new LinkedList<TGameState>();

        for (Factory factory : factories) {
            this.addObserver(factory);
        }
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        String display = "";
        String endOfLine = "\n";

        for (int i = 0; i < mapMetaData.getEdgeLength(); i++) {
            for (int j = 0; j < mapMetaData.getEdgeLength(); j++) {
                display += board[i][j].toString();
            }
            display += endOfLine;
        }


        return display;
    }

// -------------------------- PUBLIC METHODS --------------------------

    public void executeActionList(TActionsInTurn actionsInTurn) throws TechnicalException {
        for (TAction action : actionsInTurn) {
            moveUnit(action.getUnit(), action.getDirection());
        }

        endTurn();

        //System.out.println(this.toString());
    }

    public boolean moveUnit(TUnit tUnit, Direction direction) {
        Unit unit = getUnitForID(tUnit.getUnitId());

        TPosition unitPosition;
        if (ruleBook.validateMoveUnit(activePlayer.getFaction(), unit, direction, this)) {
            unitPosition = getPositionForUnit(unit);
            board[unitPosition.getY()][unitPosition.getX()].removeOccupant();
            getNeighborFieldForPosition(unitPosition, direction).setOccupant(unit);
            unit.calculateExhaustion(turn);
            return true;
        } else {
            return false;
        }
    }

    Unit getUnitForID(UUID ID) {
        for (AbstractField[] fields : board) {
            for (AbstractField field : fields) {
                if (field.isOccupied()) {
                    if (field.getOccupant().getUnitID().equals(ID)) {
                        return field.getOccupant();
                    }
                }
            }
        }

        //no unit found!
        throw new NoUnitFoundException("No unit with the given GUID: " + ID.toString() + " was found :(.");
    }

    public TPosition getPositionForUnit(Unit unit) {
        if (unit == null) {
            throw new IllegalActionException("The given unit was null.");
        }

        for (int x = 0; x < mapMetaData.getEdgeLength(); x++) {
            for (int y = 0; y < mapMetaData.getEdgeLength(); y++) {
                if (board[y][x].isOccupied()) {
                    if (board[y][x].getOccupant().getUnitID().equals(unit.getUnitID())) {
                        return new TPosition(x, y);
                    }
                }
            }
        }
        //No unit was found Something bad will happen
        return null;
    }

    public void endTurn() throws TechnicalException {
        if (ruleBook.validateEndTurn(this)) {
            turn += 1;
            roundUpdate();
            if (winningPlayer == null) {
                winningPlayer = ruleBook.validateWinner(this);
            }


            List<TFactory> tFactories = new ArrayList<TFactory>();
            TAbstractField[][] tAbstractFields = new TAbstractField[board.length][board[0].length];

            for (Factory f : factories) {
                tFactories.add(f.getTransportType());
            }

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    tAbstractFields[i][j] = board[i][j].getTransportType();
                }
            }

            boolean playerWon = false;

            if (winningPlayer != null) {
                playerWon = true;
            }

            turns.add(new TGameState(playerWon, activePlayer.getTransportType(), turn, round, gameStartedAt, tFactories, tAbstractFields));

            setChanged();
            notifyObservers(EventTyp.END_TURN);
            switchActivePlayer();
        } else {
            throw new IllegalActionException("rules of factory broken!");
        }
    }

    private void roundUpdate() {
        int isNewRound = turn % 2;
        round += isNewRound;
        if (isNewRound == 1) {
            setChanged();
            notifyObservers(EventTyp.END_ROUND);
        }
    }

    private void switchActivePlayer() {
        if (activePlayer == players.get(0))
            activePlayer = players.get(1);
        else
            activePlayer = players.get(0);
    }

    public boolean gameFinished() {
        return winningPlayer != null;
    }

    public TGameState getCurrentGameState() throws TechnicalException {
        List<TFactory> tFactories = new ArrayList<TFactory>();
        for (Factory factory : factories) {
            tFactories.add(factory.getTransportType());
        }
        tFactories = Collections.unmodifiableList(tFactories);


        TAbstractField[][] tBoard = new TAbstractField[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                tBoard[i][j] = board[i][j].getTransportType();
            }
        }

        boolean playerwon = false;

        if (winningPlayer != null) {
            playerwon = true;
        }

        return new TGameState(playerwon, activePlayer.getTransportType(), turn, round, gameStartedAt, tFactories, tBoard);
    }

    public AbstractField getFieldForPosition(TPosition position) {
        return board[position.getY()][position.getX()];
    }

    public AbstractField getNeighborFieldForPosition(TPosition position, Direction direction) {
        int x = position.getX();
        int y = position.getY();
        switch (direction) {
            case UP:
                return board[y - 1][x];
            case DOWN:
                return board[y + 1][x];
            case LEFT:
                return board[y][x - 1];
            case RIGHT:
                return board[y][x + 1];
            case UP_LEFT:
                return board[y - 1][x - 1];
            case UP_RIGHT:
                return board[y - 1][x + 1];
            case DOWN_LEFT:
                return board[y + 1][x - 1];
            case DOWN_RIGHT:
                return board[y + 1][x + 1];
        }
        return null;
    }

    public TGameReplay getReplay() {
        List<String> playerNames = new ArrayList<String>();

        for (Player p : this.players) {
            playerNames.add(p.getName());
        }

        return new TGameReplay(UUID.randomUUID(), this.gameStartedAt, playerNames, this.winningPlayer.getName(), this.turn, this.turns);
    }
}

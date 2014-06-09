package Factory.GameLogic.TransportTypes;

import EnvironmentPluginAPI.Contract.IEnvironmentState;
import Factory.Interfaces.IHasConsistencyCheck;

import java.util.Date;
import java.util.List;

/**
 * TransportType for the GameState, which is retrieved from the GameServer
 */
public class TGameState implements IEnvironmentState, IHasConsistencyCheck, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private TPlayer activePlayer;

    /**
     * Denotes if the current environment is still active. If true, the MARLA system will use the value of
     * getActiveInstance() to ask that client for his next turn. If false, the MARLA system will stop letting the
     * clients make turns and finish this environment.
     *
     * @return see description
     */
    public TPlayer getActivePlayer() {
        return activePlayer;
    }

    private int turn;

    public int getTurn() {
        return turn;
    }

    private int round;

    public int getRound() {
        return round;
    }

    private Date gameStartedAt;

    public Date getGameStartedAt() {
        return gameStartedAt;
    }

    private List<TFactory> factories;

    public List<TFactory> getFactories() {
        return factories;
    }

    private TAbstractField[][] mapFields;

    public TAbstractField[][] getMapFields() {
        return mapFields;
    }

    private boolean clientMetGoal;

    public boolean hasClientMetGoal() {
        return clientMetGoal;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TGameState(boolean clientMetGoal, TPlayer activePlayer, int turn, int round, Date gameStartedAt, List<TFactory> factories, TAbstractField[][] mapFields) {
        this.activePlayer = activePlayer;
        this.turn = turn;
        this.round = round;
        this.gameStartedAt = gameStartedAt;
        this.factories = factories;
        this.mapFields = mapFields;
        this.clientMetGoal = clientMetGoal;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TGameState)) return false;

        TGameState that = (TGameState) o;

        if (clientMetGoal != that.clientMetGoal) return false;
        if (round != that.round) return false;
        if (turn != that.turn) return false;
        if (!activePlayer.equals(that.activePlayer)) return false;
        if (!factories.equals(that.factories)) return false;
        if (!gameStartedAt.equals(that.gameStartedAt)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = activePlayer.hashCode();
        result = 31 * result + turn;
        result = 31 * result + round;
        result = 31 * result + gameStartedAt.hashCode();
        result = 31 * result + factories.hashCode();
        result = 31 * result + (clientMetGoal ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String display = "";
        String endOfLine = "\n";

        for (int i = 0; i < getMapFields().length; i++) {
            for (int j = 0; j < getMapFields()[0].length; j++) {
                display += getMapFields()[i][j].toString();
            }
            display += endOfLine;
        }


        return display;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------

    @Override
    public boolean isConsistent() {
        if (activePlayer == null || turn < 0 || round < 0 || gameStartedAt == null || factories == null || mapFields == null) {
            return false;
        }

        for (TFactory factory : factories) {
            if (!factory.isConsistent()) {
                return false;
            }
        }

        if (mapFields.length < 6 || mapFields[0].length < 6) {
            return false;
        }

        for (int i = 0; i < mapFields.length; i++) {
            for (int j = 0; j < mapFields[0].length; j++) {
                if (!mapFields[i][j].isConsistent()) {
                    return false;
                }
            }
        }

        return true;
    }
}
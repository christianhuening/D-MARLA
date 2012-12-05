package ZeroTypes.TransportTypes;

import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * TransportType for a played game.<br/>
 * <p/>
 * Holds information about when a game was played, who won it, and which clients participated.
 */
public class TCycleReplayDescription implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private Date replayDate;

    public Date getReplayDate() {
        return replayDate;
    }

    private UUID replayID;

    public UUID getReplayID() {
        return replayID;
    }

    private List<String> clients;

    public List<String> getClients() {
        return clients;
    }

    private String winningClient;

    public String getWinningClient() {
        return winningClient;
    }

    private int numberOfTurns;

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    private TEnvironmentDescription environmentDescription;

    public TEnvironmentDescription getEnvironmentDescription() {
        return environmentDescription;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TCycleReplayDescription(UUID gameReplayId, Date replayDate, List<String> clients, String winningClient, int numberOfTurns) {
        this(gameReplayId, replayDate, clients, winningClient, numberOfTurns, null);
    }

    public TCycleReplayDescription(UUID gameReplayId, Date replayDate, List<String> clients, String winningClient, int numberOfTurns, TEnvironmentDescription environmentDescription) {
        this.replayID = gameReplayId;
        this.replayDate = replayDate;
        this.clients = clients;
        this.winningClient = winningClient;
        this.numberOfTurns = numberOfTurns;
        this.environmentDescription = environmentDescription;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCycleReplayDescription)) return false;

        TCycleReplayDescription that = (TCycleReplayDescription) o;

        if (numberOfTurns != that.numberOfTurns) return false;
        if (clients != null ? !clients.equals(that.clients) : that.clients != null) return false;
        if (replayDate != null ? !replayDate.equals(that.replayDate) : that.replayDate != null) return false;
        if (replayID != null ? !replayID.equals(that.replayID) : that.replayID != null) return false;
        if (winningClient != null ? !winningClient.equals(that.winningClient) : that.winningClient != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = replayDate != null ? replayDate.hashCode() : 0;
        result = 31 * result + (replayID != null ? replayID.hashCode() : 0);
        result = 31 * result + (clients != null ? clients.hashCode() : 0);
        result = 31 * result + (winningClient != null ? winningClient.hashCode() : 0);
        result = 31 * result + numberOfTurns;
        return result;
    }
}

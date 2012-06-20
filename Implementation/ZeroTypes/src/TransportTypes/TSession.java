package TransportTypes;

import Enumeration.SessionStatus;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;

import java.util.List;
import java.util.UUID;

public class TSession implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private UUID id;

    private String name;

    public String getName() {
        return name;
    }

    private SessionStatus status;

    public SessionStatus getStatus() {
        return status;
    }

    private int playerCount;

    public int getPlayerCount() {
        return playerCount;
    }

    private int numberOfGames;

    public int getNumberOfGames() {
        return numberOfGames;
    }

    private List<TNetworkClient> clientsInThisSession;

    public List<TNetworkClient> getClientsInThisSession() {
        return clientsInThisSession;
    }

    private TMapMetaData mapMetaData;

    public TMapMetaData getMapMetaData() {
        return mapMetaData;
    }
    
    private TEnvironmentDescription environmentDescription;

    public TEnvironmentDescription getEnvironmentDescription() {
        return environmentDescription;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TSession(UUID id, String name, SessionStatus status, int playerCount, int numberOfGames, List<TNetworkClient> clientsInThisSession, TMapMetaData mapMetaData, TEnvironmentDescription environmentDescription) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.playerCount = playerCount;
        this.numberOfGames = numberOfGames;
        this.clientsInThisSession = clientsInThisSession;
        this.mapMetaData = mapMetaData;
        this.environmentDescription = environmentDescription;
    }
}

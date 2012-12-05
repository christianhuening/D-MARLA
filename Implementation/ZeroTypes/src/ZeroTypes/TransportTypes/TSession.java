package ZeroTypes.TransportTypes;

import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import ZeroTypes.Enumerations.SessionStatus;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

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

    private final IEnvironmentConfiguration configuration;

    public IEnvironmentConfiguration getConfiguration() {
        return configuration;
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
    
    private TEnvironmentDescription environmentDescription;

    public TEnvironmentDescription getEnvironmentDescription() {
        return environmentDescription;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TSession(UUID id, String name, SessionStatus status, IEnvironmentConfiguration configuration, int playerCount, int numberOfGames, List<TNetworkClient> clientsInThisSession, TEnvironmentDescription environmentDescription) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.configuration = configuration;
        this.playerCount = playerCount;
        this.numberOfGames = numberOfGames;
        this.clientsInThisSession = clientsInThisSession;
        this.environmentDescription = environmentDescription;
    }
}

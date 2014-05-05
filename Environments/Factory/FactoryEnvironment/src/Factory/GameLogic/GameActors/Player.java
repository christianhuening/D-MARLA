package Factory.GameLogic.GameActors;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.TPlayer;
import Factory.Interfaces.IHasTransportType;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 22.05.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */

public class Player implements IHasTransportType<TPlayer>, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private TMARLAClientInstance clientInstance;

    private Faction faction;

    public Faction getFaction() {
        return faction;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public Player(TMARLAClientInstance clientInstance, Faction faction) {
        this.faction = faction;
        this.clientInstance = clientInstance;
    }

    public String getName() {
        return clientInstance.getName();
    }

    @Override
    public TPlayer getTransportType() throws TechnicalException {
        return new TPlayer(clientInstance.getName(), faction);
    }

    public TMARLAClientInstance getClientInstance() {
        return clientInstance;
    }
}

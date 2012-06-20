package Factory.GameLogic.TransportTypes;

import Factory.GameLogic.Enums.Faction;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 15.06.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class TPlayer implements Serializable {
    private final String name;
    private final Faction faction;

    public TPlayer(String name, Faction faction) {
        this.name = name;
        this.faction = faction;
    }

    public String getName() {
        return name;
    }

    public Faction getFaction() {
        return faction;
    }
}

package Factory.GameLogic.GameActors;

import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.EventTyp;
import Factory.GameLogic.TransportTypes.TFactory;
import Factory.Interfaces.IHasTransportType;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */

public class Factory implements Observer, IHasTransportType<TFactory>, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    public int totalInfluence = 0;

    public int size;

    public Faction controller = Faction.NEUTRAL;

    public Faction getController() {
        return controller;
    }

    public void setController(Faction controller) {
        this.controller = controller;
    }

    public int roundsToSpawnNeeded = 5;

    public int remainingRoundsToSpawn = roundsToSpawnNeeded;

    public int factoryID;


    public List<FactoryField> factoryFields;


    public List<InfluenceField> influenceFields;

// --------------------------- CONSTRUCTORS ---------------------------

    public Factory(int size, List<FactoryField> factoryFields, List<InfluenceField> influenceFields, int factoryID) {
        this.size = size;
        this.factoryFields = factoryFields;
        this.influenceFields = influenceFields;
        this.factoryID = factoryID;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType -----------------


    @Override
    public TFactory getTransportType() {
        return new TFactory(remainingRoundsToSpawn, totalInfluence, controller, factoryID);
    }

// --------------------- Interface Observer ---------------------

    @Override
    public void update(Observable o, Object arg) {
        EventTyp event = (EventTyp) arg;

        if (event == EventTyp.END_ROUND) {
            calculateNewTotalInfluence();
            calculateController();
            calculateSpawn();
        }
    }

    private void calculateController() {
        if (controller == Faction.NEUTRAL) {
            if (totalInfluence == size) {
                controller = Faction.BLUE;
            } else if (totalInfluence == -size) {
                controller = Faction.RED;
            }
        } else if (Math.abs(totalInfluence) < size / 2) {
            controller = Faction.NEUTRAL;
            remainingRoundsToSpawn = roundsToSpawnNeeded;
        } else if (size == 1) {
            if (totalInfluence == size) {
                controller = Faction.BLUE;
            } else {
                controller = Faction.RED;
            }
        }
    }

    private void calculateNewTotalInfluence() {
        for (InfluenceField field : influenceFields) {
            totalInfluence += field.getInfluence();
            if (totalInfluence < -size) {
                totalInfluence = -size;
                break;
            }
            if (totalInfluence > size) {
                totalInfluence = size;
                break;
            }
        }
    }

    private void calculateSpawn() {
        if (controller == Faction.NEUTRAL)
            return;

        remainingRoundsToSpawn--;
        if (remainingRoundsToSpawn <= 0) {
            remainingRoundsToSpawn = roundsToSpawnNeeded;
            spawn();
        }
    }

    public void spawn() {
        for (FactoryField field : factoryFields) {
            field.setOccupant(new Unit(this.getController()));
        }
    }
}
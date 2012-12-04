package Factory.GameLogic.TransportTypes;

import EnvironmentPluginAPI.Contract.IActionDescription;
import Factory.Interfaces.IHasConsistencyCheck;

import java.util.Iterator;
import java.util.List;

/**
 * This class is used to transport action lists, that describe a complete turn of an AI player.
 */
public class TActionsInTurn implements Iterable<TAction>, java.io.Serializable, IHasConsistencyCheck, IActionDescription {
// ------------------------------ FIELDS ------------------------------

    private List<TAction> actions;

    public List<TAction> getActions() {
        return actions;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TActionsInTurn(List<TAction> actions) {
        this.actions = actions;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TActionsInTurn)) return false;

        TActionsInTurn tActions = (TActionsInTurn) o;

        if (actions != null ? !actions.equals(tActions.actions) : tActions.actions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return actions != null ? actions.hashCode() : 0;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------


    @Override
    public boolean isConsistent() {
        if(actions == null) {
            return false;
        }

        for(TAction action : actions) {
            if(!action.isConsistent()) {
                return false;
            }
        }

        return true;
    }

// --------------------- Interface Iterable ---------------------

    @Override
    public Iterator<TAction> iterator() {
        return actions.iterator();
    }
}

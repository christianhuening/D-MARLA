package AgentSystemPluginAPI.Contract;

/**
 * This class is used as a helper object for retrieving data from the database. It resembles a State (or StateAction if
 * an action has been set) equivalent to the reinforcement learning ones.
 *
 */
public class StateAction implements ICompressed {

    private final String stateDescription;
    private String actionDescription = null;

    public StateAction(String stateDescription, String actionDescription) {
        this(stateDescription);
        this.actionDescription = actionDescription;
    }

    public StateAction(String stateDescription) {

        this.stateDescription = stateDescription;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateAction that = (StateAction) o;

        if (actionDescription != null ? !actionDescription.equals(that.actionDescription) : that.actionDescription != null)
            return false;
        if (stateDescription != null ? !stateDescription.equals(that.stateDescription) : that.stateDescription != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stateDescription != null ? stateDescription.hashCode() : 0;
        result = 31 * result + (actionDescription != null ? actionDescription.hashCode() : 0);
        return result;
    }

    @Override
    public String getCompressedRepresentation() {
        return (actionDescription != null) ? stateDescription + actionDescription : stateDescription;
    }

    @Override
    public String toString() {
        return getCompressedRepresentation();
    }
}

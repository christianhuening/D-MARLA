package AgentSystemPluginAPI.Contract;

import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

import java.util.Set;

/**
 * A TransportType for exposure of the AgentSystem to the GUI  <br>
 * Provides the name, version and a description for an AgentSystem.
 * The list of compatible environments is also used for compatibility checks.
 */
public class TAgentSystemDescription {

    private String agentSystemName;
    private String agentSystemVersion;
    private String agentSystemDescription;
    private final Set<TEnvironmentDescription> compatibleEnvironments;

    public TAgentSystemDescription(String agentSystemName, String agentSystemVersion, String agentSystemDescription, Set<TEnvironmentDescription> compatibleEnvironments) {
        if (agentSystemName != null && !agentSystemName.isEmpty()) {
            this.agentSystemName = agentSystemName;
        } else {
            throw new IllegalArgumentException("agentSystemName must not be null or empty.");
        }

        if (agentSystemName != null && !agentSystemName.isEmpty()) {
            this.agentSystemVersion = agentSystemVersion;
        } else {
            throw new IllegalArgumentException("agentSystemVersion must not be null or empty.");
        }

        if (agentSystemName != null) {
            this.agentSystemDescription = agentSystemDescription;
        } else {
            throw new IllegalArgumentException("agentSystemVersion must not be null.");
        }

        if (compatibleEnvironments != null && !compatibleEnvironments.isEmpty()) {
            this.compatibleEnvironments = compatibleEnvironments;
        } else {
            throw new IllegalArgumentException("The list of compatible environments must not be null and contain at least one item.");
        }

    }

    public String getName() {
        return agentSystemName;
    }

    public String getVersion() {
        return agentSystemVersion;
    }

    public String getDescription() {
        return agentSystemDescription;
    }

    public Set<TEnvironmentDescription> getCompatibleEnvironments() {
        return compatibleEnvironments;
    }

    /**
     * Two AgentSystemDescriptions are equal, if and oly if:<br/>
     * - their name and version match (case insensitive) .<br/>
     * - they have an equal set of compatible environments
     * <br/><br/>
     * the description is ignored.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAgentSystemDescription that = (TAgentSystemDescription) o;

        if (!agentSystemName.equalsIgnoreCase(that.agentSystemName)) return false;
        if (!agentSystemVersion.equalsIgnoreCase(that.agentSystemVersion)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = agentSystemName.hashCode();
        result = 31 * result + agentSystemVersion.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return agentSystemName + " " + agentSystemVersion;
    }
}
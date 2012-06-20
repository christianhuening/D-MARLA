package EnvironmentPluginAPI.Contract;

/**
 * This class is used to identify and describe environment plugins.
 */
public class TEnvironmentDescription {

    private final String name;
    private final String version;
    private final String description;

    public TEnvironmentDescription(String name, String version, String description) {

        this.name = name;
        this.version = version;
        this.description = description;

    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " " + version;
    }

    /**
     * Environments are equal, if their name and version match (case insensitive).
     * <br/><br/>
     * The description is ignored.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TEnvironmentDescription that = (TEnvironmentDescription) o;

        if (!name.equalsIgnoreCase((that.name))) return false;
        if (!version.equalsIgnoreCase(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}

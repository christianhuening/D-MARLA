package EnvironmentPluginAPI.TransportTypes;

public class TMARLAClientInstance implements java.io.Serializable {
    private String name;
    private final int id;

    public TMARLAClientInstance(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMARLAClientInstance)) return false;

        TMARLAClientInstance that = (TMARLAClientInstance) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

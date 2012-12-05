package EnvironmentPluginAPI.Contract;

/**
 * This interface contains all aspects (which are relevant for MARLA) of a class that describes the state of an environment.
 */
public interface IEnvironmentState {

    /**
     *  If the custom environment logic has something like a goal that MARLA-Clients pursue, this return value may be
     *  used to inform a client instance that it has reached it.
     * @return see description
     */
    public boolean hasClientMetGoal();

}

package EnvironmentPluginAPI.Service;

import EnvironmentPluginAPI.Contract.IEnvironmentState;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *  If an environment is interested in saving replays of cycles, it can provide an implementation of this interface.
 *  <br/><br/>
 *  MARLA will then use this interface to automatically generate statistics for this environment, which can be viewed
 *  via the Overseer application. The statistics and replays will be accessible even after ending the program.
 *  <br/><br/>
 *  If the environment provides a visualization, it will receive this replays.
 *  <br/><br/>
 *  NOTICE: Iterator has to ensure that the states of the environment are iterated in the order they originally occurred.
 * @param <E> The actual implementation of IEnvironmentState in your Environment
 * @param <C> The actual implementation of IEnvironmentConfiguration
 * @see IEnvironmentState
 * @see IEnvironmentConfiguration
 */
public interface ICycleReplay<E extends IEnvironmentState, C extends IEnvironmentConfiguration> extends Iterable<E>, Serializable {

    /**
     *  A random UUID to identify this replay.
     * @return != null
     */
    UUID getReplayId();

    /**
     *  The initial configuration, that the environment had when it started.
     * @return
     */
    public C getConfiguration();

    /**
     *  The time when the cycle, that this replay describes, took place.
     * @return != null
     */
    Date getReplayDate();

    /**
     *  A list of all agent system names that were part of the cycle.
     * @return a non empty list != null
     */
    List<String> getAgentSystems();

    /**
     *  The name of the agent system that reached the environment's goal (if one exists).
     *  TODO: Since there is a high likeliness of an environment where multiple agent systems can reach a goal, this should be a list.
     * @return may be null, if none did
     */
    String getAgentSystemsWithGoalReached();

    /**
     *  The count of all actions that were performed by all agent systems during this cycle.
     * @return
     */
    int getNumberOfTurns();

    /**
     *  True, if this replay has the same uuid as o and describes exactly the same environment states.
     * @param o
     * @return
     */
    @Override
    boolean equals(Object o);

    /**
     *  The hashCode for this object for correct use in collections.<br/><br/>
     *
     *  It must fulfill the mathematical properties:<br/>
     *  a.equals(b) == true -> a.hashCode == b.hashCode
     * @return
     */
    @Override
    int hashCode();
}

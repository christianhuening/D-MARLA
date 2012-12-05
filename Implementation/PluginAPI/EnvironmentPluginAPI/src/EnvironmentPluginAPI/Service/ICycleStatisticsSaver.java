package EnvironmentPluginAPI.Service;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

/**
 * This interface offers the service for MARLA environment plugins to save replays of environment transitions.
 * <br/><br/>
 * It is optional to use this functionality.
 */
public interface ICycleStatisticsSaver {

    /**
     * Saves a given replay.
     * TODO: We should force the environment to pass a TEnvironmentDescription here, so the GameStatistics can store different types of replays separately.
     * @param environment The environment for which the replay will be saved.
     * @param replay The replay that will be saved.
     */
    public void SaveReplay(ICycleReplay replay, TEnvironmentDescription environment) throws TechnicalException;
}

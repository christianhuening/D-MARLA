package EnvironmentPluginAPI.Service;

import java.io.Serializable;

/**
 * This interface contains all aspects (that are relevant to MARLA) of a class that describes the configuration of an
 * environment .
 *
 * TODO: Deliver me!
 */
public interface IEnvironmentConfiguration extends Serializable {

    /**
     *  The result of this method will be shown in the session config GUI.
     * @return != null
     */
    @Override
    public String toString();
}

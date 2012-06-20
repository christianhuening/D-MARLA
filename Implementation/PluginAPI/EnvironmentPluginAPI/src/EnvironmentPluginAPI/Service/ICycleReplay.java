package EnvironmentPluginAPI.Service;

import EnvironmentPluginAPI.Contract.IEnvironmentState;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 15.06.12
 * Time: 13:23
 * //@param T : The actual implementation of IEnvironmentState in your Environment
 */
public interface ICycleReplay<T extends IEnvironmentState> extends java.io.Serializable {
    UUID getReplayId();

    DateTime getReplayDate();

    List<String> getPlayers();

    String getWinningPlayer();

    int getNumberOfTurns();

    List<T> getEnvironmentStatesPerTurn();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}

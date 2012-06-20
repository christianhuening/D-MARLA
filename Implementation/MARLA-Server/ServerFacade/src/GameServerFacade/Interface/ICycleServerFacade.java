package GameServerFacade.Interface;

import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import RemoteInterface.ICycleStatistics;
import ServerRunner.Interface.IServerRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 13.05.12
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public interface ICycleServerFacade extends
        ICycleStatistics,
        ISaveGameStatistics,
        IServerRunner {

    /**
     * Saves the given map to a file in the maps directory.
     * if a map with that name already exists, it will be overwritten.
     * @param mapMetaData the map to save
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
     */
    public void saveMap(TMapMetaData mapMetaData, TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException;

    /**
     * Gets all available maps from the maps directory.
     * @return empty, if no maps found.
     */
    public List<TMapMetaData> getAvailableMaps(TEnvironmentDescription environment) throws CorruptMapFileException, TechnicalException, PluginNotReadableException;
}

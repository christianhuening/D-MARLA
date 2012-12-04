import Actions.CliffActionDescription;
import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Logic.CliffSession;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 28.11.12
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
public class CliffEnvironmentPlugin implements IEnvironment {

    private CliffSession session;
    private TMARLAClientInstance activeInstance;

    @Override
    public List<TMapMetaData> getAvailableMaps() throws CorruptMapFileException, TechnicalException {
        return new LinkedList<TMapMetaData>();
    }

    @Override
    public void saveMap(TMapMetaData tMapMetaData) throws TechnicalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IEnvironmentState start(List<TMARLAClientInstance> marlaClientInstances, TMapMetaData tMapMetaData) throws TechnicalException, IllegalNumberOfClientsException {
        if (marlaClientInstances.size() != 1) {
            throw new IllegalNumberOfClientsException("The cliff needs exactly one participant.");
        }

        session = new CliffSession();
        activeInstance = marlaClientInstances.get(0);
        return session.getCurrentState();
    }

    @Override
    public boolean isStillActive() {
        return session.isStillActive();
    }

    @Override
    public TMARLAClientInstance getActiveInstance() {
        return activeInstance;
    }

    @Override
    public IEnvironmentState getCurrentGameState() throws TechnicalException {
        return session.getCurrentState();
    }

    @Override
    public IEnvironmentState executeAction(IActionDescription actionDescription) throws TechnicalException {
        CliffActionDescription action = (CliffActionDescription)actionDescription;
        session.moveAgent(action.getDirection());

        return session.getCurrentState();
    }

    @Override
    public void endTurn() throws TechnicalException {
        // nothing to do here
    }

    @Override
    public void end() throws TechnicalException {
        // nothing to do here
    }
}

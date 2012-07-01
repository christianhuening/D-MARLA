package Factory.GameLogic;

import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.TransportTypes.TActionsInTurn;
import Factory.GameLogic.TransportTypes.TUnit;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 19:32
 * To change this template use File | Settings | File Templates.
 */
public class GameLogicComponent implements IEnvironment {
// ------------------------------ FIELDS ------------------------------

    private GameLogicUseCase useCase;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameLogicComponent(ISaveGameStatistics saveGameStatistics) {
        this.useCase = new GameLogicUseCase(saveGameStatistics);
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IGameLogic ---------------------

    @Override
    public List<TMapMetaData> getAvailableMaps() throws CorruptMapFileException, TechnicalException {
        return useCase.getAvailableMaps();
    }

    @Override
    public void saveMap(TMapMetaData map) throws TechnicalException {
        useCase.saveMap(map);
    }

    @Override
    public IEnvironmentState start(List<TMARLAClientInstance> players, TMapMetaData metaData) throws TechnicalException, IllegalNumberOfClientsException {
        return useCase.start(players, metaData);
    }

    @Override
    public boolean isStillActive() {
        return useCase.isStillActive();
    }

    @Override
    public TMARLAClientInstance getActiveInstance() {
        return useCase.getActiveInstance();
    }

    @Override
    public IEnvironmentState getCurrentGameState() throws TechnicalException {
        return useCase.getCurrentGameState();
    }

    @Override
    public IEnvironmentState executeAction(IActionDescription actionsInTurn) throws TechnicalException {
        return useCase.executeAction(actionsInTurn);
    }

    @Override
    public void endTurn() throws TechnicalException {
        useCase.endTurn();
    }

    @Override
    public void end() throws TechnicalException {
        useCase.end();
    }


    public void moveUnit(TUnit unitBlue, Direction right) throws TechnicalException {
        useCase.moveUnit(unitBlue, right);
    }
}

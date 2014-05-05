package Factory.GameLogic;

import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.TransportTypes.TUnit;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 19:32
 * To change this template use File | Settings | File Templates.
 */
public class GameLogicComponent implements IEnvironment<TMapMetaData, IEnvironmentState, IActionDescription> {
// ------------------------------ FIELDS ------------------------------

    private GameLogicUseCase useCase;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameLogicComponent(ICycleStatisticsSaver saveGameStatistics) {
        this.useCase = new GameLogicUseCase(saveGameStatistics);
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IGameLogic ---------------------

    public List<TMapMetaData> getAvailableMaps() throws CorruptConfigurationFileException, TechnicalException {
        return useCase.getAvailableMaps();
    }


    public void saveMap(TMapMetaData map) throws TechnicalException {
        useCase.saveMap(map);
    }



    @Override
    public IEnvironmentState start(List<TMARLAClientInstance> players, TMapMetaData iEnvironmentConfiguration) throws TechnicalException, IllegalNumberOfClientsException {
        return useCase.start(players, iEnvironmentConfiguration);
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
    public IEnvironmentState getCurrentEnvironmentState() throws TechnicalException {
        return useCase.getCurrentEnvironmentState();
    }

    @Override
    public IEnvironmentState executeAction(IActionDescription actionsInTurn) throws TechnicalException {
        return useCase.executeAction(actionsInTurn);
    }


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

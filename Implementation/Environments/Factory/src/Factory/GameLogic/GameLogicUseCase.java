package Factory.GameLogic;

import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.FactoryPluginDescriptor;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.GameActors.Player;
import Factory.GameLogic.TransportTypes.TActionsInTurn;
import Factory.GameLogic.TransportTypes.TUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 04.06.12
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class GameLogicUseCase implements IEnvironment {
// ------------------------------ FIELDS ------------------------------

    private ISaveGameStatistics saveGameStatistics;
    private List<Player> players = new ArrayList<Player>();
    private Game currentlyActiveGame;
    private MapScanner mapScanner = new MapScanner("./maps/");
    private FactoryPluginDescriptor factoryPluginDescriptor;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameLogicUseCase(ISaveGameStatistics saveGameStatistics) {
        this.saveGameStatistics = saveGameStatistics;
        this.factoryPluginDescriptor = new FactoryPluginDescriptor();
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        if (currentlyActiveGame != null)
            return currentlyActiveGame.toString();
        else
            return "No Game started yet";
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IGameLogic ---------------------

    @Override
    public List<TMapMetaData> getAvailableMaps() throws CorruptMapFileException, TechnicalException {
        return mapScanner.searchMaps();
    }

    @Override
    public void saveMap(TMapMetaData map) throws TechnicalException {
        mapScanner.saveMap(map);
    }

    @Override
    public IEnvironmentState start(List<TMARLAClientInstance> players, TMapMetaData metaData) throws TechnicalException, IllegalNumberOfClientsException {
        this.players = new ArrayList<Player>();

        int i = 0;
        for (TMARLAClientInstance player : players) {
            this.players.add(new Player(player, Faction.values()[i]));
            i++;
        }

        currentlyActiveGame = new Game(metaData, this.players);

        return currentlyActiveGame.getCurrentGameState();
    }

    @Override
    public boolean isStillActive() {
        return currentlyActiveGame.getWinningPlayer() != null;
    }

    /**
     * Marks, which instance of a MARLA-Client was chosen to make the next turn.
     * <br/><br/>
     * The client, that is returned here, will receive this environment state and his answer in form of an
     * IActionDescription will be the next input for the environment.
     *
     * @return must be part of this environment, != null
     * @see EnvironmentPluginAPI.Contract.IActionDescription
     * @see EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance
     */
    @Override
    public TMARLAClientInstance getActiveInstance() {
        return currentlyActiveGame.getActivePlayer().getClientInstance();
    }

    @Override
    public IEnvironmentState getCurrentGameState() throws TechnicalException {
        return currentlyActiveGame.getCurrentGameState();
    }

    @Override
    public IEnvironmentState executeAction(IActionDescription actionsInTurn) throws TechnicalException {
        currentlyActiveGame.executeActionList((TActionsInTurn)actionsInTurn);
        return currentlyActiveGame.getCurrentGameState();
    }

    public IEnvironmentState moveUnit(TUnit unit, Direction direction) throws TechnicalException {
        currentlyActiveGame.moveUnit(unit, direction);

        return currentlyActiveGame.getCurrentGameState();
    }

    @Override
    public void endTurn() throws TechnicalException {
        currentlyActiveGame.endTurn();
    }

    @Override
    public void end() throws TechnicalException {

        ICycleReplay replay = currentlyActiveGame.getReplay();

        saveGameStatistics.SaveReplay(replay, factoryPluginDescriptor.getDescription());

        boolean gameFinished = currentlyActiveGame.gameFinished();
    }


}

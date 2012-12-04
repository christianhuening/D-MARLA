package Export;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Services.IAgent;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.TAction;
import Factory.GameLogic.TransportTypes.TActionsInTurn;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.GameLogic.TransportTypes.TUnit;
import Factory.GameLogic.Utility.GameInfos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 28.05.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class RandomAgentImplementation implements IAgentSystem<TGameState, TActionsInTurn> {

    private Faction ownFaction;

    public RandomAgentImplementation() {
        System.err.println("TGameState classloader im Plugin" + TGameState.class.getClassLoader());
    }

    @Override
    public void start(Object faction) {
        ownFaction = (Faction)faction;
    }

    @Override
    public TActionsInTurn getActionsForEnvironmentStatus(TGameState tGameState) {
        Random random = new Random();

        List<TAction> actionList = new ArrayList<TAction>();

        for(TUnit unit : GameInfos.getUnitsForFaction(tGameState, ownFaction)) {
            List<Direction> legalDirections = GameInfos.getLegalDirectionForUnit(tGameState, unit);

            if (legalDirections.size() > 0) {
                Direction direction = legalDirections.get(random.nextInt(legalDirections.size()));

                actionList.add(new TAction(unit, direction));
            }
        }

        return new TActionsInTurn(actionList);
    }

    @Override
    public void end() {

    }

    @Override
    public List<IAgent> getInternalAgents() {
        return new LinkedList<IAgent>();
    }
}

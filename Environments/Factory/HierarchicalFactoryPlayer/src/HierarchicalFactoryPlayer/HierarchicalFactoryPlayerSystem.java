package HierarchicalFactoryPlayer;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import Factory.GameLogic.TransportTypes.TActionsInTurn;
import Factory.GameLogic.TransportTypes.TGameState;
import HierarchicalFactoryPlayer.StateActionGenerators.EvaluatorStateActionGenerator;
import HierarchicalFactoryPlayer.StateActionGenerators.MoverStateActionGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 13.06.2014.
 */
public class HierarchicalFactoryPlayerSystem implements IAgentSystem<IEnvironmentConfiguration, TGameState, TActionsInTurn> {
    private final IAgent evaluator;
    private final IAgent mover;
    private List<IAgent> agentlist;

    public HierarchicalFactoryPlayerSystem(IPluginServiceProvider provider) throws TechnicalException {
        agentlist = new ArrayList<>();
        evaluator = provider.getTableAgent("Evaluator", LearningAlgorithm.SARSALambda, new EvaluatorStateActionGenerator());
        agentlist.add(evaluator);
        mover = provider.getTableAgent("Mover", LearningAlgorithm.SARSALambda, new MoverStateActionGenerator());
        agentlist.add(mover);


    }

    @Override
    public void start(IEnvironmentConfiguration environmentConfiguration) throws TechnicalException {

    }

    @Override
    public TActionsInTurn getActionsForEnvironmentStatus(TGameState current) throws TechnicalException {
        return null;
    }

    @Override
    public void end() throws TechnicalException {

    }


    public List<IAgent> getInternalAgents(){
        return this.agentlist;
    }
}

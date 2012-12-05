package AgentProvider.Implementation.Agents;


import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

public class QLearningAgent extends EpsilonGreedyAgent {
    private StateAction sa;
    private float maxQ;
    private float oldQ;


	public QLearningAgent(String name, IDictionary qValues, IStateActionGenerator stateActionGenerator, IAgentSettingUpdatedListener agentSettingUpdatedListener) {
		super(name, qValues, stateActionGenerator, agentSettingUpdatedListener);
	}

    @Override
    public StateAction startEpisode(StateAction state) throws TechnicalException {
        sa = getEpsilonInfluencedAction(state);
        return sa;
    }

    @Override
    public StateAction getCurrentState() {
        return sa;
    }

    @Override
    public StateAction step(float rewardForLastStep, StateAction newState) throws TechnicalException {
        if(sa == null) {
            throw new RuntimeException(ErrorMessages.get("startStateNotInitialized", getName()));
        }

        updateQ(sa, getBestAction(newState), rewardForLastStep);

        sa = getEpsilonInfluencedAction(newState);
        return sa;
    }

    @Override
    public void endEpisode(StateAction lastState, float reward) throws TechnicalException {
        updateQ(sa, getBestAction(lastState), reward);
        sa = null;
    }

    protected void updateQ(StateAction sa, StateAction s_a_, float reward) throws TechnicalException {
        oldQ = qValues.getValue(sa);

        //determine the Q Value of the sa action because we take its
        //value for learning, no matter what was really s_a_.
        maxQ = qValues.getValue(getBestAction(s_a_));
        qValues.setValue(sa, oldQ + (getAlpha() * (reward + (getGamma() * maxQ) - oldQ)));
    }
}
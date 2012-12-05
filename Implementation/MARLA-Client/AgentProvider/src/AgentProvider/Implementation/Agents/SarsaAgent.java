package AgentProvider.Implementation.Agents;


import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

public class SarsaAgent extends EpsilonGreedyAgent {

    private StateAction sa;
    private StateAction s_a_;

	public SarsaAgent(String name, IDictionary qValues, IStateActionGenerator stateActionGenerator, IAgentSettingUpdatedListener agentSettingUpdatedListener) {
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

        s_a_ = getEpsilonInfluencedAction(newState);

        updateQ(s_a_, rewardForLastStep);

        sa = s_a_;
        return sa;
    }

    private void updateQ(StateAction s_a_, float reward) throws TechnicalException {
        qValues.setValue(sa, qValues.getValue(sa) + (getAlpha() * (reward + (getGamma()* qValues.getValue(s_a_)) - qValues.getValue(sa))));
    }

    @Override
    public void endEpisode(StateAction stateAction, float reward) throws TechnicalException {
        updateQ(stateAction, reward);
    }
}
package AgentProvider.Implementation.Agents;

import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

import java.util.LinkedList;
import java.util.List;

public class SarsaLambdaAgent extends EpsilonGreedyAgent {

    private final IDictionary eValues;
    private List<StateAction> history;
    private StateAction sa;
    private StateAction s_a_;

    //caching
    float delta;
    float oldQ;
    float oldE;

    public SarsaLambdaAgent(String name, IDictionary qValues, IDictionary eValues, IStateActionGenerator stateActionGenerator, IAgentSettingUpdatedListener agentSettingUpdatedListener) {
        super(name, qValues, stateActionGenerator, agentSettingUpdatedListener);
        this.eValues = eValues;
        history = new LinkedList<StateAction>();
    }

    @Override
    public StateAction getCurrentState() {
        return sa;
    }

    @Override
    public StateAction startEpisode(StateAction state) throws TechnicalException {
        sa = getEpsilonInfluencedAction(state);
        history.add(0, sa);
        return sa;
    }


    @Override
    public StateAction step(float rewardForLastStep, StateAction newState) throws TechnicalException {
        if(sa == null) {
            throw new RuntimeException(ErrorMessages.get("startStateNotInitialized", getName()));
        }

        s_a_ = getEpsilonInfluencedAction(newState);

        updateValues(rewardForLastStep, s_a_);

        sa = s_a_;
        history.add(0, sa);
        return sa;
    }

    private void updateValues(float reward, StateAction s_a_) throws TechnicalException {

        oldQ = qValues.getValue(sa);
        oldE = eValues.getValue(sa) + 1.0f;

        delta = reward + (getGamma() * qValues.getValue(s_a_)) - oldQ;

            for (int i = 0; i < history.size(); i++) {
                qValues.setValue(sa, oldQ + (getAlpha() * delta * oldE));
                eValues.setValue(sa, getGamma() * getLambda() * oldE);
            }
    }

    @Override
    public void endEpisode(StateAction stateAction, float reward) throws TechnicalException {
        updateValues(reward, stateAction);
    }

}
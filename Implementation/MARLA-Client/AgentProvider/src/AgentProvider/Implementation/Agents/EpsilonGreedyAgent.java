package AgentProvider.Implementation.Agents;

import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

import java.util.Random;
import java.util.Set;

/**
 * The abstract implementation of an epsilon-greedy agent.
 * <p/>
 * Note, that the agent's learning parameters are all 0.0f by default.
 */
abstract class EpsilonGreedyAgent implements IAgent {

    //agent settings
    private float epsilon;
    private float lambda;
    private float gamma;
    private float alpha;
    private String name;

    //epsilon greedy implementation
    protected final IDictionary qValues;

    private IStateActionGenerator stateActionGenerator;
    //technical
    private final IAgentSettingUpdatedListener settingUpdatedListener;

    //caching
    private Random random = new Random();

    public EpsilonGreedyAgent(String name, IDictionary qValues, IStateActionGenerator stateActionGenerator,IAgentSettingUpdatedListener settingUpdatedListener) {
        this.name = name;
        this.stateActionGenerator = stateActionGenerator;
        this.settingUpdatedListener = settingUpdatedListener;
        this.qValues = qValues;
    }

    protected StateAction getBestAction(StateAction state) throws TechnicalException {

        float value = Float.NEGATIVE_INFINITY;
        float tmp = 0;
        StateAction result = null;
        for (StateAction action : stateActionGenerator.getAllPossibleActions(state)) {
            tmp = qValues.getValue(action);
            if(tmp >= value) {
                value = tmp;
                result = action;
            }
        }

        return result;
    }

    protected StateAction getEpsilonInfluencedAction(StateAction state) throws TechnicalException {

        StateAction result;

        // get all possible actions and test their validity
        Set<StateAction> possibleActions = stateActionGenerator.getAllPossibleActions(state);
        for(StateAction sa : possibleActions) {
            if(sa == null || sa.getCompressedRepresentation() == null) {
                throw new RuntimeException(ErrorMessages.get("erroneousStateActionGenerator"));
            }
        }

        // If there are alternatives:
        // normally choose the best one, but by chance choose one with a worse expected reward
        // else take the single one.
        if(possibleActions.size() > 1 && epsilon >= random.nextFloat()) {

            possibleActions.remove(getBestAction(state));

            StateAction[] stateActions = new StateAction[possibleActions.size()];

            int i = 0;
            for(StateAction action : possibleActions) {
                stateActions[i] = new StateAction(state.getStateDescription(), action.getActionDescription());
                i++;
            }

            result = stateActions[random.nextInt(stateActions.length)];

        } else {
            result = getBestAction(state);
        }

        return  result;
    }

    public abstract StateAction step(float rewardForLastStep, StateAction newState) throws TechnicalException;

    private void fireSettingChangedEvent(AgentSettingName name) {
        switch (name) {
            case ALPHA:
                settingUpdatedListener.onAgentSettingUpdated(this, AgentSettingName.ALPHA, alpha);
                break;
            case EPSILON:
                settingUpdatedListener.onAgentSettingUpdated(this, AgentSettingName.EPSILON, epsilon);
                break;
            case GAMMA:
                settingUpdatedListener.onAgentSettingUpdated(this, AgentSettingName.GAMMA, gamma);
                break;
            case LAMBDA:
                settingUpdatedListener.onAgentSettingUpdated(this, AgentSettingName.LAMBDA, lambda);
                break;
        }
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        fireSettingChangedEvent(AgentSettingName.ALPHA);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
        fireSettingChangedEvent(AgentSettingName.EPSILON);
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        fireSettingChangedEvent(AgentSettingName.GAMMA);
    }

    public float getGamma() {
        return gamma;
    }

    public void setLambda(float lambda) {
        this.lambda = lambda;
        fireSettingChangedEvent(AgentSettingName.LAMBDA);
    }

    public float getLambda() {
        return lambda;
    }

    public String getName() {
        return name;
    }
}

package SimpleFactoryPlayer.UnitTest;

import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import SimpleFactoryPlayer.Implementation.StateActionGenerator;

import java.util.Random;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 10.06.12
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class DummyAgent implements IAgent {
    StateActionGenerator stateActionGenerator = new StateActionGenerator();
    Random random = new Random();
    @Override
    public StateAction startEpisode(StateAction stateAction) throws TechnicalException {
        Set<StateAction> actions=stateActionGenerator.getAllPossibleActions(stateAction);
        StateAction returnAction = null;
        for(StateAction action : actions){

            if(!action.getActionDescription().equals("0")){
                returnAction=action;
            }

        }
        if(returnAction==null){
            returnAction = new StateAction("","0");

            System.out.println("noactionInstart");
        }

        return returnAction;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StateAction getCurrentState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StateAction step(float v, StateAction stateAction) throws TechnicalException {
        Set<StateAction> actions=stateActionGenerator.getAllPossibleActions(stateAction);
        StateAction returnAction = null;
        for(StateAction action : actions){

            if(!action.getActionDescription().equals("0")){
                returnAction=action;
            }
        }

        if(returnAction==null){
            returnAction = new StateAction("","0");
            System.out.println("noactionIn step");
        }

        return returnAction;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endEpisode(StateAction stateAction, float v) throws TechnicalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLambda(float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getLambda() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setGamma(float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getGamma() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEpsilon(float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getEpsilon() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getAlpha() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAlpha(float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

package SimpleFactoryPlayer.UnitTest;

import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 10.06.12
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class DummyPluginProvider implements IPluginServiceProvider {
    @Override
    public File agentDirectory() {
        return null;
    }

    @Override
    public void saveAgentSystemSetting(String s, String s1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAgentSystemSetting(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IAgent getTableAgent(String s, LearningAlgorithm learningAlgorithm, IStateActionGenerator iStateActionGenerator) throws TechnicalException {
        return new DummyAgent();  //To change body of implemented methods use File | Settings | File Templates.
    }
}

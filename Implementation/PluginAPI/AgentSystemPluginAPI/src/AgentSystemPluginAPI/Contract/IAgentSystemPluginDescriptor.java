package AgentSystemPluginAPI.Contract;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 25.05.12
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */

/**
 * CONTRACT:
 * Agent system plugins must implement this class to be recognized by the AgentSystemManagement.
 */
public interface IAgentSystemPluginDescriptor {
    /**
     * Gets the description of this agent system.
     * @return
     */
    public TAgentSystemDescription getDescription();

    /**
     * Gets a fully initialized instance of this agent system.
     * The returned system will use the existing learning data.
     * @param serviceProvider
     * @return
     */
    public IAgentSystem getInstance(IPluginServiceProvider serviceProvider) throws TechnicalException;
}

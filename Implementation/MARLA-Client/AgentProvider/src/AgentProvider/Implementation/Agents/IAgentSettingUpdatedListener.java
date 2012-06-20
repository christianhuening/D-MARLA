package AgentProvider.Implementation.Agents;


import AgentSystemPluginAPI.Services.IAgent;

/**
 * Callback interface for listeners for the event, that an IAgent's settings have changed.
 */
public interface IAgentSettingUpdatedListener {
    public void onAgentSettingUpdated(IAgent agent, AgentSettingName agentSettingName, float value);
}

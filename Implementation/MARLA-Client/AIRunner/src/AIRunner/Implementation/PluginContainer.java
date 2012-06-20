package AIRunner.Implementation;

import AIRunner.Interface.IAIRunnerEventHandler;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import NetworkAdapter.Interface.MessageChannel;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import Settings.SettingException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

/**
 * This class implements the logic of the periodic receiving and sending of game related messages and feeding the
 * plugin with it.
 */
public class PluginContainer extends Thread {
// ------------------------------ FIELDS ------------------------------

    private final IAgentSystemManagement agentSystemManagement;
    private final IClientNetworkAdapter clientNetworkAdapter;
    private final IAIRunnerEventHandler IAIRunnerEventHandler;
    private final IPluginLoader pluginLoader;
    private IAgentSystem plugin;
    private IEnvironmentState environmentState;
    private TAgentSystemDescription agentSystemDescription;

    private String hostname;
    private int port;

// --------------------------- CONSTRUCTORS ---------------------------

    public PluginContainer(IAgentSystemManagement agentSystemManagement,
                           IClientNetworkAdapter clientNetworkAdapter,
                           IAIRunnerEventHandler aiRunnerEventHandler,
                           IPluginLoader pluginLoader, String hostname, int port)
            throws TechnicalException, PluginNotReadableException {
        this.agentSystemManagement = agentSystemManagement;
        this.clientNetworkAdapter = clientNetworkAdapter;
        this.IAIRunnerEventHandler = aiRunnerEventHandler;
        this.pluginLoader = pluginLoader;
        this.hostname = hostname;
        this.port = port;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Runnable ---------------------

    @Override
    public void run() {
        //try to load plugin, if not possible exit
        try {
            plugin = agentSystemManagement.getAgentSystem(agentSystemDescription);
            clientNetworkAdapter.connectToServer(hostname, port, agentSystemDescription.toString());
        } catch (TechnicalException e) {
            handleException(e);
            return;
        } catch (PluginNotReadableException e) {
            handleException(e);
            return;
        } catch (SettingException e) {
            handleException(e);
            return;
        } catch (HostUnreachableException e) {
            handleException(e);
            return;
        }

        while (!isInterrupted()) {
            try {
                if (!isInterrupted()) {
                    synchronized (this) {
                        wait();
                    }

                    clientNetworkAdapter.sendNetworkMessage(pluginLoader.createActionDescriptionMessage(plugin.getActionsForEnvironmentStatus(environmentState)), MessageChannel.DATA);
                }
            } catch (InterruptedException e) {
                handleException(e);
            } catch (ConnectionLostException e) {
                handleException(e);
            } catch (NotConnectedException e) {
                handleException(e);
            } catch (TechnicalException e) {
                handleException(e);
            }
        }
    }

// -------------------------- PUBLIC METHODS --------------------------

    public void end() {
        try {
            plugin.end();
        } catch (TechnicalException e) {
            handleException(e);
        }
    }

    /**
     * Loads the plugin and starts running the session
     *
     * @param agentSystemDescription the plugin's description != null
     */
    public void load(TAgentSystemDescription agentSystemDescription) {
        this.agentSystemDescription = agentSystemDescription;
        start();
    }

    /**
     * memorizes the environmentState for processing upon waking up
     *
     * @param environmentState
     */
    public void receiveGameState(IEnvironmentState environmentState) {
        this.environmentState = environmentState;
    }

    public void start(Object environmentInitInfo) {
        try {
            plugin.start(environmentInitInfo);
        } catch (TechnicalException e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        interrupt();
        IAIRunnerEventHandler.onException(e);
        plugin = null;
    }

    /**
     * interrupts this thread.
     */
    public void stopPlugin() {
        interrupt();
        plugin = null;
    }
}

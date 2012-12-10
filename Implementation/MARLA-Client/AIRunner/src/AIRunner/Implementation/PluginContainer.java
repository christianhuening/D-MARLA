package AIRunner.Implementation;

import AIRunner.Interface.IAIRunnerEventHandler;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import NetworkAdapter.Interface.MessageChannel;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import ZeroTypes.Settings.SettingException;

/**
 * This class implements the logic of the periodic receiving and sending of game related messages and feeding the
 * plugin with it.
 */
public class PluginContainer extends Thread {
// ------------------------------ FIELDS ------------------------------

    private final IAgentSystemManagement agentSystemManagement;
    private final IClientNetworkAdapter clientNetworkAdapter;
    private final IAIRunnerEventHandler IAIRunnerEventHandler;
    private final IAgentSystemPluginLoader agentSystemPluginLoader;
    private IAgentSystem plugin;
    private IEnvironmentState environmentState;
    private TAgentSystemDescription agentSystemDescription;

    private String hostname;
    private int port;

// --------------------------- CONSTRUCTORS ---------------------------

    public PluginContainer(IAgentSystemManagement agentSystemManagement,
                           IClientNetworkAdapter clientNetworkAdapter,
                           IAIRunnerEventHandler aiRunnerEventHandler,
                           IAgentSystemPluginLoader agentSystemPluginLoader, String hostname, int port)
            throws TechnicalException, PluginNotReadableException {
        this.agentSystemManagement = agentSystemManagement;
        this.clientNetworkAdapter = clientNetworkAdapter;
        this.IAIRunnerEventHandler = aiRunnerEventHandler;
        this.agentSystemPluginLoader = agentSystemPluginLoader;
        this.hostname = hostname;
        this.port = port;
        environmentState = null;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Runnable ---------------------

    @Override
    public void run() {
        //try to connect to the server, if not possible exit
        try {
            plugin = agentSystemManagement.getAgentSystem(agentSystemDescription);
            clientNetworkAdapter.connectToServer(hostname, port, agentSystemDescription.toString());
            Thread.currentThread().setContextClassLoader(agentSystemPluginLoader.getUsedClassLoader());
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

        /*
         *  Wait for environment states to be received.
         *  When it is received, get according action from agent system plugin, send it to the server
         */
        while (!isInterrupted()) {
            try {
                synchronized (this) { // make sure that the value of environmentState doesn't change during this section
                    if (!isInterrupted() && environmentState != null) {
                        clientNetworkAdapter.sendNetworkMessage(
                                agentSystemPluginLoader.createActionDescriptionMessage(clientNetworkAdapter.getClientId(),
                                        plugin.getActionsForEnvironmentStatus(environmentState))
                                , MessageChannel.DATA);
                        environmentState = null;
                    } else {
                        wait(150); // after a
                    }
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
        synchronized (this) {
            this.environmentState = environmentState;
            notify();
        }
    }

    public void start(IEnvironmentConfiguration environmentConfiguration) {
        try {
            plugin.start(environmentConfiguration);
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

package AIRunner.Interface;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.security.InvalidParameterException;

/**
 * This class encapsulates the conduction of all application parts to work in concert.
 *
 */
public interface IAIRunner {

    /**
     * Tries to connect to the given IP or hostname.
     * @param hostname the host to connect to (IPv4, IPv6 address or hostname), != null
     * @param port the server's main port to connect to, != null
     * @param agentSystemDescription an agentSystemDescriptor not empty, != null
     * @throws InvalidParameterException if the hostname or the port weren't valid
     */
    public void connectToServer(TAgentSystemDescription agentSystemDescription, String hostname, int port) throws HostUnreachableException, InvalidParameterException, PluginNotReadableException, TechnicalException;

    /**
     * Subscribes the object to be called, when a network event occurs.
     * @param networkEventHandler
     */
    public void addListener(IAIRunnerEventHandler networkEventHandler);

    /**
     * Disconnects from the server, if a connection is established. Does nothing else.
     *
     * @throws SessionRunningException if a session is running at the moment this method is called.
     */
    public void disconnect() throws SessionRunningException;



}

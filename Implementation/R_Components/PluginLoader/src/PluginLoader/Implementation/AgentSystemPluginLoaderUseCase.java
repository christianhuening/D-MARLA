package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Messages.DefaultActionDescriptionMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import ZeroTypes.Settings.AppSettings;
import ZeroTypes.Settings.SettingException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class implements all logic affiliated with the loading of agent system plugins.
 */
public class AgentSystemPluginLoaderUseCase implements IAgentSystemPluginLoader {

    private Map<TAgentSystemDescription, File> aiPluginPaths;
    private PluginHelper pluginHelper;
    private Constructor customActionDescriptionMessage;


    public AgentSystemPluginLoaderUseCase() throws TechnicalException, SettingException, PluginNotReadableException {
        pluginHelper = new PluginHelper();
        listAvailableAgentSystemPlugins();
    }

    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins() throws TechnicalException, PluginNotReadableException, SettingException {
        pluginHelper = new PluginHelper();
        aiPluginPaths = new Hashtable<TAgentSystemDescription, File>();

        List<TAgentSystemDescription> result = new LinkedList<TAgentSystemDescription>();

        // Load all plugins recursively from the directory specified in the config file.
        try {
            List<String> allJars = pluginHelper.findJarsRecursively(AppSettings.getString("agentSystemPluginDirectory"));

            for (String jarPath : allJars) {
                //look for the first class that abides the contract that is not the interface itself
                for (Class c : pluginHelper.listClassesFromJar(jarPath)) {
                    if (IAgentSystemPluginDescriptor.class != c
                            && IAgentSystemPluginDescriptor.class.isAssignableFrom(c)) {

                        //save available AgentSystemDescriptor description and it's directory
                        TAgentSystemDescription tmp = ((IAgentSystemPluginDescriptor) c.newInstance()).getDescription();
                        result.add(tmp);
                        aiPluginPaths.put(tmp, new File(jarPath));
                        break;
                    }
                }

            }

        } catch (InstantiationException e) {
            throw new TechnicalException("Unable to load Class from plugin. Reason: \n\n" + e);
        } catch (IllegalAccessException e) {
            throw new TechnicalException("Unable to load Class from plugin. Reason: \n\n" + e);
        } catch (SettingException se) {
            // we assume, that we are in the server and thus can't have this setting
            //TODO: This must be fixed, Server- and Client Pluginloader should be separated completely
        }

        return result;
    }

    public IAgentSystemPluginDescriptor loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {
        IAgentSystemPluginDescriptor loadedAgentSystemDescriptor = null;

        customActionDescriptionMessage = null;

        try {
            // Load all classes from the jar where this plugin is located
            List<Class> classesInJar = pluginHelper.loadJar(aiPluginPaths.get(agentSystem).getPath());


            //search for the first class that abides the contract that is not the interface itself
            for (Class c : classesInJar) {
                if (!IAgentSystemPluginDescriptor.class.equals(c)
                        && IAgentSystemPluginDescriptor.class.isAssignableFrom(c)) {

                    //save available AgentSystemDescriptor description and it's directory
                    loadedAgentSystemDescriptor = (IAgentSystemPluginDescriptor) c.newInstance();
                    continue;
                }

                if (!IActionDescriptionMessage.class.equals(c)
                        && IActionDescriptionMessage.class.isAssignableFrom(c)) {

                    //examine constructors
                    customActionDescriptionMessage = pluginHelper.findSuitableConstructor(c, int.class, IActionDescription.class);

                    if (customActionDescriptionMessage == null) {
                        throw new PluginNotReadableException("A custom action description message was found, but didn't provide the needed constructor => (int clientId, A extends IActionDescription state).", aiPluginPaths.get(agentSystem).getPath());
                    }
                }
            }

        } catch (InstantiationException e) {
            throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) + "' Reason: \n\n" + e);
        } catch (IllegalAccessException e) {
            throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) + "' Reason: \n\n" + e);
        }

        if (loadedAgentSystemDescriptor != null) {
            return loadedAgentSystemDescriptor;
        } else {
            throw new PluginNotReadableException("Plugin didn't provide a descriptor: ", aiPluginPaths.get(agentSystem).getPath());
        }
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param agentSystemDescription a description of an existing agent system plugin != null
     * @return null, if agent system plugin was not found
     * @throws EnvironmentPluginAPI.Exceptions.TechnicalException
     *          if technical errors prevent the component from loading the plugin specified
     * @throws PluginLoader.Interface.Exceptions.PluginNotReadableException
     *          if the plugin is not readable, for example if no TAgentSystemDescription is provided
     */
    public File getAgentSystemPluginPath(TAgentSystemDescription agentSystemDescription) throws TechnicalException, PluginNotReadableException {
        if (aiPluginPaths == null) {
            throw new UnsupportedOperationException("protocol violated: listAvailableAgentSystems must be called before this method.");
        }

        return aiPluginPaths.get(agentSystemDescription).getParentFile();
    }

    /**
     * Creates an action description message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used. The message will be targeted to the server automatically
     *
     * @param actionDescription the action description to send
     * @param clientId          client's network id
     * @return not null
     */
    public NetworkMessage createActionDescriptionMessage(int clientId, IActionDescription actionDescription) {
        if (customActionDescriptionMessage != null) {

            try { //TODO: needs better exception handling
                return (NetworkMessage) customActionDescriptionMessage.newInstance(clientId, actionDescription);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return new DefaultActionDescriptionMessage(0, actionDescription);
    }

}

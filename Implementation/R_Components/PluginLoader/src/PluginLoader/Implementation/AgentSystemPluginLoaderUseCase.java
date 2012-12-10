package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.IClientNetworkAdapter;
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

    private final IClientNetworkAdapter clientNetworkAdapter;
    private Map<TAgentSystemDescription, File> aiPluginPaths;
    private PluginHelper pluginHelper;
    private Constructor customActionDescriptionMessage;
    private IAgentSystemPluginDescriptor loadedPlugin;
    private ClassLoader usedClassLoader;


    public AgentSystemPluginLoaderUseCase(IClientNetworkAdapter clientNetworkAdapter) throws TechnicalException, SettingException, PluginNotReadableException {
        this.clientNetworkAdapter = clientNetworkAdapter;
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
        }

        return result;
    }

    public void loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {

        customActionDescriptionMessage = null;

        try {
            // Load all classes from the jar where this plugin is located
            Plugin plugin = pluginHelper.loadJar(aiPluginPaths.get(agentSystem).getPath());


            //search for the first class that abides the contract that is not the interface itself
            for (Class c : plugin) {
                if (!IAgentSystemPluginDescriptor.class.equals(c)
                        && IAgentSystemPluginDescriptor.class.isAssignableFrom(c)) {

                    //save available AgentSystemDescriptor description and it's directory
                    loadedPlugin = (IAgentSystemPluginDescriptor) c.newInstance();
                    continue;
                }

                if (!IActionDescriptionMessage.class.equals(c)
                        && IActionDescriptionMessage.class.isAssignableFrom(c)) {

                    //examine constructors
                    customActionDescriptionMessage = pluginHelper.findSuitableConstructor(c, int.class, IActionDescription.class);

                    if (customActionDescriptionMessage == null) {
                        throw new PluginNotReadableException("A custom action description message was found, but didn't provide the needed constructor => (int clientId, A extends IActionDescription state).",
                                aiPluginPaths.get(agentSystem).getPath());
                    }
                }
            }


            if (loadedPlugin != null) {
                clientNetworkAdapter.setContextClassLoader(plugin.getClassLoader());
                usedClassLoader = plugin.getClassLoader();
            } else {
                throw new PluginNotReadableException("Plugin didn't provide a descriptor: ", aiPluginPaths.get(agentSystem).getPath());
            }

        } catch (InstantiationException e) {
            throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) + "' Reason: \n\n" + e);
        } catch (IllegalAccessException e) {
            throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) + "' Reason: \n\n" + e);
        }

    }

    @Override
    public ClassLoader getUsedClassLoader() {
        return  usedClassLoader;
    }

    @Override
    public IAgentSystem createAgentSystemInstance(IPluginServiceProvider serviceProvider) throws TechnicalException {
        return loadedPlugin.getInstance(serviceProvider);
    }

    public File getAgentSystemPluginPath(TAgentSystemDescription agentSystemDescription) throws TechnicalException, PluginNotReadableException {
        if (aiPluginPaths == null) {
            throw new UnsupportedOperationException("protocol violated: listAvailableAgentSystems must be called before this method.");
        }

        return aiPluginPaths.get(agentSystemDescription).getParentFile();
    }

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

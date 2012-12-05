package PluginLoader.Implementation;

import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.IEnvironmentStateMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.Service.IVisualizeReplay;
import NetworkAdapter.Messages.DefaultEnvironmentStateMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
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
 * This class implements all logic affiliated with the loading of environment plugins.
 */
public class EnvironmentPluginLoaderUseCase {

    private PluginHelper pluginHelper;
    private Map<TEnvironmentDescription, File> environmentPluginPaths;
    private Constructor customEnvironmentStateMessage;
    private Class customAbstractVisualization;
    private Class customInterfaceVisualization;
    private IEnvironmentPluginDescriptor loadedEnvironmentDescriptor = null;


    public EnvironmentPluginLoaderUseCase() throws TechnicalException, SettingException, PluginNotReadableException {
        pluginHelper = new PluginHelper();
        listAvailableEnvironments();
    }

    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException {
        pluginHelper = new PluginHelper();
        environmentPluginPaths = new Hashtable<TEnvironmentDescription, File>();

        List<TEnvironmentDescription> result = new LinkedList<TEnvironmentDescription>();
        try {
            // Load all plugins recursively from the directory specified in the config file.
            List<String> allJars = pluginHelper.findJarsRecursively(AppSettings.getString("environmentPluginsFolder"));

            TEnvironmentDescription tmp = null;
            for (String jarPath : allJars) {

                //look for the first class that abides the contract that is not the interface itself
                for (Class c : pluginHelper.listClassesFromJar(jarPath)) {
                    if (IEnvironmentPluginDescriptor.class != c
                            && IEnvironmentPluginDescriptor.class.isAssignableFrom(c)) {

                        //save available AgentSystemDescriptor description and it's path
                        tmp = ((IEnvironmentPluginDescriptor) c.newInstance()).getDescription();
                        result.add(tmp);
                        environmentPluginPaths.put(tmp, new File(jarPath));
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

    public IEnvironmentPluginDescriptor loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {

        customEnvironmentStateMessage = null;
        customAbstractVisualization = null;
        customInterfaceVisualization = null;

        try {
            // Load all classes from the jar where this plugin is located
            List<Class> classesInJar = pluginHelper.loadJar(environmentPluginPaths.get(environment).getPath());


            //search for the first class that abides the contract that is not the interface itself
            for (Class c : classesInJar) {
                if (!IEnvironmentPluginDescriptor.class.equals(c)
                        && IEnvironmentPluginDescriptor.class.isAssignableFrom(c)) {

                    //save available AgentSystemDescriptor description and it's directory
                    loadedEnvironmentDescriptor = (IEnvironmentPluginDescriptor) c.newInstance();
                    continue;
                }

                //now that this time the plugin is really loaded, get the types of all needed classes for use
                //in the factory methods
                if (!IEnvironmentStateMessage.class.equals(c)
                        && IEnvironmentStateMessage.class.isAssignableFrom(c)) {

                    customEnvironmentStateMessage = pluginHelper.findSuitableConstructor(c, int.class, IEnvironmentState.class);
                    if (customEnvironmentStateMessage == null) {
                        throw new PluginNotReadableException("A custom environment state message was found, but didn't provide the needed constructor => (int clientId, E extends IEnvironmentState state).", environmentPluginPaths.get(environment).getPath());
                    }
                    continue;
                }

                if (IVisualizeReplay.class.isAssignableFrom(c)
                        && !IVisualizeReplay.class.equals(c)
                        && !AbstractVisualizeReplayPanel.class.equals(c)) {

                    if (AbstractVisualizeReplayPanel.class.isAssignableFrom(c)) {
                        customAbstractVisualization = c;
                    } else {
                        customInterfaceVisualization = c;
                    }
                }
            }

        } catch (InstantiationException e) {
            throw new TechnicalException("Unable to load Class from '" + environmentPluginPaths.get(environment) + "' Reason: \n\n" + e);
        } catch (IllegalAccessException e) {
            throw new TechnicalException("Unable to load Class from '" + environmentPluginPaths.get(environment) + "' Reason: \n\n" + e);
        }

        if (loadedEnvironmentDescriptor != null) {
            return loadedEnvironmentDescriptor;
        } else {
            throw new PluginNotReadableException("plugin didn't provide a descriptor.", environmentPluginPaths.get(environment).getPath());
        }
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param environmentDescription a description of an existing environment != null
     * @return null, if environment was not found
     * @throws EnvironmentPluginAPI.Exceptions.TechnicalException
     *          if technical errors prevent the component from loading the plugin specified
     * @throws PluginLoader.Interface.Exceptions.PluginNotReadableException
     *          if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public File getEnvironmentPluginPath(TEnvironmentDescription environmentDescription) throws TechnicalException, PluginNotReadableException {
        if (environmentPluginPaths != null) {
            throw new UnsupportedOperationException("protocol violated: listAvailableEnvironments must be called before this method.");
        }

        return environmentPluginPaths.get(environmentDescription);
    }

    /**
     * Creates an environment state message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used.
     *
     * @param environmentState the environment state to send
     * @param targetClientId   the client instance the message is targeted to, != null
     * @return not null
     */
    public NetworkMessage createEnvironmentStateMessage(IEnvironmentState environmentState, int targetClientId) {
        NetworkMessage message;
        if (customEnvironmentStateMessage != null) {
            try { //TODO: needs better exception handling
                message = (NetworkMessage) customEnvironmentStateMessage.newInstance(targetClientId, environmentState);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        message = new DefaultEnvironmentStateMessage(targetClientId, environmentState);
        System.err.println("environment state class loader: " + environmentState.getClass().getClassLoader());
        return message;
    }

    public IVisualizeReplay getReplayVisualization() {

        try { //TODO: needs better exception handling
            if (customInterfaceVisualization != null) {
                return (IVisualizeReplay) customInterfaceVisualization.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public AbstractVisualizeReplayPanel getReplayVisualizationForSwing() {
        try { //TODO: needs better exception handling
            if (customAbstractVisualization != null) {
                return (AbstractVisualizeReplayPanel) customAbstractVisualization.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public IEnvironment createEnvironmentInstance(ICycleStatisticsSaver saveGameStatistics) throws TechnicalException {
        if (loadedEnvironmentDescriptor == null) {
            throw new UnsupportedOperationException("No plugin was loaded!");
        }

        return loadedEnvironmentDescriptor.getInstance(saveGameStatistics);
    }
}
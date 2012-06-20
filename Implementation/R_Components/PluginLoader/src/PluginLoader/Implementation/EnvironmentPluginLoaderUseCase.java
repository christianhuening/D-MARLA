package PluginLoader.Implementation;

import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.IEnvironmentStateMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Messages.DefaultActionDescriptionMessage;
import NetworkAdapter.Messages.DefaultEnvironmentStateMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;

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
    private IEnvironmentPluginDescriptor loadedEnvironmentDescriptor;
    private TEnvironmentDescription loadedEnvironment;
    private Constructor customActionDescriptionMessage;
    private Constructor customEnvironmentStateMessage;
    private Class customAbstractVisualization;
    private Class customInterfaceVisualization;


    public EnvironmentPluginLoaderUseCase() {
        pluginHelper = new PluginHelper();
    }

    public List<TEnvironmentDescription> listAvailableEnvironments(String environmentPluginDirectory) throws TechnicalException, PluginNotReadableException {
        environmentPluginPaths = new Hashtable<TEnvironmentDescription, File>();

        List<TEnvironmentDescription> result = new LinkedList<TEnvironmentDescription>();

        // Load all plugins recursively from the directory specified in the config file.
        List<String> allJars = pluginHelper.findJarsRecursively(environmentPluginDirectory);

        TEnvironmentDescription tmp = null;
        for (String jarPath : allJars) {
            try {
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
            } catch (InstantiationException e) {
                throw new TechnicalException("Unable to load Class from '" + jarPath + "' Reason: \n\n" + e);
            } catch (IllegalAccessException e) {
                throw new TechnicalException("Unable to load Class from '" + jarPath + "' Reason: \n\n" + e);
            }
        }

        return result;
    }

    public IEnvironmentPluginDescriptor loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
        if (environmentPluginPaths == null) {
            throw new UnsupportedOperationException("protocol violated: listAvailableEnvironments must be called before this method.");
        }

        if (loadedEnvironment == null || !loadedEnvironment.equals(environment)) {

            try {
                // Load all classes from the jar where this plugin is located
                List<Class> classesInJar = pluginHelper.listClassesFromJar(environmentPluginPaths.get(environment).getPath());
                pluginHelper.loadJar(environmentPluginPaths.get(environment).getPath());

                //search for the first class that abides the contract that is not the interface itself
                for (Class c : classesInJar) {
                    if (!IEnvironmentPluginDescriptor.class.equals(c)
                            && IEnvironmentPluginDescriptor.class.isAssignableFrom(c)) {

                        //save available AgentSystemDescriptor description and it's directory
                        loadedEnvironmentDescriptor = (IEnvironmentPluginDescriptor) c.newInstance();
                        loadedEnvironment = environment;
                        continue;

                    }

                    //now that this time the plugin is really loaded, get the types of all needed classes for use
                    //in the factory methods
                    if (!IEnvironmentStateMessage.class.equals(c)
                            && IEnvironmentStateMessage.class.isAssignableFrom(c)) {

                        customEnvironmentStateMessage = findSuitableConstructor(c, int.class, IEnvironmentState.class);
                        if (customEnvironmentStateMessage == null) {
                            throw new PluginNotReadableException("A custom environment state message was found, but didn't provide the needed constructor => (int clientId, E extends IEnvironmentState state).", environmentPluginPaths.get(environment).getPath());
                        }
                        continue;
                    }

                    if (!IActionDescriptionMessage.class.equals(c)
                            && IActionDescriptionMessage.class.isAssignableFrom(c)) {

                        //examine constructors
                        customActionDescriptionMessage = findSuitableConstructor(c, int.class, IActionDescription.class);

                        if (customActionDescriptionMessage == null) {
                            throw new PluginNotReadableException("A custom action description message was found, but didn't provide the needed constructor => (int clientId, A extends IActionDescription state).", environmentPluginPaths.get(environment).getPath());
                        }

                        continue;
                    }


                    if (!AbstractVisualizeReplayPanel.class.equals(c)
                            && AbstractVisualizeReplayPanel.class.isAssignableFrom(c)) {
                        customAbstractVisualization = c;
                        continue;
                    }

                    if (!IVisualizeReplay.class.equals(c)
                            && IVisualizeReplay.class.isAssignableFrom(c)) {
                        customInterfaceVisualization = c;
                        continue;
                    }
                }

            } catch (InstantiationException e) {
                throw new TechnicalException("Unable to load Class from '" + environmentPluginPaths.get(environment) + "' Reason: \n\n" + e);
            } catch (IllegalAccessException e) {
                throw new TechnicalException("Unable to load Class from '" + environmentPluginPaths.get(environment) + "' Reason: \n\n" + e);
            }
        }

        if (loadedEnvironmentDescriptor != null) {
            return loadedEnvironmentDescriptor;
        } else {
            throw new PluginNotReadableException("plugin didn't provide a descriptor.", environmentPluginPaths.get(environment).getPath());
        }
    }

    /**
     * Looks for a compatible constructors in a class object.
     * <br/><br/>
     * For the definition of compatible see isAssignable
     *
     * @param toExamine The class to look for the constructor in != null
     * @param typesToLookFor the
     * @return
     */
    private Constructor findSuitableConstructor(Class toExamine, Class... typesToLookFor) {
        System.out.println(toExamine);
        Class[] argTypes;

        for (Constructor ctor : toExamine.getConstructors()) {
            System.out.println(ctor);
            argTypes = ctor.getParameterTypes();

            if (isAssignable(argTypes, typesToLookFor)) {
                return ctor;
            }
        }

        return null;
    }

    /**
     * Determines if all types in argTypes are compatible to the classes in types.
     * <br/><br/>
     * Compatible means:<br/>
     * 1. Same length of list<br/>
     * 2. Each type in argTypes is the same type or a subtype of the class in the same index in types
     *
     * @param argTypes a list of argument types
     * @param types    the types to look for
     * @return see description
     */
    private boolean isAssignable(Class[] argTypes, Class... types) {
        if (argTypes.length != types.length) return false;

        for (int i = 0; i < argTypes.length; i++) {
            if (!types[i].isAssignableFrom(argTypes[i])) return false;
        }

        return true;
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param environmentDescription a description of an existing environment != null
     * @return null, if environment was not found
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
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
        if (customEnvironmentStateMessage != null) {
            try { //TODO: needs better exception handling
                return (NetworkMessage) customEnvironmentStateMessage.newInstance(targetClientId, environmentState);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return new DefaultEnvironmentStateMessage(targetClientId, environmentState);

    }

    /**
     * Creates an action description message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used. The message will be targeted to the server automatically
     *
     * @param actionDescription the action description to send
     * @return not null
     */
    public NetworkMessage createActionDescriptionMessage(IActionDescription actionDescription) {
        if (customActionDescriptionMessage != null) {

            try { //TODO: needs better exception handling
                return (NetworkMessage) customEnvironmentStateMessage.newInstance(0, actionDescription);
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

    public IVisualizeReplay getReplayVisualization() {
        try { //TODO: needs better exception handling
            return (IVisualizeReplay) customInterfaceVisualization.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public AbstractVisualizeReplayPanel getReplayVisualizationForSwing() {
        try { //TODO: needs better exception handling
            return (AbstractVisualizeReplayPanel) customAbstractVisualization.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
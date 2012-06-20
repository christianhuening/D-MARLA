package PluginLoader.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class implements all logic affiliated with the loading of agent system plugins.
 */
public class AgentSystemPluginLoaderUseCase {

    private Map<TAgentSystemDescription, File> aiPluginPaths;
    private PluginHelper pluginHelper;
    private TAgentSystemDescription loadedAgentSystem = null;
    private IAgentSystemPluginDescriptor loadedAgentSystemDescriptor = null;

    public AgentSystemPluginLoaderUseCase() {
        pluginHelper = new PluginHelper();
    }

    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins(String agentPluginDirectory) throws TechnicalException, PluginNotReadableException {
        aiPluginPaths = new Hashtable<TAgentSystemDescription, File>();

        List<TAgentSystemDescription> result = new LinkedList<TAgentSystemDescription>();

        // Load all plugins recursively from the directory specified in the config file.
        List<String> allJars = pluginHelper.findJarsRecursively(agentPluginDirectory);

        for (String jarPath : allJars) {
            try {
                //look for the first class that abides the contract that is not the interface itself
                for (Class c : pluginHelper.listClassesFromJar(jarPath)) {
                    if (IAgentSystemPluginDescriptor.class != c
                            && IAgentSystemPluginDescriptor.class.isAssignableFrom(c)) {

                        //save available AgentSystemDescriptor description and it's directory
                        TAgentSystemDescription tmp = ((IAgentSystemPluginDescriptor) c.newInstance()).getDescription();
                        result.add(tmp);
                        aiPluginPaths.put(tmp, new File(jarPath));
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

    public IAgentSystemPluginDescriptor loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {
        if (aiPluginPaths == null) {
            throw new UnsupportedOperationException("protocol violated: listAvailableAgentSystems must be called before this method.");
        }

        if (loadedAgentSystem == null || !loadedAgentSystem.equals(agentSystem)) {

            try {
                // Load all classes from the jar where this plugin is located
                List<Class> classesInJar = pluginHelper.listClassesFromJar(aiPluginPaths.get(agentSystem).getPath());
                pluginHelper.loadJar(aiPluginPaths.get(agentSystem).getPath());

                //search for the first class that abides the contract that is not the interface itself
                for (Class c : classesInJar) {
                    if (!IAgentSystemPluginDescriptor.class.equals(c)
                            && IAgentSystemPluginDescriptor.class.isAssignableFrom(c)) {

                        //save available AgentSystemDescriptor description and it's directory
                        loadedAgentSystemDescriptor = (IAgentSystemPluginDescriptor) c.newInstance();
                        loadedAgentSystem = agentSystem;
                        continue;

                    }
                }

            } catch (InstantiationException e) {
                throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) +"' Reason: \n\n" + e);
            } catch (IllegalAccessException e) {
                throw new TechnicalException("Unable to load Class from '" + aiPluginPaths.get(agentSystem) + "' Reason: \n\n" + e);
            }
        }

        if (loadedAgentSystemDescriptor != null) {
            return loadedAgentSystemDescriptor;
        } else {
            throw new PluginNotReadableException("plugin didn't provide a descriptor.", aiPluginPaths.get(agentSystem).getPath());
        }
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param agentSystemDescription a description of an existing agent system plugin != null
     * @return null, if agent system plugin was not found
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
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
}

import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import Logic.GridWorldConfiguration;
import Logic.GridWorldStyle;

import java.util.LinkedList;
import java.util.List;

/**
 * This is an example of how an environment plugin descriptor may look like.
 */
public class CliffEnvironmentPluginDescriptor implements IEnvironmentPluginDescriptor<GridWorldConfiguration> {
    @Override
    public TEnvironmentDescription getDescription() {
        return new TEnvironmentDescription("The Cliff", "v0.01", "A simple environment, illustrating the cliff environment" +
                " from the book by Sutton. See http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node1.html");
    }

    @Override
    public List<GridWorldConfiguration> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException {
        LinkedList<GridWorldConfiguration> configurations = new LinkedList<GridWorldConfiguration>();
        configurations.add(new GridWorldConfiguration(20, 8, GridWorldStyle.Cliff));
        configurations.add(new GridWorldConfiguration(20, 8, GridWorldStyle.Random));
        return configurations;
    }

    @Override
    public void saveConfiguration(GridWorldConfiguration configuration) throws TechnicalException {
        // not yet..
    }

    @Override
    public IEnvironment getInstance(ICycleStatisticsSaver cycleStatisticsSaver) throws TechnicalException {
        return new CliffEnvironmentPlugin(cycleStatisticsSaver);
    }
}

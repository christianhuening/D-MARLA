import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;

/**
 * This is an example of how an environment plugin descriptor may look like.
 */
public class CliffEnvironmentPluginDescriptor implements IEnvironmentPluginDescriptor {
    @Override
    public TEnvironmentDescription getDescription() {
        return new TEnvironmentDescription("The Cliff", "v0.01", "A simple environment, illustrating the cliff environment" +
                " from the book by Sutton. See http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node1.html");
    }

    @Override
    public IEnvironment getInstance(ISaveGameStatistics iSaveGameStatistics) throws TechnicalException {
        return new CliffEnvironmentPlugin();
    }
}

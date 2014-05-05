package Factory;


import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import Factory.GameLogic.GameLogicComponent;

import java.util.List;


public class FactoryPluginDescriptor implements IEnvironmentPluginDescriptor {

    @Override
    public TEnvironmentDescription getDescription() {
        return new TEnvironmentDescription("Factory","v0.3","For further information see: https://docs.google.com/document/d/1wehUEc_XivoXuG7OrfJ8LhgsDBYPH-xLT3JoBU5rFfA/edit");
    }

    @Override
    public List getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException {
        return null;
    }

    @Override
    public void saveConfiguration(IEnvironmentConfiguration iEnvironmentConfiguration) throws TechnicalException {

    }

    @Override
    public IEnvironment getInstance(ICycleStatisticsSaver iCycleStatisticsSaver) throws TechnicalException {
        return new GameLogicComponent(iCycleStatisticsSaver);
    }

}

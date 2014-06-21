package Factory;


import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.GameLogic.GameLogicComponent;

import java.util.LinkedList;
import java.util.List;


public class FactoryPluginDescriptor implements IEnvironmentPluginDescriptor<TMapMetaData> {

    @Override
    public TEnvironmentDescription getDescription() {
        return new TEnvironmentDescription("Factory","v0.3","For further information see: https://docs.google.com/document/d/1wehUEc_XivoXuG7OrfJ8LhgsDBYPH-xLT3JoBU5rFfA/edit");
    }

    @Override
    public List<TMapMetaData> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException {
        LinkedList<TMapMetaData> list = new LinkedList<TMapMetaData>();
        list.add(new TMapMetaData("32x32-45% Spawn",0,23452,32,45,2,4));
        list.add(new TMapMetaData("16x16-45% Spawn",0,23452,16,45,2,4));
        list.add(new TMapMetaData("8x8-45% Spawn",0,23452,8,45,2,4));
        return list;
    }

    @Override
    public void saveConfiguration(TMapMetaData tMapMetaData) throws TechnicalException {

    }

    @Override
    public IEnvironment getInstance(ICycleStatisticsSaver iCycleStatisticsSaver) throws TechnicalException {
        return new GameLogicComponent(iCycleStatisticsSaver);
    }

}

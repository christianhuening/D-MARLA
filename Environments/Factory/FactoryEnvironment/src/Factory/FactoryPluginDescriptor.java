package Factory;


import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import Factory.GameLogic.GameLogicComponent;



public class FactoryPluginDescriptor implements IEnvironmentPluginDescriptor {

    @Override
    public TEnvironmentDescription getDescription() {
        return new TEnvironmentDescription("Factory","v0.3","For further information see: https://docs.google.com/document/d/1wehUEc_XivoXuG7OrfJ8LhgsDBYPH-xLT3JoBU5rFfA/edit");
    }

    @Override
    public IEnvironment getInstance(ISaveGameStatistics gameStatisticSaver) throws TechnicalException {
        return new GameLogicComponent(gameStatisticSaver);
    }
}

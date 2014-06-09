import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import Factory.GameLogic.GameActors.GameReplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 28.05.12
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class DummyStatistics implements ICycleStatisticsSaver {
    List<GameReplay> replays = new ArrayList<GameReplay>();

    public void SaveReplay(GameReplay replay) {
        replays.add(replay);

    }

    public List<GameReplay> getReplays() {
        return replays;
    }

    @Override
    public void SaveReplay(ICycleReplay replay, TEnvironmentDescription environment) throws TechnicalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

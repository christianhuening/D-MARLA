package GameStatistics.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import GameStatistics.Implementation.Entities.ClientName;
import GameStatistics.Implementation.Entities.CycleReplayDescription;

import java.io.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 13.06.12
 * Time: 00:43
 * To change this template use File | Settings | File Templates.
 */
public class GameReplayDescriptionSaverHelper {
// ------------------------------ FIELDS ------------------------------

    private HashMap<TEnvironmentDescription, CycleReplayDescriptionDao> gameReplayDescriptionDaosForEnvironments;
    private HashMap<TEnvironmentDescription, ClientNameDao> playerNameDaosForEnvironments;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameReplayDescriptionSaverHelper() {
        this.gameReplayDescriptionDaosForEnvironments = new HashMap<TEnvironmentDescription, CycleReplayDescriptionDao>();
        this.playerNameDaosForEnvironments = new HashMap<TEnvironmentDescription, ClientNameDao>();
    }

// -------------------------- PUBLIC METHODS --------------------------

    public void saveReplay(CycleReplayDescription description, ICycleReplay replay, TEnvironmentDescription environment) {
        try {
            saveTGameReplayToFile(replay, description.getGameReplayFileLocation());

            // TODO: An interface method that should return a List<String> returns a List<Object> for some weird unknown reason...It's a bug Dave...
            for (Object player : replay.getPlayers()) {
                this.getPlayerNameDaoForEnvironment(environment).saveClientName((String) player);
            }

            this.getGameReplayDescriptionDaoForEnvironment(environment).create(description);
        } catch (TechnicalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void saveTGameReplayToFile(ICycleReplay replay, String fileLocation) throws TechnicalException {
        try {
            File file = new File(fileLocation);

            if (!file.exists()) {
                // create directory if the directory does not exist
                new File("./GameReplays/").mkdirs();
                file.createNewFile();
            }

            // Write to disk with FileOutputStream
            FileOutputStream f_out = null;

            f_out = new
                    FileOutputStream(file);

            // Write object with ObjectOutputStream
            ObjectOutputStream obj_out = null;

            obj_out = new
                    ObjectOutputStream(f_out);

            // Write object out to disk
            obj_out.writeObject(replay);

            obj_out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new TechnicalException("");
        } catch (IOException e) {
            e.printStackTrace();
            throw new TechnicalException("");
        }
    }

    CycleReplayDescriptionDao getGameReplayDescriptionDaoForEnvironment(TEnvironmentDescription environmentDescription) throws TechnicalException {
        if(!gameReplayDescriptionDaosForEnvironments.containsKey(environmentDescription)) {
            gameReplayDescriptionDaosForEnvironments.put(environmentDescription, new CycleReplayDescriptionDao(CycleReplayDescription.class, environmentDescription));
        }

        return gameReplayDescriptionDaosForEnvironments.get(environmentDescription);
    }

    ClientNameDao getPlayerNameDaoForEnvironment(TEnvironmentDescription environmentDescription) throws TechnicalException {
        if(!playerNameDaosForEnvironments.containsKey(environmentDescription)) {
            playerNameDaosForEnvironments.put(environmentDescription, new ClientNameDao(ClientName.class));
        }

        return playerNameDaosForEnvironments.get(environmentDescription);
    }
}

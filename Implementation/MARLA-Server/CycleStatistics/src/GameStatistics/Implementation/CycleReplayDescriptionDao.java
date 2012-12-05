package GameStatistics.Implementation;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import ZeroTypes.Exceptions.ErrorMessages;
import GameStatistics.Implementation.Entities.CycleReplayDescription;
import PersistenceManager.Interface.AbstractDao;

import javax.persistence.TypedQuery;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Administrates game replays in the database.
 */
public class CycleReplayDescriptionDao extends AbstractDao<CycleReplayDescription> {
    private String environmentDescription;

// --------------------------- CONSTRUCTORS ---------------------------

    public CycleReplayDescriptionDao(Class<CycleReplayDescription> entityClass, TEnvironmentDescription environmentDescription) throws TechnicalException {
        this(entityClass, environmentDescription, false);
    }

    public CycleReplayDescriptionDao(Class<CycleReplayDescription> entityClass, TEnvironmentDescription environmentDescription, boolean isUnitTest) throws TechnicalException {
        super(entityClass, isUnitTest);
        this.environmentDescription = environmentDescription.toString();
    }

// -------------------------- PUBLIC METHODS --------------------------

    @Override
    public void create(CycleReplayDescription description) throws TechnicalException {
        super.create(description);
    }

    @Override
    public void edit(CycleReplayDescription description) throws TechnicalException {
        super.edit(description);
    }

    public CycleReplayDescription getGameReplayDescriptionWithGameReplayId(UUID id) throws TechnicalException {
        synchronized (daoLock) {
        TypedQuery<CycleReplayDescription> query = getEntityManager().createQuery(
                "SELECT g FROM CycleReplayDescription g WHERE g.replayID = :gameReplayID AND g.environmentDescription = :environment", CycleReplayDescription.class).setParameter("gameReplayID", id).setParameter("environment", environmentDescription);

        List<CycleReplayDescription> result = query.getResultList();

        if(result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
        }
    }

    public List<CycleReplayDescription> getGameReplaysFromToTime(Date from, Date to) throws TechnicalException {
        synchronized (daoLock) {
            TypedQuery<CycleReplayDescription> query = getEntityManager().createQuery(
                    "SELECT g FROM CycleReplayDescription g WHERE g.replayDate >= :startingTime AND g.replayDate <= :endingTime AND g.environmentDescription = :environment", CycleReplayDescription.class).setParameter("startingTime", from).setParameter("endingTime", to).setParameter("environment", this.environmentDescription);

            return query.getResultList();
        }
    }

    @Override
    public void remove(CycleReplayDescription description) throws TechnicalException {
        super.remove(description);

        File replayFile = new File(description.getGameReplayFileLocation());

        if (replayFile.exists()) {
            if (replayFile.canWrite()) {
                boolean success = replayFile.delete();
                if (!success) {
                    throw new TechnicalException(ErrorMessages.get("deleteReplayFile"));
                }
            } else {
                throw new TechnicalException(ErrorMessages.get("replayFileNotWritable"));
            }
        } else {
            System.err.println(ErrorMessages.get("replayFileNotFound"));
            //throw new TechnicalException(ErrorMessages.get("replayFileNotFound") + description.getGameReplayFileLocation());
        }
    }

// -------------------------- PRIVATE METHODS --------------------------
}

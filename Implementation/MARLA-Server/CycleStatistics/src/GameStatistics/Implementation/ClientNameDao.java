package GameStatistics.Implementation;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import GameStatistics.Implementation.Entities.ClientName;
import PersistenceManager.Interface.AbstractDao;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 27.05.12
 * Time: 22:40
 * To change this template use File | Settings | File Templates.
 */
public class ClientNameDao extends AbstractDao<ClientName> {
    /**
     * Initializes a new Abstract Dao. An abstract dao is responsible for providing administration functionality for one
     * entity class. There may be more than one Abstract Dao for an entity. All Daos for a certain entity type will share
     * the same EntityManager.
     *
     * @param entityClass The type of the entity, this dao will be responsible for.
     */
    public ClientNameDao(Class<ClientName> entityClass) throws TechnicalException {
        super(entityClass);
    }

    public ClientNameDao(Class<ClientName> entityClass, boolean isUnitTest) throws TechnicalException {
        super(entityClass, isUnitTest);
    }

    void saveClientName(String playerName) throws TechnicalException {
        synchronized (daoLock) {
            Session session = getEntityManager().unwrap(Session.class);

            String hql = "select p from ClientName p where p.name = '" + playerName + "'";
            Query query = session.createQuery(hql);
            List<ClientName> results = query.list();

            if (results.size() == 0) {
                this.create(new ClientName(playerName));
            }
        }
    }

    List<String> getAllClientNames() throws TechnicalException {
        synchronized (daoLock) {
            Session session = getEntityManager().unwrap(Session.class);

            String hql = "select p.name from ClientName p";
            Query query = session.createQuery(hql);


            List<String> results = query.list();

            return results;
        }
    }
}

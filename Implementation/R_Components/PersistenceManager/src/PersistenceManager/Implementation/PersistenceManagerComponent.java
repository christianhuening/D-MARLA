package PersistenceManager.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import PersistenceManager.Interface.IPersistenceManager;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 12.05.12
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceManagerComponent implements IPersistenceManager {

    private static PersistenceManagerUseCase _persistenceManagerUseCase;

    public static EntityManager getEntityManager(Class entityClass) throws TechnicalException {
        return PersistenceManagerUseCase.getEntityManager(entityClass);
    }

    public static EntityManager getEntityManagerForUnitTesting(Class entityClass) throws TechnicalException {
        return PersistenceManagerUseCase.getEntityManager(entityClass, true);
    }
}

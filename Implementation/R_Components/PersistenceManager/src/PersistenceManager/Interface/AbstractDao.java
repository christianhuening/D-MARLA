package PersistenceManager.Interface;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import PersistenceManager.Implementation.PersistenceManagerComponent;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 13.05.12
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDao<T> {
// ------------------------------ FIELDS ------------------------------
    protected static Object daoLock = new Object();

    protected boolean isUnitTest;

    protected Class<T> entityClass;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Initializes a new Abstract Dao. An abstract dao is responsible for providing administration functionality for one
     * entity class. There may be more than one Abstract Dao for an entity. All Daos for a certain entity type will share
     * the same EntityManager.
     *
     * @param entityClass The type of the entity, this dao will be responsible for.
     */
    public AbstractDao(Class<T> entityClass) throws TechnicalException {
        this(entityClass, false);
    }

    /**
     * Initializes a new Abstract Dao. An abstract dao is responsible for providing administration functionality for one
     * entity class. There may be more than one Abstract Dao for an entity. All Daos for a certain entity type will share
     * the same EntityManager.
     *
     * @param entityClass The type of the entity, this dao will be responsible for.
     * @param isUnitTest  Indicates if the entitymanager should use the database for unit tests.
     */
    public AbstractDao(Class<T> entityClass, boolean isUnitTest) throws TechnicalException {
        this.isUnitTest = isUnitTest;
        this.entityClass = entityClass;

        getEntityManager();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    // private EntityManager entityManager;

    /**
     * Provides access to the Entity Manager for the entityClass this dao is responsible for.
     * All daos that have the same entityClass also share the same EntityManager.
     *
     * @return The EntityManager used for connecting to the database.
     */
    protected synchronized EntityManager getEntityManager() throws TechnicalException {
        EntityManager entityManager;

        if (isUnitTest) {
            entityManager = PersistenceManagerComponent.getEntityManagerForUnitTesting(entityClass);
        } else {
            entityManager = PersistenceManagerComponent.getEntityManager(entityClass);
        }

        return entityManager;
    }

// -------------------------- PUBLIC METHODS --------------------------

    /**
     * Returns the number of entities contained in the database.
     *
     * @return The number of entities that are contained in the database.
     */
    public int count() throws TechnicalException {
        synchronized (daoLock) {
            getEntityManager().getTransaction().begin();

            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(rt));
            javax.persistence.Query q = getEntityManager().createQuery(cq);

            getEntityManager().getTransaction().commit();

            return ((Long) q.getSingleResult()).intValue();
        }
    }

    /**
     * Saves the given entity in the database.
     *
     * @param entity The entity that will be saved.
     */
    public void create(T entity) throws TechnicalException {
        synchronized (daoLock) {
            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            entityManager.persist(entity);

            entityManager.getTransaction().commit();
        }
    }

    /**
     * Updates the given entity in the database.
     *
     * @param entity
     */
    public void edit(T entity) throws TechnicalException {
        synchronized (daoLock) {

            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            entityManager.merge(entity);

            entityManager.getTransaction().commit();

        }
    }

    /**
     * Executes a typed query on the database.
     *
     * @param query The query that will be executed.
     * @return The results-list of the query.
     */
    public List<T> executeQuery(TypedQuery<T> query) {
        synchronized (daoLock) {

            return query.getResultList();

        }
    }

    /**
     * Executes a typed query on the database.
     *
     * @param queryString The query that will be executed.
     * @return The results-list of the query.
     */
    public List<T> executeQuery(String queryString) throws TechnicalException {
        synchronized (daoLock) {

            TypedQuery<T> query = getEntityManager().createQuery(
                    queryString, entityClass);

            return query.getResultList();
        }
    }

    /**
     * Finds and returns a entity by its id.
     *
     * @param id The id of the entity that should be searched for.
     * @return The entity, if one with the given id was found, otherwise null.
     */
    public T find(Object id) throws TechnicalException {
        synchronized (daoLock) {

            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            T foundEntity = entityManager.find(entityClass, id);

            entityManager.getTransaction().commit();

            return foundEntity;
        }
    }

    /**
     * Finds all entites of T that are contained in the database.
     *
     * @return A list of all entities of T.
     */
    public List<T> findAll() throws TechnicalException {
        synchronized (daoLock) {

            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            javax.persistence.criteria.CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            List<T> foundEntities = entityManager.createQuery(cq).getResultList();

            entityManager.getTransaction().commit();

            return foundEntities;
        }
    }

    public List<T> findRange(int[] range) throws TechnicalException {
        synchronized (daoLock) {

            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            javax.persistence.criteria.CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = entityManager.createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);

            entityManager.getTransaction().commit();

            return q.getResultList();
        }
    }

    /**
     * Gets a criteria builder for this abstract Dao.
     *
     * @return
     */
    public CriteriaBuilder getCriteriaBuilder() throws TechnicalException {
        synchronized (daoLock) {

            return getEntityManager().getCriteriaBuilder();

        }
    }

    /**
     * Removes the given entity from the database.
     *
     * @param entity The entity that will be removed.
     */
    public void remove(T entity) throws TechnicalException {
        synchronized (daoLock) {

            EntityManager entityManager = getEntityManager();

            entityManager.getTransaction().begin();

            entityManager.remove(getEntityManager().merge(entity));

            entityManager.getTransaction().commit();
        }
    }
}

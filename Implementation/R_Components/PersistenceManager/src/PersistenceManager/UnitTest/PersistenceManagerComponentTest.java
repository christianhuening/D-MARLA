import EnvironmentPluginAPI.Exceptions.TechnicalException;
import PersistenceManager.Implementation.Entities.TestEntity;
import PersistenceManager.Interface.AbstractDao;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 13.05.12
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceManagerComponentTest {
    private TestDao testDao;

    private TestEntity testEntity1;
    private TestEntity testEntity2;

    @Before
    public void TestSetup() throws TechnicalException {
        testDao = new TestDao(TestEntity.class);

        testEntity1 = new TestEntity(1);
        testEntity2 = new TestEntity(2);
    }

    @After
    public void TestTearDown() throws TechnicalException {
        for (TestEntity entity : testDao.findAll())
            testDao.remove(entity);
    }

    @Test
    public void CreateFindTestEntityTest() throws TechnicalException {
        testDao.create(testEntity1);
        testDao.create(testEntity2);

        TestEntity foundEntity1 = testDao.find(testEntity1.Id);
        TestEntity foundEntity2 = testDao.find(testEntity2.Id);

        Assert.assertTrue(foundEntity1.equals(testEntity1));
        Assert.assertTrue(foundEntity2.equals(testEntity2));
    }

    @Test
    public void EditTestEntityTest() throws TechnicalException {
        testDao.create(testEntity1);

        testEntity1.testValue1 = 2;

        testDao.edit(testEntity1);

        Assert.assertTrue(testDao.find(testEntity1.Id).testValue1 == 2);
    }

    @Test
    public void RemoveTestEntityTest() throws TechnicalException {
        testDao.create(testEntity1);

        testDao.remove(testEntity1);

        Assert.assertTrue(testDao.find(testEntity1.Id) == null);
    }

    class TestDao extends AbstractDao<TestEntity> {
        public TestDao(Class<TestEntity> entityClass) throws TechnicalException {
            super(entityClass, true);
        }
    }
}

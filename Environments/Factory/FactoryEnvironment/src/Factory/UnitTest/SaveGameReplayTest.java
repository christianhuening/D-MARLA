//import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
//import Factory.GameLogic.GameActors.GameReplay;
//import PersistenceManager.Interface.AbstractDao;
//
///**
//* Created with IntelliJ IDEA.
//* User: N3trunner
//* Date: 30.05.12
//* Time: 15:40
//* To change this template use File | Settings | File Templates.
//*/
//public class SaveGameReplayTest {
//
//    /*private static TestGameReplayDao testDao;
//
//    @Before
//    public void InitialSetup() throws TechnicalException {
//        testDao = new TestGameReplayDao(GameReplay.class);
//    }
//
//    @After
//    public void tearDown() throws TechnicalException {
//        for (GameReplay replay : testDao.findAll()) {
//            testDao.remove(replay);
//        }
//    }
//
//    @Test
//    public void TestSaveGameReplay() throws TechnicalException {
//        GameReplay replay = new GameReplay(DateTime.now(), new ArrayList<String>(), "", 0, new ArrayList<TGameState>());
//
//        testDao.create(replay);
//    }*/
//}
//
//class TestGameReplayDao extends AbstractDao<GameReplay> {
//
//    /**
//     * Initializes a new Abstract Dao. An abstract dao is responsible for providing administration functionality for one
//     * entity class. There may be more than one Abstract Dao for an entity. All Daos for a certain entity type will share
//     * the same EntityManager.
//     *
//     * @param entityClass The type of the entity, this dao will be responsible for.
//     */
//    public TestGameReplayDao(Class<GameReplay> entityClass) throws TechnicalException {
//        super(entityClass, true);
//    }
//}

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import ZeroTypes.Exceptions.GameReplayNotContainedInDatabaseException;
import GameStatistics.Implementation.ClientNameDao;
import GameStatistics.Implementation.CycleReplayDescriptionDao;
import GameStatistics.Implementation.CycleStatisticsComponent;
import GameStatistics.Implementation.Entities.ClientName;
import GameStatistics.Implementation.Entities.CycleReplayDescription;
import GameStatistics.Implementation.GameReplayDescriptionSaverHelper;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ZeroTypes.TransportTypes.TCycleReplayDescription;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 13.05.12
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class GameStatisticsComponentTest {
    // Test Data parameters
    private static final int MinimumNumberOfTurns = 10;
    private static final int MaximumNumberOfTurns = 1000;
    private static final int NumberOfPlayers = 4;
    private static final int NumberOfReplays = 10;
    // The time when the first CycleReplayDescription happened.
    // The time of the following GameReplays will be incremented by 1 minute for each new game replay.
    private Calendar startingDateTime;
    private static final TEnvironmentDescription ENVIRONMENT_DESCRIPTION = new TEnvironmentDescription("test", "test", "test");

    static CycleStatisticsComponent gameStatisticsComponent;
    static CycleReplayDescriptionDao cycleDescriptionDao;
    static ClientNameDao clientNameDao;

    static GameReplayDescriptionSaverHelper gameReplayDescriptionSaverHelper;

    static ICycleStatistics iCycleStatistics;
    static ICycleStatisticsSaver iCycleStatisticsSaver;

    List<String> testPlayers;
    List<playerCombination> allPossiblePlayerCombinations;
    List<ICycleReplay> testGameReplays;
    List<CycleReplayDescription> testReplayDescriptions;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public GameStatisticsComponentTest() {
        startingDateTime = startingDateTime();
    }


    @BeforeClass
    public static void testClassSetup() {

    }

    private static Calendar startingDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 1);

        return calendar;
    }

    @Before
    public void testSetup() throws TechnicalException {

        cycleDescriptionDao = new CycleReplayDescriptionDao(CycleReplayDescription.class, ENVIRONMENT_DESCRIPTION, true);
        clientNameDao = new ClientNameDao(ClientName.class, true);

        gameReplayDescriptionSaverHelper = new GameReplayDescriptionSaverHelper();

        // Cleanup
        for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
            try {
                cycleDescriptionDao.remove(replay);
            } catch (TechnicalException e) {
                e.printStackTrace();
            }
        }

        for (ClientName name : clientNameDao.findAll()) {
            clientNameDao.remove(name);
        }

        gameStatisticsComponent = new CycleStatisticsComponent();

        iCycleStatistics = gameStatisticsComponent;
        iCycleStatisticsSaver = gameStatisticsComponent;

        // Setup the test data.

        // Setup the players
        testPlayers = new ArrayList<String>();
        for (int i = 0; i < NumberOfPlayers; i++) {
            testPlayers.add("Player" + i);
        }

        //Get all possible combinations of test players
        allPossiblePlayerCombinations = new ArrayList<playerCombination>();

        for (String player : testPlayers) {
            for (String opponent : testPlayers) {
                if (!player.equals(opponent)) {
                    allPossiblePlayerCombinations.add(new playerCombination(player, opponent));
                    allPossiblePlayerCombinations.add(new playerCombination(opponent, player));
                }
            }
        }

        // Create game replays, and save them in the database
        testReplayDescriptions = new LinkedList<CycleReplayDescription>();
        testGameReplays = new LinkedList<ICycleReplay>();
        Random random = new Random();

        for (int i = 0; i < NumberOfReplays; i++) {
            String player1;
            String player2;

            // Choose two different players to participate in the replay
            int randomIndex1 = 0;
            int randomIndex2 = 0;

            while (randomIndex1 == randomIndex2) {
                randomIndex1 = random.nextInt(testPlayers.size());
                randomIndex2 = random.nextInt(testPlayers.size());
            }

            player1 = testPlayers.get(randomIndex1);
            player2 = testPlayers.get(randomIndex2);

            List<String> playersInReplay = new ArrayList<String>();
            playersInReplay.add(player1);
            playersInReplay.add(player2);

            // Choose a random winning player
            String winningPlayer = playersInReplay.get(random.nextInt(playersInReplay.size()));

            // Choose a random number of turns
            int numberOfTurns = random.nextInt(MaximumNumberOfTurns - MinimumNumberOfTurns) + MinimumNumberOfTurns;

            Calendar tmp = startingDateTime();
            tmp.add(Calendar.MINUTE, -i);
            ICycleReplay replay = new TestReplay(UUID.randomUUID(), tmp.getTime(), playersInReplay, winningPlayer, numberOfTurns, new LinkedList<IEnvironmentState>());

            tmp = startingDateTime();
            tmp.add(Calendar.MINUTE, -i);
            CycleReplayDescription description = new CycleReplayDescription(replay.getReplayId(), tmp.getTime(), playersInReplay, winningPlayer, numberOfTurns, ENVIRONMENT_DESCRIPTION);

            // Create the GameStatistics.GameLogic.Entities.TestReplay and add it to the list.
            testReplayDescriptions.add(description);
            testGameReplays.add(replay);
        }

        // Save the created game replays in the database
        for (int i = 0; i < testReplayDescriptions.size(); i++) {
            gameReplayDescriptionSaverHelper.saveReplay(testReplayDescriptions.get(i), testGameReplays.get(i), ENVIRONMENT_DESCRIPTION);
        }
    }

    @After
    public void testTearDown() throws TechnicalException {
        // Remove all replays from the database.
        // Note that some tests create their own replays, that are not contained in the test replays.
        // Some replay descriptions have no corresponding game replays on the harddrive.
        // This may cause some "Cannot find the specified replay file at: " warnings on the console.
        // These warnings can be savely ignored.
        for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
            try {
                cycleDescriptionDao.remove(replay);
            } catch (TechnicalException e) {
                e.printStackTrace();
            }
        }

        for (ClientName name : clientNameDao.findAll()) {
            clientNameDao.remove(name);
        }
    }

    @Test
    public void getGameReplayTest() throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException {

        Assert.assertEquals(testReplayDescriptions.size(), cycleDescriptionDao.findAll().size());

        List<CycleReplayDescription> descriptions = cycleDescriptionDao.findAll();

        List<CycleReplayDescription> expectedDescriptions = new ArrayList<CycleReplayDescription>(testReplayDescriptions);

        for (CycleReplayDescription description : cycleDescriptionDao.findAll()) {
            expectedDescriptions.remove(description);
        }

        Assert.assertTrue(expectedDescriptions.size() == 0);

        /*for (CycleReplayDescription expected : testReplayDescriptions) {
            boolean matchingReplayFound = false;
            for (CycleReplayDescription actual : cycleDescriptionDao.findAll()) {
                if (expected.equals(actual)) {
                    matchingReplayFound = true;

                    Assert.assertEquals(expected, actual);
                    Assert.assertEquals(expected.getGameReplay(), actual.getGameReplay());

                    break;
                }
            }
            Assert.assertTrue(matchingReplayFound);
        }*/
    }

    @Test
    public void getGameReplayTestNegative() throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException {
        // Try to find a GameStatistics.GameLogic.Entities.TestReplay with a non existent id.
        // A GameReplayNotContainedInDatabaseException should be thrown.
        exception.expect(GameReplayNotContainedInDatabaseException.class);

        iCycleStatistics.getCycleReplay(UUID.randomUUID(), ENVIRONMENT_DESCRIPTION);
    }

    @Test
    public void getWinLooseRatioTest() throws RemoteException, TechnicalException {
        for (playerCombination combination : allPossiblePlayerCombinations) {

            float wins = 0;
            float totalGames = 0;

            for (CycleReplayDescription replay : testReplayDescriptions) {
                if (replay.getPlayers().contains(combination.getPlayer()) && replay.getPlayers().contains(combination.getOpponent())) {
                    totalGames++;

                    if (replay.getWinningPlayer().equals(combination.player)) {
                        wins++;
                    }
                }
            }

            float expectedWinLossRatio;

            if (wins != 0 && totalGames != 0) {
                expectedWinLossRatio = wins / totalGames;
            } else {
                expectedWinLossRatio = 0;
            }

            float returnedWinLossRatio = iCycleStatistics.getWinLoseRatio(combination.getPlayer(), combination.getOpponent(), ENVIRONMENT_DESCRIPTION);

            Assert.assertTrue(Float.compare(expectedWinLossRatio, returnedWinLossRatio) == 0);
        }
    }

    @Test
    public void getGameReplayDescriptionsByDeltaTimeTest() throws RemoteException, TechnicalException {
        // Test if all descriptions of all test replays are returned properly.

        // Test if all descriptions are returned, if providing a time frame that covers all of them.
        Calendar tmp = startingDateTime();
        tmp.add(Calendar.MINUTE, testReplayDescriptions.size());
        List<TCycleReplayDescription> allReplays = iCycleStatistics.getCycleReplayDescriptionsByDeltaTime(startingDateTime.getTime(), tmp.getTime(), ENVIRONMENT_DESCRIPTION);
        Assert.assertTrue("checkGameDescriptions", checkGameDescriptions(allReplays, testReplayDescriptions));

        // Test if no descriptions are returned, if providing a time frame that covers none of them.
        tmp = startingDateTime();
        tmp.add(Calendar.MINUTE, testReplayDescriptions.size());
        Calendar tmp2 = startingDateTime();
        tmp.add(Calendar.MINUTE, -1);
        List<TCycleReplayDescription> descriptionRange1 = iCycleStatistics.getCycleReplayDescriptionsByDeltaTime(tmp.getTime(), tmp2.getTime(), ENVIRONMENT_DESCRIPTION);
        Assert.assertTrue("descriptionRange1", descriptionRange1.size() == 0);

        tmp = startingDateTime();
        tmp.add(Calendar.MINUTE, testReplayDescriptions.size() + 1);
        tmp2 = startingDateTime();
        tmp2.add(Calendar.MINUTE, testReplayDescriptions.size() + 2);
        List<TCycleReplayDescription> descriptionRange2 = iCycleStatistics.getCycleReplayDescriptionsByDeltaTime(tmp.getTime(), tmp2.getTime(), ENVIRONMENT_DESCRIPTION);
        Assert.assertTrue("descriptionRange2", descriptionRange2.size() == 0);

        // Test if single description is returned, if providing a timeframe that only covers one replay.
//        List<TCycleReplayDescription> descriptionRange3 = iCycleStatistics.getCycleReplayDescriptionsByDeltaTime(startingDateTime.getTime(), startingDateTime.getTime(), ENVIRONMENT_DESCRIPTION);
//        Assert.assertTrue("descriptionRange3 size wrong, was: " + descriptionRange3.size(), descriptionRange3.size() == 1);
//        Assert.assertTrue("descriptionRange3 not equal", descriptionRange3.get(0).getReplayDate().equals(startingDateTime));
    }

    @Test
    public void getCurrentGamesPerMinuteTest() throws RemoteException, TechnicalException {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -61);
        cycleDescriptionDao.create(new CycleReplayDescription(UUID.randomUUID(), calendar.getTime(), new ArrayList<String>(), "", 0, ENVIRONMENT_DESCRIPTION));

        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -55);
        cycleDescriptionDao.create(new CycleReplayDescription(UUID.randomUUID(), calendar.getTime(), new ArrayList<String>(), "", 0, ENVIRONMENT_DESCRIPTION));

        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -45);
        cycleDescriptionDao.create(new CycleReplayDescription(UUID.randomUUID(), calendar.getTime(), new ArrayList<String>(), "", 0, ENVIRONMENT_DESCRIPTION));

        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -30);
        cycleDescriptionDao.create(new CycleReplayDescription(UUID.randomUUID(), calendar.getTime(), new ArrayList<String>(), "", 0, ENVIRONMENT_DESCRIPTION));

        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -1);
        cycleDescriptionDao.create(new CycleReplayDescription(UUID.randomUUID(), calendar.getTime(), new ArrayList<String>(), "", 0, ENVIRONMENT_DESCRIPTION));

        float expectedGamesPerMinute = 4.0f;
        float returnedGamesPerMinute = iCycleStatistics.getCurrentGamesPerMinute(ENVIRONMENT_DESCRIPTION);

        Assert.assertTrue(Float.compare(returnedGamesPerMinute, expectedGamesPerMinute) == 0);
    }

    @Test
    public void getTotalNumberOfPlayedGamesTest() throws RemoteException, TechnicalException {
        HashMap<String, Integer> numberOfGamesPerPlayer = new HashMap<String, Integer>();

        // Generate the expected number of games values for each player.
        for (String player : testPlayers) {
            int numberOfGamesForPlayer = 0;

            for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
                if (replay.getPlayers().contains(player)) {
                    numberOfGamesForPlayer++;
                }
            }

            numberOfGamesPerPlayer.put(player, numberOfGamesForPlayer);
        }

        // Test if the expected values match the values returned from the method.
        for (String player : testPlayers) {
            Assert.assertTrue(iCycleStatistics.getTotalNumberOfCycles(player, ENVIRONMENT_DESCRIPTION) == numberOfGamesPerPlayer.get(player));
        }
    }

    @Test
    public void getTotalNumberOfGamesWonTest() throws RemoteException, TechnicalException {
        HashMap<String, Integer> numberOfGamesWonPerPlayer = new HashMap<String, Integer>();

        // Generate the expected number of games values for each player.
        for (String player : testPlayers) {
            int numberOfGamesWonForPlayer = 0;

            for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
                if (replay.getPlayers().contains(player) && replay.getWinningPlayer().equals(player)) {
                    numberOfGamesWonForPlayer++;
                }
            }

            numberOfGamesWonPerPlayer.put(player, numberOfGamesWonForPlayer);
        }

        // Test if the expected values match the values returned from the method.
        for (String player : testPlayers) {
            Assert.assertTrue(iCycleStatistics.getTotalNumberOfCyclesWon(player, ENVIRONMENT_DESCRIPTION) == numberOfGamesWonPerPlayer.get(player));
        }
    }

    @Test
    public void getTotalNumberOfGamesLostTest() throws RemoteException, TechnicalException {
        HashMap<String, Integer> numberOfGamesLostPerPlayer = new HashMap<String, Integer>();

        // Generate the expected number of games values for each player.
        for (String player : testPlayers) {
            int numberOfGamesLostForPlayer = 0;

            for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
                if (replay.getPlayers().contains(player) && !replay.getWinningPlayer().equals(player)) {
                    numberOfGamesLostForPlayer++;
                }
            }

            numberOfGamesLostPerPlayer.put(player, numberOfGamesLostForPlayer);
        }

        // Test if the expected values match the values returned from the method.
        for (String player : testPlayers) {
            Assert.assertTrue(iCycleStatistics.getTotalNumberOfCyclesLost(player, ENVIRONMENT_DESCRIPTION) == numberOfGamesLostPerPlayer.get(player));
        }
    }

    @Test
    public void getAverageTurnsPerGameTest() throws RemoteException, TechnicalException {
        // The maximum number of games that will be taken into account for the numberOfLastGames parameter
        int maximumNumberOfGamesChecked = testReplayDescriptions.size() + 1;

        for (String player : testPlayers) {
            Assert.assertTrue(iCycleStatistics.getAverageTurnsPerCycle(player, 0, ENVIRONMENT_DESCRIPTION) == 0);
        }

        // Test the method for all players for all values of numberOfLastGames up to maximumNumberOfGamesChecked
        for (int numberOfGamesChecked = 1; numberOfGamesChecked <= maximumNumberOfGamesChecked; numberOfGamesChecked++) {
            for (String player : testPlayers) {
                float numberOfGamesForPlayer = 0;
                float numberOfTurnsForPlayer = 0;
                int numberOfGamesFound = 0;

                for (CycleReplayDescription replay : cycleDescriptionDao.findAll()) {
                    if (replay.getPlayers().contains(player)) {
                        numberOfGamesForPlayer++;
                        numberOfTurnsForPlayer += replay.getNumberOfTurns();
                        numberOfGamesFound++;

                        if (numberOfGamesFound >= numberOfGamesChecked) {
                            break;
                        }
                    }
                }

                float expectedValue;

                if (numberOfGamesForPlayer != 0 && numberOfTurnsForPlayer != 0) {
                    expectedValue = numberOfTurnsForPlayer / numberOfGamesForPlayer;
                } else {
                    expectedValue = 0;
                }

                Assert.assertTrue(Float.compare(iCycleStatistics.getAverageTurnsPerCycle(player, numberOfGamesChecked, ENVIRONMENT_DESCRIPTION), expectedValue) == 0);
            }
        }
    }

    @Test
    public void saveReplayTest() throws RemoteException, TechnicalException {
        List<String> players = new ArrayList<String>();

        players.add("Batman");
        players.add("Superman");

        ICycleReplay testReplay = new TestReplay(UUID.randomUUID(), Calendar.getInstance().getTime(), new ArrayList<String>(), "Batman", 100000, new LinkedList<IEnvironmentState>());

        CycleReplayDescription testDescription = new CycleReplayDescription(UUID.randomUUID(), testReplay.getReplayDate(), new ArrayList<String>(), "Batman", 100000, ENVIRONMENT_DESCRIPTION);

        iCycleStatisticsSaver.SaveReplay(testReplay, ENVIRONMENT_DESCRIPTION);

        for (CycleReplayDescription description : cycleDescriptionDao.findAll()) {
            if (description.equals(testDescription)) {
                // Assert that an equal replay is found.
                Assert.assertTrue(true);
                break;
            }
        }
    }

    @Test
    public void getAllPlayerNamesTest() throws RemoteException, TechnicalException {
        List<String> expected = testPlayers;

        List<String> actual = iCycleStatistics.getClientNames(ENVIRONMENT_DESCRIPTION);

        Assert.assertEquals(expected.size(), actual.size());

        for (String s : expected) {
            Assert.assertTrue(actual.contains(s));
        }

        for (CycleReplayDescription a : cycleDescriptionDao.findAll()) {
            cycleDescriptionDao.remove(a);
        }

        iCycleStatistics.getClientNames(ENVIRONMENT_DESCRIPTION);
    }

    private boolean checkGameDescriptions(List<TCycleReplayDescription> descriptions, List<CycleReplayDescription> replays) {
        /*if (descriptions.size() != replays.size()) {
            return false;
        }

        // Extract all replays from the descriptions
        List<CycleReplayDescription> replaysInDescription = new ArrayList<CycleReplayDescription>();

        for (TGameDescription description : descriptions) {
            replaysInDescription.add(new CycleReplayDescription(description.get_replay()));
        }

        // Check if all wanted replays are contained in the descriptions
        for (CycleReplayDescription replay : replays) {
            if (!replaysInDescription.contains(replay)) {
                return false;
            }
        }*/

        return true;
    }

    private class playerCombination {
        private String player;
        private String opponent;

        public String getPlayer() {
            return player;
        }

        public String getOpponent() {
            return opponent;
        }

        public playerCombination(String player, String opponent) {
            this.player = player;
            this.opponent = opponent;
        }
    }
}
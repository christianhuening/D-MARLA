import AgentProvider.Implementation.Agents.IDictionary;
import AgentProvider.Implementation.Database.AgentSettingsAccessor;
import AgentProvider.Implementation.Database.PersistenceFactory;
import AgentProvider.Implementation.KeyNotFoundException;
import AgentProvider.Implementation.PersistenceType;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Settings.SettingException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestAgentProvider {

    private static PersistenceFactory persistenceFactory;

    /**
     * Setup once before fixture.
     */
    @BeforeClass
    public static void setup() throws TechnicalException, SettingException {
        persistenceFactory = new PersistenceFactory("testAgentSystem");
    }

    @AfterClass
    public static void tearDown() {
        persistenceFactory = null;
    }

    private static boolean areEqual(float value, float target) {
        return value <= target + 0.001f && value >= target - 0.001f;
    }

    private static String generateRandomString(int length, Random random) {
     StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            stringBuilder.append((byte)random.nextInt(120));
        }

        return stringBuilder.toString();
    }

    /**
     * Tests, if values are remembered correctly
     * @throws TechnicalException
     * @throws SQLException
     */
    @Test
    public void TestTableDictionary() throws TechnicalException, SQLException {

        Map<StateAction, Float> stored = new HashMap<StateAction, Float>();

        //create a PersistenceFactory and retrieve a dictionary from it
        IDictionary testDictionary = persistenceFactory.getDictionary("testAgent", "testDictionary", PersistenceType.Table);

        Random random = new Random();
        StateAction stateAction;
        float value;
        // generate 50000 random entries and save them in our map and the dictionary. then test if values were remembered correctly
        for (int i = 0; i < 50000; i++) {
            if(i%1000 == 0) System.out.println(i);
            stateAction = new StateAction(generateRandomString(random.nextInt(8), random), generateRandomString(random.nextInt(8), random));
            value = random.nextFloat();
            stored.put(stateAction, value);
            testDictionary.setValue(stateAction, value);
        }

        for (StateAction tmp : stored.keySet()) {
            Assert.assertTrue("Wert nicht korrekt gespeichert. erwartet: " + stored.get(tmp) + " bekommen: " + testDictionary.getValue(tmp), areEqual(stored.get(tmp), testDictionary.getValue(tmp)));
        }

        // clear both dictionaries and try the same thing again.
        testDictionary.resetValues();
        stored.clear();

        for (int i = 0; i < 50000; i++) {
            if(i%1000 == 0) System.out.println(i);
            stateAction = new StateAction(generateRandomString(random.nextInt(15), random), generateRandomString(random.nextInt(15), random));
            value = random.nextFloat();
            stored.put(stateAction, value);
            testDictionary.setValue(stateAction, value);
        }

        for (StateAction tmp : stored.keySet()) {
            Assert.assertTrue("Wert nach reset nicht korrekt gespeichert.", areEqual(stored.get(tmp), testDictionary.getValue(tmp)));
        }

    }

    /**
     * Tests, if the getAllSimilarStatesFor method of dictionary really returns all similar keys.
     * @throws TechnicalException
     */
//    @Test
//    public void TestSimilarValues() throws TechnicalException {
//        Random rand = new Random();
//
//        IDictionary testDictionary = persistenceFactory.getDictionary("testAgent", "testDictionary", PersistenceType.Table);
//        testDictionary.resetValues();
//
//        Map<StateAction, Float> expected = new Hashtable<StateAction, Float>();
//
//        StateAction test1 = new StateAction("AAAA");
//        StateAction test2 = new StateAction("AAAB");
//        StateAction test3 = new StateAction("AAAFUU");
//        StateAction test4 = new StateAction("AAA:)");
//
//        expected.put(test1, rand.nextFloat());
//        expected.put(test2, rand.nextFloat());
//        expected.put(test3, rand.nextFloat());
//        expected.put(test4, rand.nextFloat());
//
//        testDictionary.setValue(test1, expected.get(test1));
//        testDictionary.setValue(test2, expected.get(test2));
//        testDictionary.setValue(test3, expected.get(test3));
//        testDictionary.setValue(test4, expected.get(test4));
//
//        Map<StateAction, Float> similarValues = testDictionary.getAllSimilarStatesFor(new StateAction("AAA"));
//
//        Assert.assertTrue("4 entries expected, but there were " + similarValues.size() + ".", similarValues.size() == 4);
//
//        float orig;
//        float saved;
//        for(Map.Entry<StateAction, Float> entry : expected.entrySet()) {
//            Assert.assertTrue("not all expected keys were contained", similarValues.containsKey(entry.getKey()));
//
//            orig = expected.get(entry.getKey());
//            saved = similarValues.get(entry.getKey());
//            Assert.assertTrue("saved value was not in range of the original.\n orig:\n" + orig + "\nsaved:\n" + saved, saved > orig - 0.1 && saved < orig + 0.1);
//        }
//    }

    @Test
    public void TestAgentSettingsAccessor() throws TechnicalException, KeyNotFoundException, SQLException {
        //get an agent setting accessor
        AgentSettingsAccessor agentSettingsAccessor = persistenceFactory.getAgentSettingsAccessor("testAgent");

        // set some agent values
        agentSettingsAccessor.setValue("alpha", 0.95f);
        agentSettingsAccessor.setValue("epsilon", 0.75f);
        agentSettingsAccessor.setValue("gamma", 1.0f);

        List<String> settingsKeys = agentSettingsAccessor.getAgentParameterKeys();

        Assert.assertTrue("'alpha' should have been in the key set, but was not.", settingsKeys.contains("alpha"));
        Assert.assertTrue("'epsilon' should have been in the key set, but was not.", settingsKeys.contains("epsilon"));
        Assert.assertTrue("'gamma' should have been in the key set, but was not.", settingsKeys.contains("gamma"));

        //retrieve values back and expect them to be persisted correctly
        float value1 = agentSettingsAccessor.getValue("alpha");
        float value2 = agentSettingsAccessor.getValue("epsilon");
        float value3 = agentSettingsAccessor.getValue("gamma");

        Assert.assertTrue("Was not correctly saved: " + value1, value1 > 0.949 && value1 < 0.951);
        Assert.assertTrue("Was not correctly saved: " + value2, value2 > 0.749 && value2 < 0.751);
        Assert.assertTrue("Was not correctly saved: " + value3, value3 > 0.99 && value3 < 1.001);
    }
}

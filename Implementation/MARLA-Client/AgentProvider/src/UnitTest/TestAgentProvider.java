import AgentProvider.Implementation.Agents.IDictionary;
import AgentProvider.Implementation.Database.AgentSettingsAccessor;
import AgentProvider.Implementation.Database.PersistenceFactory;
import AgentProvider.Implementation.KeyNotFoundException;
import AgentProvider.Implementation.PersistenceType;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.StateAction;
import Settings.SettingException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Hashtable;
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

    /**
     * Tests, if values are remembered correctly
     * @throws TechnicalException
     * @throws SQLException
     */
    @Test
    public void TestTableDictionary() throws TechnicalException, SQLException {

        //create a PersistenceFactory and retrieve a dictionary from it
        IDictionary testDictionary = persistenceFactory.getDictionary("testAgent", "testDictionary", PersistenceType.Table);

        // set some values
        StateAction key1 = new StateAction(":(");
        StateAction key2 = new StateAction("AAA");
        StateAction key3 = new StateAction("FUU");
        StateAction key4 = new StateAction(":)");

        testDictionary.setValue(key1, 5.0f);
        testDictionary.setValue(key2, 65.0f);
        testDictionary.setValue(key3, 3.14159f);
        testDictionary.setValue(key4, 1.4f);

        //and expect them to be remembered correctly
        float savedValue1 = testDictionary.getValue(key1);
        Assert.assertTrue("Was not correctly saved: " + savedValue1, savedValue1 > 4.9 && savedValue1 < 5.1);
        float savedValue2 = testDictionary.getValue(key2);
        Assert.assertTrue("Was not correctly saved: " + savedValue2, savedValue2 > 64.999 && savedValue2 < 65.001);
        float savedValue3 = testDictionary.getValue(key3);
        Assert.assertTrue("Was not correctly saved: " + savedValue3, savedValue3 > 3.14 && savedValue3 < 3.142);
        float savedValue4 = testDictionary.getValue(key4);
        Assert.assertTrue("Was not correctly saved: " + savedValue4, savedValue4 > 1.3999 && savedValue4 < 1.441);

        testDictionary.resetValues();

        float resetValue = testDictionary.getValue(key1);
        Assert.assertTrue("Was not reset: " + resetValue, resetValue > -0.001 && resetValue < 0.001);
        resetValue = testDictionary.getValue(key2);
        Assert.assertTrue("Was not reset: " + resetValue, resetValue > -0.001 && resetValue < 0.001);
        resetValue = testDictionary.getValue(key3);
        Assert.assertTrue("Was not reset: " + resetValue, resetValue > -0.001 && resetValue < 0.001);
        resetValue = testDictionary.getValue(key4);
        Assert.assertTrue("Was not reset: " + resetValue, resetValue > -0.001 && resetValue < 0.001);

    }

    /**
     * Tests, if the getAllSimilarStatesFor method of dictionary really returns all similar keys.
     * @throws TechnicalException
     */
    @Test
    public void TestSimilarValues() throws TechnicalException {
        Random rand = new Random();

        IDictionary testDictionary = persistenceFactory.getDictionary("testAgent", "testDictionary", PersistenceType.Table);
        testDictionary.resetValues();

        Map<StateAction, Float> expected = new Hashtable<StateAction, Float>();

        StateAction test1 = new StateAction("AAAA");
        StateAction test2 = new StateAction("AAAB");
        StateAction test3 = new StateAction("AAAFUU");
        StateAction test4 = new StateAction("AAA:)");

        expected.put(test1, rand.nextFloat());
        expected.put(test2, rand.nextFloat());
        expected.put(test3, rand.nextFloat());
        expected.put(test4, rand.nextFloat());

        testDictionary.setValue(test1, expected.get(test1));
        testDictionary.setValue(test2, expected.get(test2));
        testDictionary.setValue(test3, expected.get(test3));
        testDictionary.setValue(test4, expected.get(test4));

        Map<StateAction, Float> similarValues = testDictionary.getAllSimilarStatesFor(new StateAction("AAA"));

        Assert.assertTrue("4 entries expected, but there were " + similarValues.size() + ".", similarValues.size() == 4);

        float orig;
        float saved;
        for(Map.Entry<StateAction, Float> entry : expected.entrySet()) {
            Assert.assertTrue("not all expected keys were contained", similarValues.containsKey(entry.getKey()));

            orig = expected.get(entry.getKey());
            saved = similarValues.get(entry.getKey());
            Assert.assertTrue("saved value was not in range of the original.\n orig:\n" + orig + "\nsaved:\n" + saved, saved > orig - 0.1 && saved < orig + 0.1);
        }
    }

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

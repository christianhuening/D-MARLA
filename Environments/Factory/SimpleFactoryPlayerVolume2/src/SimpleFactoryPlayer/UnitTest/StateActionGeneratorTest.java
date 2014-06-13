package SimpleFactoryPlayer.UnitTest;

import AgentSystemPluginAPI.Contract.StateAction;
import Factory.GameLogic.Enums.Direction;
import SimpleFactoryPlayer.Implementation.Entities.RawField;
import SimpleFactoryPlayer.Implementation.Entities.RawState;
import SimpleFactoryPlayer.Implementation.Enums.FieldType;
import SimpleFactoryPlayer.Implementation.Enums.FriendFoe;
import SimpleFactoryPlayer.Implementation.StateActionGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
* Created with IntelliJ IDEA.
* User: TwiG
* Date: 08.06.12
* Time: 16:01
* To change this template use File | Settings | File Templates.
*/
public class StateActionGeneratorTest {
    RawState testState = new RawState();

    RawField rawField = new RawField();
    RawField rawField2 = new RawField();
    RawField rawField3 = new RawField();
    StateActionGenerator stateActionGenerator =new StateActionGenerator();
    @Before
    public void setUp() throws Exception {
        rawField.setUnit(FriendFoe.NONE);
        rawField.setFieldController(FriendFoe.FRIEND);
        rawField.setFieldType(FieldType.FACTORY);
        rawField.setRemainingTimeToSpawn(2);

        testState.setLeft(rawField);
        testState.setTop(rawField);
        testState.setLeftTop(rawField);

        rawField2.setUnit(FriendFoe.FOE);
        rawField2.setFieldController(FriendFoe.FOE);
        rawField2.setFieldType(FieldType.INFLUENCE);
        rawField2.setRemainingTimeToSpawn(6);

        testState.setDown(rawField2);
        testState.setLeftDown(rawField2);

        rawField3.setUnit(FriendFoe.FRIEND);
        rawField3.setFieldController(FriendFoe.FOE);
        rawField3.setFieldType(FieldType.NORMAL);
        rawField3.setRemainingTimeToSpawn(4);

        testState.setMiddle(rawField3);
        testState.setRight(rawField3);
        testState.setRightTop(rawField3);
        testState.setRightDown(rawField3);

        testState.setSignal(Direction.RIGHT);

    }

    @After
    public void tearDown() throws Exception {}


    @Test
    public void testGetAllPossibleActions() throws Exception {
        String encryptedState = stateActionGenerator.encryptState(testState);
        Set<StateAction> actionSet ;
        actionSet = stateActionGenerator.getAllPossibleActions(new StateAction(encryptedState,""));
        for(StateAction stateAction : actionSet){
            int toTest = Integer.parseInt(stateAction.getActionDescription());
            assert(toTest== 6 ||toTest== 5 ||toTest== 0);
        }

    }

    @Test
    public void testDecryptDirection() throws Exception {
        String encryptedState = stateActionGenerator.encryptState(testState);
        Set<StateAction> stateActions = stateActionGenerator.getAllPossibleActions(new StateAction(encryptedState));

        for(StateAction action : stateActions){
            //System.out.println(stateActionGenerator.decryptDirection(action.getActionDescription()));
        }



    }

    @Test
    public void testDecryptState() throws Exception {
        String encryptedState = stateActionGenerator.encryptState(testState);
        //System.out.println(testState);
        //System.out.println(stateActionGenerator.decryptState(encryptedState));
    }

    @Test
    public void testDecryptField() throws Exception {
    }
}

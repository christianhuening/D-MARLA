
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;
import SimpleFactoryPlayer.Implementation.SimpleFactoryPlayerSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* Created with IntelliJ IDEA.
* User: TwiG
* Date: 09.06.12
* Time: 19:48
* To change this template use File | Settings | File Templates.
*/
public class SimpleFactoryPlayerSystemTest {
    UUID one = UUID.randomUUID();
    UUID two = UUID.randomUUID();


    TFactory factoryEnemy1 = new TFactory(5,0, Faction.BLUE,1);
    TFactory factoryNeutral2 = new TFactory(5,0,Faction.NEUTRAL,2);
    List<TFactory> factoryList = new ArrayList<TFactory>();

    TFactoryField factoryField1 = new TFactoryField(null,1);
    TFactoryField factoryField2 = new TFactoryField(null,2);
    TGameState gameState;
    TUnit blueUnit1 = new TUnit(one, Faction.BLUE,-1);
    TUnit redUnit2 = new TUnit(two,Faction.RED,-2);
    TNormalField normalWithEnemy = new TNormalField(blueUnit1);
    TNormalField normalWithFriend = new TNormalField(redUnit2);
    TNormalField normalWithNone = new TNormalField(null);
    TAbstractField[][] boardForSearch = {
            {normalWithNone,normalWithEnemy,normalWithNone,normalWithNone},
            {normalWithNone,normalWithNone,normalWithNone,normalWithNone},
            {factoryField1,normalWithFriend,normalWithNone,factoryField2},
            {normalWithNone,normalWithFriend,normalWithNone,normalWithNone},
            };
    @Before
    public void setUp() throws Exception {
        factoryList.add(factoryEnemy1);
        factoryList.add(factoryNeutral2);
        gameState= new TGameState(false,null,0,0,null,factoryList,boardForSearch);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetActionsForEnvironmentStatus() throws Exception {
        SimpleFactoryPlayerSystem system = new SimpleFactoryPlayerSystem(new DummyPluginProvider());
        system.start(Faction.RED);
        TActionsInTurn actions= (TActionsInTurn)system.getActionsForEnvironmentStatus(gameState);

        for(TAction action: actions){
            System.out.println("Actions after start: "+action.getDirection());

            System.out.println("Unit after start: "+action.getUnit().getControllingFaction());

        }
        actions= (TActionsInTurn)system.getActionsForEnvironmentStatus(gameState);

        for(TAction action: actions){
            System.out.println("Actions after 2: "+action.getDirection());

            System.out.println("Unit after 2: "+action.getUnit().getControllingFaction());
        }
        actions= (TActionsInTurn)system.getActionsForEnvironmentStatus(gameState);

        for(TAction action: actions){
            System.out.println("Actions after 3: "+action.getDirection());

            System.out.println("Unit after 3: "+action.getUnit().getControllingFaction());
        }


    }
}

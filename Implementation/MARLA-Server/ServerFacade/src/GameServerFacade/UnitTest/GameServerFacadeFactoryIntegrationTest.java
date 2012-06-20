import GameServerFacade.Interface.GameServerFacadeFactory;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 24.05.12
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class GameServerFacadeFactoryIntegrationTest {

    @BeforeClass
    public static void Setup() {

    }

    @Test
    public void BasicFacadeSetupTest() {
        Assert.assertNotNull(GameServerFacadeFactory.getIntegrationTestApplicationCore());
    }
}

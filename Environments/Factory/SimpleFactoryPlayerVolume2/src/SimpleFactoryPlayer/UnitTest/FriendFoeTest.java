package SimpleFactoryPlayer.UnitTest;

import SimpleFactoryPlayer.Implementation.Enums.FriendFoe;
import org.junit.Test;

/**
* Created with IntelliJ IDEA.
* User: TwiG
* Date: 08.06.12
* Time: 15:40
* To change this template use File | Settings | File Templates.
*/
public class FriendFoeTest {
    @Test
    public void testGet() throws Exception {
        assert(FriendFoe.FRIEND==FriendFoe.get(0));
        assert(FriendFoe.FRIEND.ordinal()==0);
    }
}

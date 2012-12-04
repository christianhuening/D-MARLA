package Factory.GameLogic.TransportTypes;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 21.05.12
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public class TPosition implements java.io.Serializable {
    private int x;

    private int y;

    public TPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

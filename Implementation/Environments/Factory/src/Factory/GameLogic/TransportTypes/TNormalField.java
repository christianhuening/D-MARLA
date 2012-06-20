package Factory.GameLogic.TransportTypes;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 22.05.12
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class TNormalField extends TAbstractField implements java.io.Serializable {
    public TNormalField(TUnit occupant) {
        super(occupant);
    }
}
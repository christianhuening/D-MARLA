package Factory.GameLogic.GameActors;

import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TNormalField;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */

public class NormalField extends AbstractField {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public TAbstractField getTransportType() {
        if (this.isOccupied()) {
            return new TNormalField(this.getOccupant().getTransportType());
        } else {
            return new TNormalField(null);
        }
    }
}

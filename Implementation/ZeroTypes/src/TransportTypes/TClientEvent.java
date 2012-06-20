package TransportTypes;

import Enumeration.ClientEventType;
import TransportTypes.TSession;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class TClientEvent implements java.io.Serializable {
    private ClientEventType eventType;

    private TSession sessionReference;

    public TSession getSessionReference() {
        return sessionReference;
    }

    public ClientEventType getEventType() {
        return eventType;
    }

    public TClientEvent(ClientEventType eventType, TSession sessionReference) {
        this.eventType = eventType;
        this.sessionReference = sessionReference;
    }
}

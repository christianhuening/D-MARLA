package ServerRunner.Interface;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 07.05.12
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */

import ZeroTypes.TransportTypes.TClientEvent;

/**
 * Event handler interface for player events.
 */
public interface IPlayerEventHandler {
    void call(TClientEvent event);
}

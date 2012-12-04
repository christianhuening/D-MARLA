package Factory.Interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 29.05.12
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */


public interface IHasConsistencyCheck {
    /**
     * Checks if this type is in a consistent state.
     */
    boolean isConsistent();
}

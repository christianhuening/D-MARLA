package Factory.GameLogic.Exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 11.06.12
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class NoUnitFoundException extends RuntimeException {
    public NoUnitFoundException(String message) {
        super(message);
    }
}

package Factory.Interfaces;


import EnvironmentPluginAPI.Exceptions.TechnicalException;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 16.05.12
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
public interface IHasTransportType<T> {
    public T getTransportType() throws TechnicalException;
}

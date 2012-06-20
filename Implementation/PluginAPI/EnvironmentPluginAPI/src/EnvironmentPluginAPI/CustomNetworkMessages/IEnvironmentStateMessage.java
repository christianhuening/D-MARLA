package EnvironmentPluginAPI.CustomNetworkMessages;

import EnvironmentPluginAPI.Contract.IEnvironmentState;

import java.io.Externalizable;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 16.06.12
 * Time: 02:21
 * To change this template use File | Settings | File Templates.
 */
public interface IEnvironmentStateMessage<E extends IEnvironmentState> extends Externalizable {
    E getEnvironmentState();
}

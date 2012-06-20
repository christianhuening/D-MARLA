package EnvironmentPluginAPI.CustomNetworkMessages;

import EnvironmentPluginAPI.Contract.IActionDescription;

import java.io.Externalizable;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 16.06.12
 * Time: 02:21
 * To change this template use File | Settings | File Templates.
 */
public interface IActionDescriptionMessage<A extends IActionDescription> extends Externalizable {

    public A getAction();

}

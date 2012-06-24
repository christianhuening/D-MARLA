package NetworkAdapter.Messages;

import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * This is the default message implementation for ActionDescriptions
 */
public class DefaultActionDescriptionMessage extends NetworkMessage implements Serializable, IActionDescriptionMessage {

    private IActionDescription actionDescription;

    public DefaultActionDescriptionMessage(int clientId, IActionDescription actionDescription){
        super(clientId);
        this.actionDescription = actionDescription;
    }

    /**
     * Only for serialization!!
     */
    public DefaultActionDescriptionMessage() {
        super(-1);
    }

    @Override
    public IActionDescription getAction() {
        return this.actionDescription;
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @throws java.io.IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe
     * the data layout of this Externalizable object.
     * List the sequence of element types and, if possible,
     * relate the element to a public/protected field and/or
     * method of this Externalizable class.
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(getClientId());
        out.writeObject(actionDescription);
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws java.io.IOException    if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setClientId(in.readInt());
        actionDescription = (IActionDescription)in.readObject();
    }
}

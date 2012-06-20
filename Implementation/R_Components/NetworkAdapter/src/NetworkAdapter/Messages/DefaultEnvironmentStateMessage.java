package NetworkAdapter.Messages;

import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.CustomNetworkMessages.IEnvironmentStateMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * This is the default message implementation for EnvironmentStates
 */
public class DefaultEnvironmentStateMessage extends NetworkMessage implements IEnvironmentStateMessage, Serializable {

    private IEnvironmentState environmentState;

    /**
     * Only for serialization!!
     */
    public DefaultEnvironmentStateMessage() {
        super(-1);
    }

    public DefaultEnvironmentStateMessage(int clientId, IEnvironmentState environmentState){
        super(clientId);
        this.environmentState = environmentState;
    }

    @Override
    public IEnvironmentState getEnvironmentState() {
        return environmentState;
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
        out.writeObject(environmentState);
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
        environmentState = (IEnvironmentState)in.readObject();
    }
}

package EnvironmentPluginAPI.CustomNetworkMessages;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The basic network message.
 * <br/><br/>
 * All communication in the MARLA system between MARLA-Server and -Client is realized by messages inheriting from this
 * class.
 * <br/><br/>
 * If no custom message responsible for transporting an environment plugin implementation of "enviroment state" or
 * "action description" is found in the environment plugin, MARLA will use default messages. Those messages simply
 * serialize all it's contents. This implies that all classes, that are part of an environment plugin and may be
 * contained in an "environment state" and/or an "action description" message, must implement the java.io.Serializable
 * interface. This is an adequate practice for most network messages.
 * <br/><br/>
 * If you are, however, interested in <b>customizing</b> the serialization process (mostly if you expect noticeable
 * compression and thus increased network throughput from doing so), you have to do 3 things:<br/><br/>
 * 1. (optional) Provide a custom implementation for a message by deriving from NetworkMessage<br/>
 * 2. (optional, and only if 1 was done) Tag the custom message with the @CustomMessage annotation<br/>
 * 2. Implement the  interface where there is optimization potential<br/><br/>
 *
 * Since the full object tree of the messages is serialized, you can do step 3 anywhere you want. But because the optimal
 * serialization of an object tree can become a non-trivial task, we encourage plugins to implement logic related to
 * that in a dedicated message class.
 * <br/><br/>
 * NOTICE: To be recognized by the MARLA plugin loader, you must tag those implementations with the Annotation
 * @see EnvironmentPluginAPI.Contract.IEnvironmentState
 * @see EnvironmentPluginAPI.Contract.IActionDescription
 * @see EnvironmentPluginAPI.CustomNetworkMessages.CustomMessage
 * @see java.io.Externalizable
 *
 */
public abstract class NetworkMessage implements Serializable {

    public static byte[] EMPTY_BYTES = new byte[]{};

    public static byte[] ZERO = new byte[]{0};

    public static byte[] ONE = new byte[]{1};

    public static byte[] TWO = new byte[]{2};

    private int clientId;

    /**
     * creates a basic message.
     *
     * @param clientId see getClientID()
     */
    public NetworkMessage(int clientId) {
        this.clientId = clientId;
    }

    protected void setClientId(int clientId) {
        this.clientId = clientId;
    }


    /**
     * in client mode: The MARLA-Client id from which the message was sent<br/>
     * in server mode: The MARLA-Client id to which the message is targeted
     *
     * @return the client id. -1 if unknown, 0..Integer.MAX_VALUE else
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Concatenates the given byte arrays to a new one.
     *
     * @param first the front part, != null
     * @param second the rear part, != null
     * @return a byte array of length a.length + b.length containing the values of a in the front and the values of b in
     * the rear. not null
     */
    public static byte[] concatByteArrays(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
package AgentSystemPluginAPI.Contract;

/**
 * This interface must be implemented by States, Actions or StateActions. It is used for disk space efficient persisting
 * of the Q- and E-Values of the agent implementations.
 *
 * CONTRACT:    Classes implementing this interface must be capable of rebuilding objects of themselves with a String
 * returned from @see getCompressedRepresentation.
 */
public interface ICompressed {
    /**
     * Returns a compressed representation of this object as a String.
     * @return A compressed representation of this object != null. Must also be at least one character long.
     */
	public String getCompressedRepresentation();
}
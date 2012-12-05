package AgentProvider.Implementation.Database;

import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

/**
 *  This class defines the interface of a cache for persisting agent learning data efficiently.
 *  The topmost cache of this stack is expected to be a database, which means that all size restrictions don't apply.
 */
interface ICache {

    /**
     *  Stores the given values. If there is a parent and the cache is full,
     *  it is expected to store part of its data in the parent cache, thus making room for the new value.
     * @param stateAction != null
     * @param value != null
     */
    public void store(StateAction stateAction, float value) throws TechnicalException;

    /**
     *  Returns the value of the state action if found.
     * @param stateAction != null
     * @return see description
     * @throws ValueNotFoundException if the key is not present in this cache
     */
    public float remove(StateAction stateAction) throws ValueNotFoundException, TechnicalException;

    /**
     *  Returns true, if this cache has the key stored, false else.
     * @param stateAction != null
     * @return see description
     */
    public boolean hasStored(StateAction stateAction) throws TechnicalException;

    /**
     *  Resolves a value from the cache stack. The topmost cache is expected to be a database.
     *  If the value is found in a parent cache of this cache, the value will be stored in a cache one level deeper.
     * @param stateAction != null
     * @return the learned data
     */
    public float get(StateAction stateAction) throws ValueNotFoundException, TechnicalException;

    /**
     *  All data on all levels will be pushed to the topmost cache (normally the database).
     *  As a result, all stack levels except for the topmost are empty after this operation.
     */
    public void flush() throws TechnicalException;

    /**
     *  Removes all data from this cache level without flushing.
     */
    public void clear() throws TechnicalException;
}

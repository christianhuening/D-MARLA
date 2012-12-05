package AgentProvider.Implementation.Database;

import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  This class implements a cache that is only stored in memory.
 */
public class MemoryCache implements ICache {

    private final ICache child;
    private int size;
    private int elementCount;
    private final Map<StateAction, Float> dict;

    public MemoryCache(ICache child, int size) {
        this.child = child;
        this.size = size;
        dict = new HashMap<StateAction, Float>(size);
        elementCount = 0;
    }

    @Override
    public void store(StateAction stateAction, float value) throws TechnicalException {
        if(elementCount >= size) {
            flushPartly();
        }

        if(!dict.containsKey(stateAction)) {
            elementCount++;
        }

        dict.put(stateAction, value);
    }

    @Override
    public boolean hasStored(StateAction stateAction) {
        return dict.containsKey(stateAction);
    }

    /**
     *  Removes about one quarter of the elements randomly and flushes them to the parent cache.
     */
    private void flushPartly() throws TechnicalException {
        int nrToPush = elementCount / 4;
        elementCount -= nrToPush;
        StateAction[] entries = dict.keySet().toArray(new StateAction[dict.keySet().size()]);
        Random random = new Random();
        StateAction stateAction;
        int i;
        float value;
        while (nrToPush > 0) {
            i = random.nextInt(entries.length);
            stateAction = entries[i];
            if (stateAction != null) {
                value = remove(stateAction);
                child.store(stateAction, value);
                nrToPush--;
                entries[i] = null;
            }
        }
    }

    public float remove(StateAction stateAction) {
        float value = dict.remove(stateAction);
        elementCount--;
        return value;
    }

    @Override
    public float get(StateAction stateAction) throws ValueNotFoundException, TechnicalException {

        if(dict.containsKey(stateAction)) {
            return dict.get(stateAction);
        }

        if(child.hasStored(stateAction)) {
            float value = child.remove(stateAction);
            store(stateAction, value);
            return value;
        }

        return child.get(stateAction);
    }

    @Override
    public void flush() throws TechnicalException {
        for (Map.Entry<StateAction, Float> stateActionFloatEntry : dict.entrySet()) {
            child.store(stateActionFloatEntry.getKey(), stateActionFloatEntry.getValue());
        }
        child.flush();
    }

    @Override
    public void clear() throws TechnicalException {
        dict.clear();
        elementCount = 0;
        child.clear();
    }
}

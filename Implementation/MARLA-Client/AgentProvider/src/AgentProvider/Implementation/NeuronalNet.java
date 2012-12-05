package AgentProvider.Implementation;

import AgentProvider.Implementation.Agents.IDictionary;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

public class NeuronalNet implements IDictionary {

	public int hashCode() {
		int lhashCode = 0;
		if ( lhashCode == 0 ) {
			lhashCode = super.hashCode();
		}
		return lhashCode;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof NeuronalNet) {
			NeuronalNet lNeuronalNetObject = (NeuronalNet) object;
			boolean lequals = true;
			return lequals;
		}
		return false;
	}

	public float getValue(StateAction key) {
		throw new UnsupportedOperationException();
	}

	public void setValue(StateAction key, float newValue) {
		throw new UnsupportedOperationException();
	}

    @Override
    public void resetValues() throws TechnicalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
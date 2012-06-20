import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;


public class AgentTableModel extends DefaultTableModel {

    private List<TAgentSystemDescription> agentList;

    public AgentTableModel() {
        this.addColumn("Name");
        this.addColumn("Version");
        this.addColumn("Description");
        agentList = new ArrayList<TAgentSystemDescription>();
    }

    public void addAgent(TAgentSystemDescription agent){
        agentList.add(agent);
        this.addRow(new Object[]{agent.getName(), agent.getVersion(), agent.getDescription()});
    }

    public void addAgents(List<TAgentSystemDescription> agents) {
        for (TAgentSystemDescription agent : agents) {
            this.addAgent(agent);
        }
    }

    /**
     * Returns the agent plugin that is currently selected
     * @return null, if no agent selected
     */
    public TAgentSystemDescription getSelectedAgent(int id) {
        return agentList.get(id);
    }
}

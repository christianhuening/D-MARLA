package Models;

import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the model for a JTable that manages TEnvironmentDescriptions.
 */
public class EnvironmentListTableModel extends DefaultTableModel {
    private List<TEnvironmentDescription> environmentDescriptions;

    public EnvironmentListTableModel() {
        environmentDescriptions = new LinkedList<TEnvironmentDescription>();

        addColumn("Name");
        addColumn("Version");
        addColumn("Description");
    }

    public void addEnvironment(TEnvironmentDescription environmentDescription) {
        int i = environmentDescriptions.size();

        environmentDescriptions.add(environmentDescription);
        addRow(new Object[] {environmentDescription.getName(), environmentDescription.getVersion(), environmentDescription.getDescription()});

        fireTableRowsInserted(i, i);
    }

    public void addAllEnvironments(List<TEnvironmentDescription> descriptions) {
        for(TEnvironmentDescription tmp : descriptions) {
            addEnvironment(tmp);
        }
    }

    public TEnvironmentDescription get(int index) {
        return environmentDescriptions.get(index);
    }
}

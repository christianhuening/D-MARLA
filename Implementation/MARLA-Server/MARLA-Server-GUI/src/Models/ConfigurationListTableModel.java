package Models;

import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 28.05.12
 * Time: 02:25
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationListTableModel extends DefaultTableModel {

    private List<IEnvironmentConfiguration> configurations = new LinkedList<IEnvironmentConfiguration>();

    public ConfigurationListTableModel(){
        addColumn("MapName");
    }

    public void addMap(IEnvironmentConfiguration mapMetaData){
        configurations.add(mapMetaData);
        addRow(new String[]{mapMetaData.toString()});
        //fireTableStructureChanged();
    }

    public void addConfigurations(List<IEnvironmentConfiguration> maps){
        for(IEnvironmentConfiguration s : maps){
            addMap(s);
        }
    }

    public IEnvironmentConfiguration get(int index) {
        return configurations.get(index);
    }

    public int getSize(){
        return configurations.size();
    }

    public void removeAllConfigurations(){
        configurations.removeAll(configurations);
        for(int i = 0; i < this.getRowCount(); i++) {
            this.removeRow(i);
        }
    }

    public void clear(){
        configurations = new LinkedList<IEnvironmentConfiguration>();
        setRowCount(0);
    }
}
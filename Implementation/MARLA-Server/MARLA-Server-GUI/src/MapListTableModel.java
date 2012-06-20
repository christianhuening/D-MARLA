import EnvironmentPluginAPI.TransportTypes.TMapMetaData;

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
public class MapListTableModel extends DefaultTableModel {

    private List<TMapMetaData> maps = new LinkedList<TMapMetaData>();

    public MapListTableModel(){
        addColumn("MapName");
    }

    public void addMap(TMapMetaData mapMetaData){
        maps.add(mapMetaData);
        addRow(new String[]{mapMetaData.toString()});
        //fireTableStructureChanged();
    }

    public void addMaps(List<TMapMetaData> maps){
        for(TMapMetaData s : maps){
            addMap(s);
        }
    }

    public TMapMetaData get(int index) {
        return maps.get(index);
    }

    public int getSize(){
        return maps.size();
    }

    public void removeAllMaps(){
        maps.removeAll(maps);
        for(int i = 0; i < this.getRowCount(); i++) {
            this.removeRow(i);
        }
    }

    public void clear(){
        maps = new LinkedList<TMapMetaData>();
        setRowCount(0);
    }
}
package OverseerGUI;


import ZeroTypes.TransportTypes.TCycleReplayDescription;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class CycleDescriptionTableModel extends DefaultTableModel {

    private List<TCycleReplayDescription> cycleDescriptions;

    public CycleDescriptionTableModel(){
        this.addColumn("Clients in Game");
        this.addColumn("Winning Client");
        this.addColumn("Number of Turns");
        cycleDescriptions = new ArrayList<TCycleReplayDescription>();
    }

    public void addGameDescription(TCycleReplayDescription gameDesc){
        cycleDescriptions.add(gameDesc);
        this.addRow(new Object[] {gameDesc.getClients().get(0),gameDesc.getWinningClient(),gameDesc.getNumberOfTurns()});
    }

    public void addGameDescriptions(List<TCycleReplayDescription> gameDescs){
        for(TCycleReplayDescription g : gameDescs){
            this.addGameDescription(g);
        }
    }

    public void removeAllGameDescriptions(){
        cycleDescriptions = new ArrayList<TCycleReplayDescription>();
        for(int i = this.getRowCount()-1; i >= 0; i--){
            this.removeRow(i);
        }
    }

    public TCycleReplayDescription getGameDescription(int id){
        return cycleDescriptions.get(id);
    }

}

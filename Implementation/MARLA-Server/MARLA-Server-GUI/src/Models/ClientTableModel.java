package Models;

import ZeroTypes.TransportTypes.TNetworkClient;

import javax.swing.table.DefaultTableModel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom TableModel for storing TNetworkClient Objects in a JTable
 */
public class ClientTableModel extends DefaultTableModel {

    public ClientTableModel(){
        this.addColumn("ID");
        this.addColumn("Name");
        this.addColumn("Address");
        this.addColumn("Connected Since");
    }


    public void addClient(TNetworkClient client){
        this.addRow(new String[] {((Integer)client.getId()).toString(), client.getName(), client.getAddress().getHostAddress(), client.getConnectedSince().toString()});
    }

    public void addClients(List<TNetworkClient> clients){
        for(TNetworkClient client : clients){
            addClient(client);
        }
    }

    public TNetworkClient removeClientAt(int rowNr) throws UnknownHostException {
        TNetworkClient client = createTClientFromRow(rowNr);
        this.removeRow(rowNr);
        return client;
    }

    public void removeAllClients(){
        for(int i=0; i < this.getRowCount(); i++){
            this.removeRow(i);
        }
    }

    public List<TNetworkClient> getAllClients() throws UnknownHostException {
        List<TNetworkClient> clients = new ArrayList<TNetworkClient>();
        for(int i=0; i < this.getRowCount(); i++){
            clients.add(createTClientFromRow(i));
        }
        this.removeAllClients();
        return clients;
    }

    private TNetworkClient createTClientFromRow(int rowNr) throws UnknownHostException {
        Integer id = Integer.parseInt(this.getValueAtAsString(rowNr,0));

        InetAddress inetAddress = InetAddress.getByName(this.getValueAtAsString(rowNr, 2));

        Date connectedSince = new Date();

        TNetworkClient client = new TNetworkClient(id.intValue(), this.getValueAtAsString(rowNr,1), inetAddress, connectedSince);

        return client;
    }

    private String getValueAtAsString(int row, int column){
        return String.valueOf(this.getValueAt(row,column));
    }


}

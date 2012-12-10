package Models;

import ZeroTypes.TransportTypes.TNetworkClient;

import javax.swing.table.DefaultTableModel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Custom TableModel for storing TNetworkClient Objects in a JTable
 */
public class ClientTableModel extends DefaultTableModel {

    private List<TNetworkClient> clients;

    public ClientTableModel(){
        this.addColumn("ID");
        this.addColumn("Name");
        this.addColumn("Address");
        this.addColumn("Connected Since");

        clients = new LinkedList<TNetworkClient>();
    }


    public void addClient(TNetworkClient client){
        clients.add(client);
        this.addRow(new String[] {((Integer)client.getId()).toString(), client.getName(), client.getAddress().getHostAddress(), client.getConnectedSince().toString()});
    }

    public void addClients(List<TNetworkClient> clients){
        for(TNetworkClient client : clients){
            addClient(client);
        }
    }

    public TNetworkClient removeClientAt(int rowNr) throws UnknownHostException {
        this.removeRow(rowNr);
        return clients.remove(rowNr);
    }

    public void removeAllClients(){
        for(int i=0; i < this.getRowCount(); i++){
            this.removeRow(i);
        }
    }

    public List<TNetworkClient> getAllClients() throws UnknownHostException {
        return clients;
    }
}

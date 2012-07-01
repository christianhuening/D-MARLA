package Models;

import TransportTypes.TSession;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chhuening
 * Date: 20.05.12
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class SessionTableModel extends DefaultTableModel {

    private Map<Integer,TSession> sessionList;

    public SessionTableModel(){
        this.addColumn("Name");
        this.addColumn("PlayerCount");
        this.addColumn("MapHash");
        this.addColumn("Status");
        sessionList = new HashMap<Integer, TSession>();
    }

    public void addSession(TSession session){
        sessionList.put(this.getRowCount(),session);
        this.addRow(new String[] {session.getName(), String.valueOf(session.getPlayerCount()), session.getMapMetaData().toString(), session.getStatus().toString() });
    }

    public void addSessions(List<TSession> sessions){
        for(TSession s : sessions){
            addSession(s);
        }
    }

    public TSession removeSessionAt(int rowNr){
        TSession session = sessionList.remove(rowNr);
        return session;
    }

}

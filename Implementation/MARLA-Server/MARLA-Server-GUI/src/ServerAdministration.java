import EnvironmentPluginAPI.Exceptions.TechnicalException;
import GameServerFacade.Interface.ServerFacadeFactory;
import GameServerFacade.Interface.IServerFacade;
import Models.SessionTableModel;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.RemoteInterface.ClientSocketFactory;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ServerRunner.Interface.IPlayerEventHandler;
import ZeroTypes.TransportTypes.TClientEvent;
import ZeroTypes.TransportTypes.TSession;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;

/**
 * Provides GUI GameLogic for the Server and thus is the starting point of the whole ServerApp.
 */
public class ServerAdministration extends JFrame implements Observer {
    private JPanel panel1;
    private JButton addSessionButton;
    private JButton deleteSessionButton;
    private SessionConfig sessionConfig;
    private JTable sessionList;
    private JPanel sessionListPanel;
    private JButton startSessionsButton;
    private JTextField statisticsAddressTextField;
    private JButton hostStatisticsButton;
    private JCheckBox hostStatisticsCheckBox;
    private JButton startHostingButton;
    private SessionTableModel sessionListModel;

    private Registry registry;

    private ICycleStatistics stub;

    private static JFrame frame;

    public ServerAdministration(final IServerFacade facade) {
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        // Add Main GUI as Observer to sessionConfig
        sessionConfig.addObserver(this);

        // update Session Status in GUI
        // #TODO: create real Session Model
        facade.subscribeForPlayerEvent(new IPlayerEventHandler() {
            @Override
            public void call(TClientEvent event) {
//                SessionStatus status = event.getSessionReference().getStatus();
            }
        });

        // Setup SessionList
        sessionList.setModel(new SessionTableModel());
        sessionListModel = (SessionTableModel) sessionList.getModel();

        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                sessionConfig.enableSessionConfig();
            }
        };

        sessionList.getSelectionModel().addListSelectionListener(selectionListener);

        addSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sessionConfig.enableSessionConfig();
            }
        });

        startSessionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                facade.startAllReadySessions();
            }
        });

        deleteSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });



        hostStatisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    ClientSocketFactory socketFactory = new ClientSocketFactory(InetAddress.getByName(statisticsAddressTextField.getText()));

                    System.setSecurityManager(new RMISecurityManager() {
                        public void checkPermission(java.security.Permission permission) {
                        }

                        public void checkPermission(java.security.Permission permission, java.lang.Object o) {
                        }
                    });

                    stub = (ICycleStatistics) UnicastRemoteObject.exportObject(new RMIServiceConnector(facade), 0, socketFactory, null);

                    // Bind remote objects stub in registry
                    registry = LocateRegistry.createRegistry(1099);

                    registry.bind("ICycleStatistics", stub);
                    hostStatisticsButton.setEnabled(false);
                    hostStatisticsButton.setText("Hosting...");
                    statisticsAddressTextField.setEnabled(false);
                } catch (AccessException e1) {
                    JOptionPane.showMessageDialog(frame,
                            "Hosting of the statistics component could not be initiated, probably an RMI problem.",
                            "Remote Access Problem",
                            JOptionPane.ERROR_MESSAGE);
                } catch (RemoteException e1) {
                    JOptionPane.showMessageDialog(frame,
                            "Hosting of the statistics component could not be initiated, probably an RMI problem.",
                            "Remote Access Problem",
                            JOptionPane.ERROR_MESSAGE);
                } catch (AlreadyBoundException e1) {
                    JOptionPane.showMessageDialog(frame,
                            "The connection is already established. Are you running two instances of Overseer?",
                            "Connection already established!",
                            JOptionPane.ERROR_MESSAGE);
                } catch (UnknownHostException e1) {
                    JOptionPane.showMessageDialog(frame,
                            "The host address can't be resolved, please check it!",
                            "Unknown Host!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        sessionListModel.addSession((TSession) o);
    }

}

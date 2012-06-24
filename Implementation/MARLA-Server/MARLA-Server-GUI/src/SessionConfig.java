import Enumeration.SessionStatus;
import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import GameServerFacade.Interface.IServerFacade;
import GameServerFacade.Interface.ServerFacadeFactory;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import Settings.SettingException;
import TransportTypes.TNetworkClient;
import TransportTypes.TSession;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

/**
 * User: chhuening
 * Date: 18.05.12
 * Time: 13:08
 */
public class SessionConfig extends Observable {
    private JButton refreshButton;
    private JButton addClientToSession;
    private JButton removeClientFromSession;
    private JPanel sessionConfigPanel;
    private JTextField sessionNameTextField;
    private JButton saveSessionButton;
    private JButton cancelButton;
    private JTextField gameNumberTextField;
    private JPanel aiConfigButtonPanel;
    private JPanel mainConfigPanel;
    private JTextField mapSeedTextField;
    private JTextField edgeFieldTextField;
    private JTextField factoryFactorTextField;
    private JTextField factorySizeFactorTextField;
    private JComboBox mapSymmetryComboBox;
    private JPanel aiClientConfigPanel;
    private JTable clientsAvailableTable;
    private JTable clientsInSessionTable;
    private JTextField maximumFactorySizeTextField;
    private JLabel maximumFactorySizeLabel;
    private JComboBox allMapsComboBox;
    private JTextField mapNameTextField;
    private JButton saveMapButton;
    private JTable mapList;
    private JComboBox environmentComboBox;
    private ClientTableModel clientsInSessionTableModel;
    private ClientTableModel clientsAvailableTableModel;
    private MapListTableModel mapListTableModel;

    private final IServerFacade facade;
    private boolean mapEdited = false;

    private EnvironmentComboBoxModel environmentComboBoxModel;
    private List<TEnvironmentDescription> environmentDescriptions;

    public SessionConfig() throws TechnicalException, SettingException, PluginNotReadableException {

        this.facade = ServerFacadeFactory.getProductiveApplicationCore();

        try {
            environmentDescriptions = facade.listAvailableEnvironments();
        } catch (TechnicalException e) {
            e.printStackTrace();
        } catch (PluginNotReadableException e) {
            e.printStackTrace();
        } catch (SettingException e) {
            e.printStackTrace();
        }

        environmentComboBoxModel = new EnvironmentComboBoxModel();
        environmentComboBox.setModel(environmentComboBoxModel);
        environmentComboBoxModel.addEnvironmentDescriptions(environmentDescriptions);


        sessionConfigPanel.setVisible(false);


        clientsAvailableTable.setModel(new ClientTableModel());
        clientsInSessionTable.setModel(new ClientTableModel());

        mapList.setModel(new MapListTableModel());


        clientsInSessionTableModel = (ClientTableModel) clientsInSessionTable.getModel();
        clientsAvailableTableModel = (ClientTableModel) clientsAvailableTable.getModel();
        mapListTableModel = (MapListTableModel) mapList.getModel();

        updateMapList();

        mapList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(mapList.getSelectedRow() > -1 && mapListTableModel.getSize() >= mapList.getSelectedRow()){
                    fillGUIWithMapMetaData(mapListTableModel.get(mapList.getSelectedRow()));
                }
            }

        });


        // TODO: Error Messages einsammeln und in einem Dialog anzeigen
        addClientToSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int index : clientsAvailableTable.getSelectedRows()) {
                    try {
                        TNetworkClient elem = clientsAvailableTableModel.removeClientAt(index);
                        clientsInSessionTableModel.addClient(elem);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        // TODO: Error Messages einsammeln und in einem Dialog anzeigen
        removeClientFromSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int index : clientsInSessionTable.getSelectedRows()) {
                    try {
                        TNetworkClient elem = clientsInSessionTableModel.removeClientAt(index);
                        clientsAvailableTableModel.addClient(elem);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // TODO: Error Messages einsammeln und in einem Dialog anzeigen
        saveSessionButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {

                    List<TNetworkClient> clients = clientsInSessionTableModel.getAllClients();

                    TSession session = new TSession(UUID.randomUUID(), sessionNameTextField.getText(),
                            SessionStatus.READY,
                            clientsInSessionTableModel.getRowCount(),
                            Integer.parseInt(gameNumberTextField.getText()),
                            clients,
                            createMapMetaDataFromGUI(),
                            environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex()));

                    facade.createSession(session);

                    setChanged();
                    notifyObservers(session);
                    sessionConfigPanel.setVisible(false);

                } catch (UnknownHostException e) {

                } catch (PluginNotReadableException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (TechnicalException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clientsAvailableTableModel.removeAllClients();
                clientsAvailableTableModel.addClients(facade.getFreeClients());
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sessionConfigPanel.setVisible(false);
            }
        });

        saveMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    TMapMetaData map = createMapMetaDataFromGUI();
                    facade.saveMap(map, environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex()));
                    //updateMapList();
                    mapListTableModel.addMap(map);
                    saveMapButton.setEnabled(true);
                } catch (TechnicalException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (PluginNotReadableException e) {
                    // TODO: WTF? why is there a pluginnotreadableexception here
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    private void updateMapList() {
        try {
            mapList.clearSelection();
            mapListTableModel.removeAllMaps();
            if(environmentComboBox.getItemCount() > 0){
                TEnvironmentDescription env = environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex());
                mapListTableModel.addMaps(facade.getAvailableMaps(env));
            }
        } catch (CorruptMapFileException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TechnicalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PluginNotReadableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void fillGUIWithMapMetaData(TMapMetaData metaData) {
        mapNameTextField.setText(metaData.getName());
        mapSymmetryComboBox.setSelectedIndex(metaData.getSymmetry());

        edgeFieldTextField.setText("" + metaData.getEdgeLength());
        mapSeedTextField.setText("" + metaData.getSeed());
        factoryFactorTextField.setText("" + metaData.getFactoryNumberFactor());
        factorySizeFactorTextField.setText("" + metaData.getFactorySizeFactor());
        maximumFactorySizeTextField.setText("" + metaData.getMaximumFactorySize());
    }

    private TMapMetaData createMapMetaDataFromGUI() {
        // Create Map Metadata from FormComponents
        int symmetry = mapSymmetryComboBox.getSelectedIndex();
        Integer edgeLength = Integer.parseInt(edgeFieldTextField.getText());
        Integer seed = Integer.parseInt(mapSeedTextField.getText());
        Integer factoryFactor = Integer.parseInt(factoryFactorTextField.getText());
        Integer factorySizeFactor = Integer.parseInt(factorySizeFactorTextField.getText());
        Integer maximumFactorySize = Integer.parseInt(maximumFactorySizeTextField.getText());

        return new TMapMetaData(mapNameTextField.getText(), symmetry, seed, edgeLength, factoryFactor, factorySizeFactor, maximumFactorySize);
    }

    /**
     * Cleares SessionConfig GUI for new Session
     */
    public void enableSessionConfig() {
        this.gameNumberTextField.setText("");
        this.sessionNameTextField.setText("");
        this.edgeFieldTextField.setText("");
        this.factoryFactorTextField.setText("");
        this.factorySizeFactorTextField.setText("");
        this.mapSeedTextField.setText("");
        this.maximumFactorySizeTextField.setText("");

        this.clientsAvailableTableModel.removeAllClients();
        this.clientsAvailableTableModel.addClients(facade.getFreeClients());

        clientsInSessionTableModel.removeAllClients();

        this.sessionConfigPanel.setVisible(true);
    }

}

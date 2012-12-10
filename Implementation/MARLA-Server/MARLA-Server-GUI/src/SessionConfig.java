import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import Models.ConfigurationListTableModel;
import ZeroTypes.Enumerations.SessionStatus;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import GameServerFacade.Interface.IServerFacade;
import GameServerFacade.Interface.ServerFacadeFactory;
import Models.ClientTableModel;
import Models.EnvironmentComboBoxModel;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Exceptions.ErrorMessages;
import ZeroTypes.Settings.SettingException;
import ZeroTypes.TransportTypes.TNetworkClient;
import ZeroTypes.TransportTypes.TSession;

import javax.swing.*;
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
    private JComboBox allMapsComboBox;
    private JTextField mapNameTextField;
    private JButton saveMapButton;
    private JTable configurationList;
    private JComboBox environmentComboBox;
    private ClientTableModel clientsInSessionTableModel;
    private ClientTableModel clientsAvailableTableModel;
    private ConfigurationListTableModel configurationListTableModel;

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

        configurationList.setModel(new ConfigurationListTableModel());


        clientsInSessionTableModel = (ClientTableModel) clientsInSessionTable.getModel();
        clientsAvailableTableModel = (ClientTableModel) clientsAvailableTable.getModel();
        configurationListTableModel = (ConfigurationListTableModel) configurationList.getModel();

        updateConfigurationList();

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
                            configurationListTableModel.get(configurationList.getSelectedRow()),
                            clientsInSessionTableModel.getRowCount(),
                            Integer.parseInt(gameNumberTextField.getText()),
                            clients,
                            environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex()));

                    facade.createSession(session);

                    setChanged();
                    notifyObservers(session);
                    sessionConfigPanel.setVisible(false);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (PluginNotReadableException e) {
                    e.printStackTrace();
                } catch (TechnicalException e) {
                    e.printStackTrace();
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
    }

    private void updateConfigurationList() {
        try {
            configurationList.clearSelection();
            configurationListTableModel.removeAllConfigurations();
            if (environmentComboBox.getItemCount() > 0) {
                TEnvironmentDescription env = environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex());
                configurationListTableModel.addConfigurations(facade.getAvailableConfigurations(env));
            }
        } catch (CorruptConfigurationFileException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (TechnicalException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (PluginNotReadableException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (AbstractMethodError e) {
            JOptionPane.showMessageDialog(null,
                    ErrorMessages.get("pluginCompiledAgainstIncompatibleVersion", e.getMessage()),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears SessionConfig GUI for new Session
     */
    public void enableSessionConfig() {
        this.gameNumberTextField.setText("");
        this.sessionNameTextField.setText("");

        this.clientsAvailableTableModel.removeAllClients();
        this.clientsAvailableTableModel.addClients(facade.getFreeClients());

        clientsInSessionTableModel.removeAllClients();

        this.sessionConfigPanel.setVisible(true);
    }

}

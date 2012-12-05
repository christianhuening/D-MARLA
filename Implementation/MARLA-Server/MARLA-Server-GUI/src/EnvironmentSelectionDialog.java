import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import GameServerFacade.Interface.IServerFacade;
import Models.EnvironmentListTableModel;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Settings.SettingException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnvironmentSelectionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable tableAvailableEnvironments;
    private EnvironmentListTableModel tableModel;
    private final IServerFacade serverFacade;
    private TEnvironmentDescription selectedEnvironmentDescription = null;


    public EnvironmentSelectionDialog(IServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        tableModel = new EnvironmentListTableModel();
        tableAvailableEnvironments.setModel(tableModel);

        try {
            tableModel.addAllEnvironments(serverFacade.listAvailableEnvironments());
        } catch (TechnicalException e) {
            e.printStackTrace();  //TODO: Needs better exception handling
        } catch (PluginNotReadableException e) {
            e.printStackTrace();  //TODO: Needs better exception handling
        } catch (SettingException e) {
            e.printStackTrace();  //TODO: Needs better exception handling
        }



        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        selectedEnvironmentDescription = tableModel.get(tableAvailableEnvironments.getSelectedRow());
        dispose();
    }

    public TEnvironmentDescription getSelectedEnvironment() {
        return selectedEnvironmentDescription;
    }
}

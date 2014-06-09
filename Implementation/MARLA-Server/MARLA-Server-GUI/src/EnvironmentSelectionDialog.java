import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import GameServerFacade.Interface.IServerFacade;
import GameServerFacade.Interface.ServerFacadeFactory;
import Models.EnvironmentListTableModel;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Exceptions.ErrorMessages;
import ZeroTypes.Settings.SettingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RMISecurityManager;

public class EnvironmentSelectionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable tableAvailableEnvironments;
    private EnvironmentListTableModel tableModel;
    private static final IServerFacade serverFacade = ServerFacadeFactory.getProductiveApplicationCore();
    private TEnvironmentDescription selectedEnvironmentDescription = null;


    public EnvironmentSelectionDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        tableModel = new EnvironmentListTableModel();
        tableAvailableEnvironments.setModel(tableModel);

        try { //TODO: Needs better exception handling
            tableModel.addAllEnvironments(serverFacade.listAvailableEnvironments());
        } catch (TechnicalException e) {
            e.printStackTrace();
        } catch (PluginNotReadableException e) {
            e.printStackTrace();
        } catch (SettingException e) {
            e.printStackTrace();
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        EnvironmentSelectionDialog dialog = new EnvironmentSelectionDialog();
        dialog.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - dialog.getWidth()) / 2;
        int y = (dim.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    private void onOK() {
        selectedEnvironmentDescription = tableModel.get(tableAvailableEnvironments.getSelectedRow());
        try { //TODO: Needs better exception handling
            serverFacade.loadEnvironmentPlugin(selectedEnvironmentDescription);
            serverFacade.startHosting();
        } catch (PluginNotReadableException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch(AbstractMethodError e) {
            JOptionPane.showMessageDialog(this,
                    ErrorMessages.get("pluginCompiledAgainstIncompatibleVersion", e.getClass().toString()),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (TechnicalException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ConnectionLostException e) {
            JOptionPane.showMessageDialog(this,
                    "The connection to the server was lost.",
                    "Connection Lost",
                    JOptionPane.ERROR_MESSAGE);
        }

        dispose();

        new ServerAdministration(serverFacade);
    }
}

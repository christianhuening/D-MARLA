package OverseerGUI;

import EnvironmentPluginAPI.Contract.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import Exceptions.GameReplayNotContainedInDatabaseException;
import PluginLoader.Implementation.EnvironmentPluginLoaderComponent;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import RemoteInterface.ICycleStatistics;
import Settings.AppSettings;
import Settings.SettingException;
import TransportTypes.TCycleReplayDescription;
import org.joda.time.DateTime;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


public class OverseerMain {
    private JPanel mainPanel;
    private JTextField serverAddressTextField;
    private JButton connectToServerButton;
    private JList playerList;
    private JLabel winLoseRatioLabel;
    private JLabel totalPlayerGamesLabel;
    private JLabel wonGamesLabel;
    private JLabel lostGamesLabel;
    private JLabel averageTurnLabel;
    private JSpinner gameSpinner;
    private JButton refreshButton;
    private JLabel connectionLabel;
    private JPanel gamePanel;
    private JLabel currentGamesPerMinuteLabel;
    private JTable gameReplayTable;
    private GameReplayForm gameReplayForm;
    private JComboBox environmentComboBox;
    private JButton refreshReplaysButton;
    private JButton refreshPlayersButton;

    private String remoteHost;
    private ICycleStatistics gameStatistics;
    private List<TCycleReplayDescription> gameDescs;


    private Registry registry;
    private DefaultListModel playerListModel;
    private CycleDescriptionTableModel cycleDescriptionTableModel;
    private EnvironmentComboBoxModel environmentComboBoxModel;

    private IEnvironmentPluginLoader pluginLoader;
    private List<TEnvironmentDescription> environmentDescriptions;


    private static JFrame frame;

    public OverseerMain() {

        // Load available EnvironmentPlugins
        try {
            pluginLoader = new EnvironmentPluginLoaderComponent();
        } catch (TechnicalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SettingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PluginNotReadableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            environmentDescriptions = pluginLoader.listAvailableEnvironments();
            //AppSettings.getString("environmentPluginsFolder")
        } catch (TechnicalException e) {
            e.printStackTrace();
        } catch (PluginNotReadableException e) {
            e.printStackTrace();
        } catch (SettingException e) {
            e.printStackTrace();
        }

        // GUI Init
        playerListModel = new DefaultListModel();
        cycleDescriptionTableModel = new CycleDescriptionTableModel();
        environmentComboBoxModel = new EnvironmentComboBoxModel();

        gameReplayTable.setModel(cycleDescriptionTableModel);
        playerList.setModel(playerListModel);
        environmentComboBox.setModel(environmentComboBoxModel);

        gameSpinner.setValue(1000);

        // add all found Environments to the comboBox Model for Selection
        if (environmentDescriptions != null) {
            environmentComboBoxModel.addEnvironmentDescriptions(environmentDescriptions);
        }


        // Start GameReplay Visualisation when new Replay is selected
        gameReplayTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (gameReplayTable.getSelectedRow() <= cycleDescriptionTableModel.getRowCount()) {

                    // get GameDescription for Selected Replay
                    TCycleReplayDescription desc = cycleDescriptionTableModel.getGameDescription(gameReplayTable.getSelectedRow());
                    // Get GameReplay from Server by UUID
                    ICycleReplay replay = null;

                    try {
                        replay = gameStatistics.getCycleReplay(desc.getReplayID(), environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex()));
                        gameReplayForm.setGameReplay(replay);
                    } catch (GameReplayNotContainedInDatabaseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (RemoteException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (TechnicalException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
            }
        });


        /**
         * Connects the Overseer with the provided server
         */
        connectToServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                remoteHost = serverAddressTextField.getText();

                if (remoteHost.equals("") || remoteHost == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Please provide an address in the Server Address Field!",
                            "No Address entered",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    /**
                     * Start new Thread for RMI Connection, so that the GUI doesn't hang
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                connectToServerButton.setEnabled(false);
                                environmentComboBox.setEnabled(false);

                                TEnvironmentDescription environmentDescription = environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex());

                                registry = LocateRegistry.getRegistry(remoteHost);
                                gameStatistics = (ICycleStatistics) registry.lookup("IGameStatistics");

                                // Load Environment
                                pluginLoader.loadEnvironmentPlugin(environmentDescription);

                                // Get VisualizationPanel
                                AbstractVisualizeReplayPanel visualizeReplayPanel = pluginLoader.getReplayVisualizationForSwing();

                                // Add VisualizationPanel to GameReplayForm
                                gameReplayForm.setVisualizationPlugin(visualizeReplayPanel);

                                // get Available Players from Server
                                playerListModel.removeAllElements();
                                for (String name : gameStatistics.getClientNames(environmentDescription)) {
                                    playerListModel.addElement(name);
                                }

                                // get Available GameReplayDescriptions for the Last minute
                                // #TODO: GameStatistics should have a method which allows for retreival of the last n-Games instead of a time interval
                                gameDescs = gameStatistics.getCycleReplayDescriptionsByDeltaTime(DateTime.now().minusDays(1), DateTime.now(), environmentDescription);
                                cycleDescriptionTableModel.removeAllGameDescriptions();
                                cycleDescriptionTableModel.addGameDescriptions(gameDescs);


                                connectionLabel.setText("Connected");
                                connectionLabel.setForeground(Color.green);

                                environmentComboBox.setEnabled(true);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                                connectionLabel.setText("Not Connected");
                                connectionLabel.setForeground(Color.red);
                                connectToServerButton.setEnabled(true);
                                environmentComboBox.setEnabled(true);
                            } catch (NotBoundException e) {
                                e.printStackTrace();
                                connectionLabel.setText("Not Connected");
                                connectionLabel.setForeground(Color.red);
                                connectToServerButton.setEnabled(true);
                                environmentComboBox.setEnabled(true);
                            } catch (TechnicalException e) {
                                e.printStackTrace();
                                connectionLabel.setText("Not Connected");
                                connectionLabel.setForeground(Color.red);
                                connectToServerButton.setEnabled(true);
                                environmentComboBox.setEnabled(true);
                            } catch (PluginNotReadableException e) {
                                e.printStackTrace();
                                connectionLabel.setText("Not Connected");
                                connectionLabel.setForeground(Color.red);
                                connectToServerButton.setEnabled(true);
                                environmentComboBox.setEnabled(true);
                            }
                        }
                    }).start();
                }
            }
        });

        /**
         * Retrieves values from GameServer for the selected Player
         */
        playerList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!playerList.getValueIsAdjusting()) {
                    updateStats();
                }
            }
        });


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStats();
            }
        });


        environmentComboBox.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (environmentComboBox.getItemCount() > 0) {
                    connectToServerButton.setEnabled(true);
                }
            }
        });

    }

    /**
     * Update statistics
     * TODO: also update PlayerList and GameRepays
     */
    public void updateStats() {
        if (playerList.getSelectedIndices().length > 2) {
            JOptionPane.showMessageDialog(frame,
                    "Please select only 2 or less players!",
                    "Too many players selected",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String na = "N / A";
                wonGamesLabel.setText(na);
                lostGamesLabel.setText(na);
                totalPlayerGamesLabel.setText(na);
                averageTurnLabel.setText(na);
                currentGamesPerMinuteLabel.setText(na);
                winLoseRatioLabel.setText(na);

                TEnvironmentDescription selectedEnvironment = environmentComboBoxModel.getEnvironmentDescription(environmentComboBox.getSelectedIndex());

                String selectedPlayer = (String) playerListModel.get(playerList.getSelectedIndex());
                Integer won = gameStatistics.getTotalNumberOfCyclesWon(selectedPlayer, selectedEnvironment);
                wonGamesLabel.setText(won.toString());

                Integer lost = gameStatistics.getTotalNumberOfCyclesLost(selectedPlayer, selectedEnvironment);
                lostGamesLabel.setText(lost.toString());

                Integer played = gameStatistics.getTotalNumberOfCycles(selectedPlayer, selectedEnvironment);
                totalPlayerGamesLabel.setText(played.toString());

                Float averageTurns = gameStatistics.getAverageTurnsPerCycle(selectedPlayer, (Integer) gameSpinner.getValue(), selectedEnvironment);
                averageTurnLabel.setText(averageTurns.toString());

                Float gamesPerMinute = gameStatistics.getCurrentGamesPerMinute(selectedEnvironment);
                currentGamesPerMinuteLabel.setText(gamesPerMinute.toString());

                if (playerList.getSelectedIndices().length == 2) {
                    String otherPlayer = (String) playerListModel.get(playerList.getSelectedIndices()[1]);
                    Float ratio = gameStatistics.getWinLoseRatio(selectedPlayer, otherPlayer, selectedEnvironment);
                    winLoseRatioLabel.setText(ratio.toString());
                }

            } catch (RemoteException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (TechnicalException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("OverseerMain");
        frame.setContentPane(new OverseerMain().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

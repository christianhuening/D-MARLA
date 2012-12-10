import AIClientFacade.Interface.AIClientApplicationCoreFactory;
import AIClientFacade.Interface.IAIClientFacade;
import AIRunner.Interface.AIRunnerEventType;
import AIRunner.Interface.IAIRunnerEventHandler;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Settings.SettingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AIClientMain {


    private JPanel panel1;
    private JTable agentTable;
    private JButton connectButton;
    private JTextField serverPortTextField;
    private JTextField serverIPTextField;
    private JLabel connectionLabel;
    private JProgressBar progressBar1;
    private IAIClientFacade facade;
    private AgentTableModel agentTableModel;

    private static JFrame frame;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("AIClientMain");
        frame.setContentPane(new AIClientMain().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public AIClientMain() {

        //retrieve AppCore
        facade = AIClientApplicationCoreFactory.getProductionApplicationCore();


        /**
         * register an anonymous class as listener to network events, change GUI accordingly
         */
        facade.addListener( new IAIRunnerEventHandler() {
            @Override
            public void onAIRunnerEvent(final AIRunnerEventType eventType) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if(eventType == AIRunnerEventType.CycleEnded){
                            progressBar1.setValue(progressBar1.getValue()+1);
                        }

                        if(eventType == AIRunnerEventType.SessionEnded){

                        }

                        if(eventType == AIRunnerEventType.Connected){
                            connectionLabel.setForeground(Color.green);
                            connectionLabel.setText("Connected");
                            connectButton.setEnabled(false);
                        }

                        if(eventType == AIRunnerEventType.ConnectionLost || eventType == AIRunnerEventType.Disconnected){
                            connectionLabel.setForeground(Color.red);
                            connectionLabel.setText("Not Connected");
                            connectButton.setEnabled(true);
                        }
                    }
                });

            }

            @Override
            public void onSessionStart(int games) {
                progressBar1.setMinimum(0);
                progressBar1.setMaximum(games);
                progressBar1.setValue(0);
            }

            @Override
            public void onException(Exception exception) {
                JOptionPane.showMessageDialog(frame,
                        exception.toString(),
                        "Whoops, an Error occurred",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create new TableModel and fill it with available Agentsystems
        agentTableModel = new AgentTableModel();
        try {
            agentTableModel.addAgents(facade.getAvailableAgentSystems());
        } catch (TechnicalException e) {
            e.printStackTrace();
        } catch (PluginNotReadableException e) {
            e.printStackTrace();
        } catch (SettingException e) {
            e.printStackTrace();
        }
        agentTable.setModel(agentTableModel);



        /**
         * Connects the selected AgentSystem with the provided server
         */
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if(agentTable.getSelectedRow() == -1){
                    JOptionPane.showMessageDialog(frame,
                            "Please select an Agentsystem. If you don't have one...: Good luck!",
                            "No Agentsystem selected",
                            JOptionPane.ERROR_MESSAGE);
                } else if(serverIPTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Please provide a Server-IP",
                            "No Server-IP entered",
                            JOptionPane.ERROR_MESSAGE);
                } else if(serverPortTextField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(frame,
                            "Please provide a Server-Port",
                            "No Server-Port entered",
                            JOptionPane.ERROR_MESSAGE);
                } else {

                    try {
                        TAgentSystemDescription agentSystem = agentTableModel.getSelectedAgent(agentTable.getSelectedRow());
                        facade.connectToServer(agentSystem, serverIPTextField.getText(), Integer.parseInt(serverPortTextField.getText()));
                    } catch (HostUnreachableException e1) {
                        e1.printStackTrace();
                    } catch (TechnicalException e1) {
                        e1.printStackTrace();
                    } catch (PluginNotReadableException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


    }
}

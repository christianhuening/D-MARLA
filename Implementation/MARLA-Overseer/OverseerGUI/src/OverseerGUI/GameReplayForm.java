package OverseerGUI;

import EnvironmentPluginAPI.Service.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.IVisualizeReplay;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameReplayForm {
    private JPanel panel1;
    private JButton stepBackButton;
    private JButton playButton;
    private JButton stepForwardButton;
    private JButton pauseButton;
    private JPanel gamePanel;
    private JSlider replaySpeedSlider;

    private ICycleReplay replay;
    private IVisualizeReplay visualizeReplay;
    private boolean swing;

    public GameReplayForm() {

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (replay != null) {
                    visualizeReplay.play();
                    pauseButton.setEnabled(true);
                }
            }
        });

        stepForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (replay != null) {
                    visualizeReplay.stepForward();
                }
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizeReplay.pause();
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });

        replaySpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!replaySpeedSlider.getValueIsAdjusting() && visualizeReplay != null) {
                    visualizeReplay.changeReplaySpeed(replaySpeedSlider.getValue());
                }
            }
        });

        stepBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (replay != null) {
                    visualizeReplay.stepBackward();
                }
            }
        });
    }

    public void setGameReplay(ICycleReplay replay) {
        this.replay = replay;
        visualizeReplay.setGameReplay(replay);
    }

    public void setVisualizationPlugin(IVisualizeReplay plugin) {
        if (plugin != null) { //if a visualization plugin is set

            this.visualizeReplay = plugin;
            swing = plugin instanceof AbstractVisualizeReplayPanel;
            gamePanel.setLayout(new BorderLayout());

            if (swing) {
                gamePanel.add((AbstractVisualizeReplayPanel) visualizeReplay);
                gamePanel.revalidate();
            }

            visualizeReplay.changeReplaySpeed(replaySpeedSlider.getValue());
        } else { //if ne plugin was provided disable all controls
            pauseButton.setEnabled(false);
            playButton.setEnabled(false);
            stepBackButton.setEnabled(false);
            stepBackButton.setEnabled(false);
        }
    }
}

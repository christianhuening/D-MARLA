package EnvironmentPluginAPI.Service;


import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.IVisualizeReplay;

import javax.swing.*;
import java.awt.*;

/**
 * An abstract implementation of IVisualizeReplay, which basically extends JPanel to be usable in a Java Swing GUI.
 * Derived classes must implement paintComponent(). Make sure you call super.paintComponent(g) first!
 */
public abstract class AbstractVisualizeReplayPanel extends JPanel implements IVisualizeReplay {

    /**
     * An abstract JPanel derived class which utilizes Java's Graphics Library to draw a GameReplay for the Factory Game
     */
    public AbstractVisualizeReplayPanel() {}

    public abstract void setGameReplay(ICycleReplay replay);

    public abstract void stepForward();

    public abstract void stepBackward();

    public abstract void pause();

    /**
     * Sets ReplaySpeed to speed and re-initializes the replay
     * @param speed
     */
    public abstract void changeReplaySpeed(long speed);


    /**
     * Will replay the game stepForward by stepForward with 500 ms between each stepForward
     */
    public abstract void play();

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    };

}
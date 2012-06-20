package EnvironmentPluginAPI.Contract;

import EnvironmentPluginAPI.Service.ICycleReplay;

/**
 *  If one wants to be able to use a visualization of the Environment, this interface needs to be implemeted.
 *  Notice: If you want to use a Swing GUI Vis., then use 'AbstractVisualizeReplayPanel' instead.
 *  It'll give you a JPanel derived abstract class. Implement your drawing logic inside of paintComponent()
 */
public interface IVisualizeReplay {

    /**
     * Sets replay as the current GameReplay used in the visualization
     * @param replay
     */
    public void setGameReplay(ICycleReplay replay);

    /**
     * Shifts the Environment one step forward
     * @pre: setGameReplay must have been called
     */
    public void stepForward();

    /**
     * Shifts the Environment one step backward
     * @pre: setGameReplay must have been called
     */
    public void stepBackward();

    /**
     * Shifts the Environment continuously forward
     * @pre: setGameReplay must have been called
     */
    public void play();

    /**
     * Pauses play(), assumes that the current State is being stored for later resuming.
     * @pre: setGameReplay must have been called
     */
    public void pause();

    /**
     * Sets the period between two steps in play() to the value of speed in ms.
     * @param speed
     * @pre: setGameReplay must have been called
     */
    public void changeReplaySpeed(long speed);


}

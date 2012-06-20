package Factory.Visualization;


import EnvironmentPluginAPI.Contract.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Service.ICycleReplay;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class VisualizeReplay extends AbstractVisualizeReplayPanel {
    BufferedImage normalField;
    BufferedImage factoryFieldNeutral;
    BufferedImage factoryFieldBlue;
    BufferedImage factoryFieldRed;
    BufferedImage influenceField;
    BufferedImage unitBlue;
    BufferedImage unitRed;
    private ICycleReplay replay;
    private List<TGameState> gameStates;
    private ListIterator<TGameState> gameStateIterator;

    private boolean doStep;
    private Timer playTimer;
    private long replaySpeed;
    private TGameState gameStateToPaint;


    /**
     * A JPanel derived class which utilizes Java's Graphics Library to draw a GameReplay for the Factory Game
     */
    public VisualizeReplay() {
        try {
            // TODO: Get Imagepaths right
            normalField = ImageIO.read(new File("OverseerGUI/src/resources/images/normalField.png"));
            factoryFieldBlue = ImageIO.read(new File("OverseerGUI/src/resources/images/factoryFieldBlue.png"));
            factoryFieldRed = ImageIO.read(new File("OverseerGUI/src/resources/images/factoryFieldRed.png"));
            factoryFieldNeutral = ImageIO.read(new File("OverseerGUI/src/resources/images/factoryFieldNeutral.png"));
            unitBlue = ImageIO.read(new File("OverseerGUI/src/resources/images/unitBlue.png"));
            unitRed = ImageIO.read(new File("OverseerGUI/src/resources/images/unitRed.png"));
            influenceField = ImageIO.read(new File("OverseerGUI/src/resources/images/influenceField.png"));
        } catch (IOException ie) {
            System.out.println("Error:" + ie.getMessage());
        }

        // Important variable to make sure
        doStep = false;
        replaySpeed = 500;
    }

    public void setGameReplay(ICycleReplay replay) {
        if (playTimer != null) {
            playTimer.cancel();
        }
        this.replay = replay;
        gameStates = this.replay.getEnvironmentStatesPerTurn();
        gameStateIterator = (ListIterator) gameStates.iterator();
    }

    public void stepForward() {
        if (gameStateIterator != null && gameStateIterator.hasNext()) {
            doStep = true;
            gameStateToPaint = gameStateIterator.next();
            repaint();
        }
    }

    public void stepBackward() {
        if (gameStateIterator != null && gameStateIterator.hasPrevious()) {
            doStep = true;
            gameStateToPaint = gameStateIterator.previous();
            repaint();
        }
    }

    public void pause() {
        if (playTimer != null) {
            playTimer.cancel();
        }
    }

    /**
     * Sets ReplaySpeed to speed and re-initializes the replay
     *
     * @param speed
     */
    public void changeReplaySpeed(long speed) {
        this.replaySpeed = speed;
        this.pause();
        this.play();
    }


    /**
     * Will replay the game stepForward by stepForward with 500 ms between each stepForward
     */
    public void play() {
        if (gameStateIterator != null && gameStateIterator.hasNext()) {
            playTimer = new Timer();

            playTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (gameStateIterator.hasNext()) {
                        doStep = true;
                        gameStateToPaint = gameStateIterator.next();
                        repaint();
                    } else {
                        playTimer.cancel();
                    }
                }
            }, 10, replaySpeed);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameStateToPaint != null && doStep) {
            // set to false to make sure just one single stepForward is being printed
            doStep = false;

            TAbstractField[][] fields = gameStateToPaint.getMapFields();

            for (int i = 0; i < fields.length; i++) {

                int j = 0;

                for (TAbstractField field : fields[i]) {

                    BufferedImage fieldToDraw = normalField;
                    BufferedImage unitToDraw = null;
                    Faction faction = Faction.NEUTRAL;

                    if (field.isOccupied()) {
                        faction = field.getOccupant().getControllingFaction();
                        if (faction == Faction.BLUE) {
                            unitToDraw = unitBlue;
                        } else if (faction == Faction.RED) {
                            unitToDraw = unitRed;
                        }
                    }

                    if (field instanceof TNormalField) {
                        fieldToDraw = normalField;

                    } else if (field instanceof TFactoryField) {

                        for (TFactory factory : gameStateToPaint.getFactories()) {
                            if (factory.getFactoryID() == ((TFactoryField) field).getFactoryID()) {
                                faction = factory.getOwningFaction();
                            }
                        }

                        if (faction == Faction.BLUE) {
                            fieldToDraw = factoryFieldBlue;
                        } else if (faction == Faction.RED) {
                            fieldToDraw = factoryFieldRed;
                        } else {
                            fieldToDraw = factoryFieldNeutral;
                        }

                    } else if (field instanceof TInfluenceField) {
                        fieldToDraw = influenceField;
                    }

                    g.drawImage(fieldToDraw, (i % fields[i].length) * fieldToDraw.getWidth(), j * fieldToDraw.getHeight(), null);
                    if (field.isOccupied()) {
                        g.drawImage(unitToDraw, (i % fields[i].length) * unitToDraw.getWidth(), j * unitToDraw.getHeight(), null);
                    }

                    j++;
                }

            }

        }
    }

}
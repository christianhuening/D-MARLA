package Actions;

import EnvironmentPluginAPI.Contract.IEnvironmentState;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 *  This class is used to tranpsort the state of the environment.
 *  In the case of the grid world example, there are
 */
public class CliffEnvironmentState implements IEnvironmentState, Serializable {

    private int x;
    private int y;
    private boolean hasClientMetGoal;
    private final float reward;

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    private final int gridWidth;
    private final int gridHeight;

    public CliffEnvironmentState(int x, int y, boolean hasClientMetGoal, float reward, int gridWidth, int gridHeight) {
        this.x = x;
        this.y = y;
        this.hasClientMetGoal = hasClientMetGoal;
        this.reward = reward;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean hasClientMetGoal() {
        return hasClientMetGoal;
    }

    public float getReward() {
        return reward;
    }

    public String getCompressedRepresentation() {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOut);
            out.write(gridWidth);
            out.write(gridHeight);
            out.write(x);
            out.write(y);
            out.close();
            return new String(byteOut.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

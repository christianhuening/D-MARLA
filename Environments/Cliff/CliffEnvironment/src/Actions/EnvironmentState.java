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
public class EnvironmentState implements IEnvironmentState, Serializable {

    private int x;
    private int y;
    private boolean hasClientMetGoal;
    private final float reward;

    public EnvironmentState(int x, int y, boolean hasClientMetGoal, float reward) {
        this.x = x;
        this.y = y;
        this.hasClientMetGoal = hasClientMetGoal;
        this.reward = reward;
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

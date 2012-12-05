package Logic;

import Actions.EnvironmentState;
import Actions.Direction;

import java.util.Random;

/**
 * This class implements the actual logic of one session in the "cliff" environment.
 */
public class Session {

    //a 2-dimensional array to represent the grid world.
    private GridType[][] grid;

    private int agentX;
    private int agentY;

    private GridWorldConfiguration configuration;

    public Session(GridWorldConfiguration configuration) {
        this.configuration = configuration;

        grid = new GridType[configuration.getWidth()][configuration.getHeight()];

        agentX = 0;
        agentY = 0;

        switch (configuration.getGridWorldStyle()) {
            case Cliff:
                generateCliff();
                break;
            case Random:
                initializeRandom();
        }

    }

    /**
     *  initializes the field randomly
     */
    private void initializeRandom() {
        Random random = new Random();
        float nr;
        for (int x = 0; x < configuration.getWidth(); x++) {
            for (int y = 0; y < configuration.getHeight(); y++) {
                nr = random.nextFloat();
                if(nr <= 0.15) {
                    grid[x][y] = GridType.GOAL;
                } else if(nr <= 0.3) {
                    grid[x][y] = GridType.PENALTY;
                } else {
                    grid[x][y] = GridType.NORMAL;
                }
            }
        }
    }

    /**
     * initializes the field with the types known of the cliff example
     */
    private void generateCliff() {
        // normal fields
        for (int x = 0; x < configuration.getWidth(); x++) {
            for (int y = 0; y < configuration.getHeight(); y++) {
                if (y > 0 || (x > 0 && x < configuration.getWidth() - 1)) {
                    grid[x][y] = GridType.NORMAL;
                }
            }
        }

        // cliff fields
        for (int x = 1; x < configuration.getWidth() - 1; x++) {
            grid[x][0] = GridType.PENALTY;
        }

        grid[0][0] = GridType.NORMAL; //the start
        grid[configuration.getWidth() - 1][0] = GridType.GOAL; //the goal
    }

    public boolean canAgentMoveTo(Direction direction) {
        if (direction == Direction.UP) {
            return agentY < configuration.getHeight();
        } else if (direction == Direction.RIGHT) {
            return agentX < configuration.getWidth() - 1;
        } else if (direction == Direction.DOWN) {
            return agentY > 0;
        } else {
            return agentX > 0;
        }
    }

    public void moveAgent(Direction direction) {
        if (!canAgentMoveTo(direction)
                || !isStillActive()) {
            throw new IllegalMoveException();
        }

        switch (direction) {
            case UP:
                agentY++;
                break;
            case RIGHT:
                agentX++;
                break;
            case DOWN:
                agentY--;
                break;
            case LEFT:
                agentX--;
                break;
        }
    }

    /**
     * True as long as the agent is not on the goal field, false else.
     *
     * @return see summary
     */
    public boolean isStillActive() {
        return grid[agentX][agentY] != GridType.GOAL;
    }

    public EnvironmentState getCurrentState() {
        float reward;

        switch (grid[agentX][agentY]) {
            case PENALTY:
                reward = -100f;
                break;
            default:
                reward = -1.0f;
        }
        return new EnvironmentState(agentX, agentY, !isStillActive(), reward);
    }
}

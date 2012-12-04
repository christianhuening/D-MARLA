package Logic;

import Actions.CliffEnvironmentState;
import Actions.Direction;

/**
 * This class implements the actual logic of one run in the "cliff" environment.
 */
public class CliffSession {

    //a 2-dimensional array to represent the gridworld.
    private GridType[][] grid;

    private int agentX;
    private int agentY;

    private int gridWidth;
    private int gridHeight;

    public CliffSession() {
        this.gridWidth = 20;
        this.gridHeight = 8;

        grid = new GridType[gridWidth][gridHeight];

        agentX = 0;
        agentY = 0;

        initializeGrid();
    }

    /**
     * initializes the field with the types known of the cliff example
     */
    private void initializeGrid() {
        // normal fields
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (y > 0 || (x > 0 && x < grid.length - 1)) {
                    grid[x][y] = GridType.NORMAL;
                }
            }
        }

        // cliff fields
        for (int x = 1; x < grid.length - 1; x++) {
            grid[x][0] = GridType.CLIFF;
        }

        grid[0][0] = GridType.NORMAL; //the start
        grid[grid.length - 1][0] = GridType.GOAL; //the goal
    }

    public boolean canAgentMoveTo(Direction direction) {
        if (direction == Direction.UP) {
            return agentY < grid[0].length;
        } else if (direction == Direction.RIGHT) {
            return agentX < grid.length - 1;
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

    public CliffEnvironmentState getCurrentState() {
        float reward;

        switch (grid[agentX][agentY]) {
            case CLIFF:
                reward = -100f;
                break;
            default:
                reward = -1.0f;
        }
        return new CliffEnvironmentState(agentX, agentY, !isStillActive(), reward, gridWidth, gridHeight);
    }
}

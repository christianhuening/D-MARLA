package Logic;

import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

/**
 * This class describes the configuration of a grid world as used in this environment.
 */
public class GridWorldConfiguration implements IEnvironmentConfiguration {

    private int width;
    private int height;
    private GridWorldStyle gridWorldStyle;

    public GridWorldConfiguration(int width, int height, GridWorldStyle gridWorldStyle) {
        this.width = width;
        this.height = height;
        this.gridWorldStyle = gridWorldStyle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GridWorldStyle getGridWorldStyle() {
        return gridWorldStyle;
    }

    @Override
    public String toString() {
        return String.format("%s ( %s |%s )", gridWorldStyle, width, height);
    }
}

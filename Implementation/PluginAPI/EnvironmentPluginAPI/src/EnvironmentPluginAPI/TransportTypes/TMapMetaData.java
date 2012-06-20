package EnvironmentPluginAPI.TransportTypes;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class TMapMetaData implements java.io.Serializable {

    public TMapMetaData(String name, int symmetry, int seed, int edgelength, int factoryNumberFactor, int factorySizeFactor, int maximumFactorySize) {
        this.name = name;
        this.symmetry = symmetry;
        this.seed = seed;
        this.edgelength = edgelength;
        this.factoryNumberFactor = factoryNumberFactor;
        this.factorySizeFactor = factorySizeFactor;
        this.maximumFactorySize = maximumFactorySize;
    }


    // the map's name
    private final String name;

    // 0 for horizontal symmetry ; 1 for horizontal and vertical symmetry
    private int symmetry;

    // seed for the randomnumber generator
    private int seed;

    /**
     * The map is allways a square
     */
    private int edgelength;

    /**
     * Chance for a factory to spawn.
     * Values between 0-1000.
     * useful values should be between 10 and 100
     */
    private int factoryNumberFactor;

    /**
     * defines the average factory size
     * Values between 1-100
     */
    private int factorySizeFactor;

    /**
     * defines the maximum Factory size (edgeLength)
     */
    private int maximumFactorySize;

    public String getName() {
        return name;
    }

    public int getSymmetry() {
        return symmetry;
    }

    public int getSeed() {
        return seed;
    }

    public int getEdgeLength() {
        return edgelength;
    }

    public int getFactoryNumberFactor() {
        return factoryNumberFactor;
    }

    public int getFactorySizeFactor() {
        return factorySizeFactor;
    }

    public int getMaximumFactorySize() {
        return maximumFactorySize;
    }

    @Override
    public String toString() {
        return name;
    }
}

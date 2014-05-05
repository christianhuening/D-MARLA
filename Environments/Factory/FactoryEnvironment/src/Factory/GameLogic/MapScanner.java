package Factory.GameLogic;


import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Factory.Exceptions.ErrorMessages;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class MapScanner {
// ------------------------------ FIELDS ------------------------------

    private Properties mapData = new Properties();
    private final String pathToMapDirectory;
    private File currentFile;

// --------------------------- CONSTRUCTORS ---------------------------

    public MapScanner(String pathToMapDirectory) {
        this.pathToMapDirectory = pathToMapDirectory;
    }

// -------------------------- OTHER METHODS --------------------------

    public void saveMap(TMapMetaData map) throws TechnicalException {
        try {
            FileOutputStream outputStream = new FileOutputStream("./maps/" + map.getName() + ".map");

            Properties mapData = new Properties();
            mapData.put("symmetry", "" + map.getSymmetry());
            mapData.put("seed", "" + map.getSeed());
            mapData.put("edgeLength", "" + map.getEdgeLength());
            mapData.put("factoryFactor", "" + map.getFactoryNumberFactor());
            mapData.put("factorySizeFactor", "" + map.getFactorySizeFactor());
            mapData.put("maximumFactorySizeFactor", "" + map.getMaximumFactorySize());

            mapData.store(outputStream, "");
        } catch (IOException e) {
            throw new TechnicalException(ErrorMessages.get("unableToWrite") + ": " + "./maps/" + map.getName() + ".map");
        }
    }

    public List<TMapMetaData> searchMaps() throws CorruptConfigurationFileException, TechnicalException {
        return searchMapsRecursively(new File(pathToMapDirectory));
    }

    public List<TMapMetaData> searchMapsRecursively(File file) throws TechnicalException, CorruptConfigurationFileException {
        try {
            List<TMapMetaData> result = new LinkedList<TMapMetaData>();
            File[] files = file.listFiles();

            for (File tmp : files) {
                if (tmp.isFile() && tmp.getPath().endsWith(".map")) {
                    currentFile = tmp; //only for errors

                    mapData.load(new FileInputStream(tmp.getAbsolutePath()));

                    result.add(new TMapMetaData(tmp.getName().replaceAll("(.*).map", "$1"),
                            readInt("symmetry"),
                            readInt("seed"),
                            readInt("edgeLength"),
                            readInt("factoryFactor"),
                            readInt("factorySizeFactor"),
                            readInt("maximumFactorySizeFactor")
                    ));
                } else if (tmp.isDirectory()) {
                    result.addAll(searchMapsRecursively(tmp));
                }
            }

            return result;
        } catch (IOException e) {
            throw new TechnicalException(ErrorMessages.get("unreadableMapsFolder") + " :\n'" + file.getAbsolutePath() + "'");
        }
    }

    private int readInt(String key) throws CorruptConfigurationFileException {
        try {
            return Integer.parseInt(mapData.getProperty(key));
        } catch (NumberFormatException e) {
            throw new CorruptConfigurationFileException(ErrorMessages.get("corruptMapFile"), currentFile.getAbsolutePath());
        }
    }
}


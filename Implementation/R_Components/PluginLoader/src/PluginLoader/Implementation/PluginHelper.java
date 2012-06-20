package PluginLoader.Implementation;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import Exceptions.ErrorMessages;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  This class helps with the loading of plugins
 */
class PluginHelper {

    private URLClassLoader classLoader;
    /**
     * Returns a list of all jars in a directory. This is not only true for jars located directly in the
     * directory, but also for jars in any sub-folder of the given folder.
     *
     * @param directory a valid directory string != null
     * @return empty, if no jars found
     */
    public List<String> findJarsRecursively(String directory) {
        List<String> result = new LinkedList<String>();

        File[] files = new File(directory).listFiles();

        for (File file : files) {
            if (file.isFile() && file.getPath().endsWith(".jar")) {
                result.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                result.addAll(findJarsRecursively(file.getPath()));
            }
        }

        return result;
    }

    /**
     * Creates a new classloader, in which all classes from the given jar are loaded.
     * @param pathToJar the location of the jar != null
     * @return the classloader
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException if the jar is not readable
     * @throws IllegalArgumentException if no path is found under the given path
     */
    public void loadJar(String pathToJar) throws TechnicalException, PluginNotReadableException {
        try {
            //create a new class loader and make it the new context
            classLoader = new URLClassLoader(new URL[]{new File(pathToJar).toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);

            //then make all classes of the plugin known to the class loader
            for (Class<?> clazz : listClassesFromJar(pathToJar)) {
                System.out.println("pluginloader: " + clazz);
                try {
                    Class.forName(clazz.getName(), true, classLoader);
                } catch (ClassNotFoundException e) {
                    throw new PluginNotReadableException(e.toString(), pathToJar);
                }
            }

        } catch (MalformedURLException e) {
            throw new TechnicalException(ErrorMessages.get("invalidJarPath") + e);
        } catch (IOException e) {
            throw new PluginNotReadableException(e.toString(), pathToJar);
        }
    }

    /**
     * Returns a list containing all class files found in the jar described by the parameter.
     * The classes are not fully initialized but from now on known to the JVM.
     * @param pathToJar the location of the jar != null
     * @return empty, if no class files are in the jar
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException if the jar is not readable
     * @throws IllegalArgumentException if no path is found under the given path
     */
    public List<Class> listClassesFromJar(String pathToJar) throws TechnicalException {
        //create file access resources needed
        URLClassLoader classLoader = null;
        List<Class> classes = new LinkedList<Class>();

        try {

            classLoader = new URLClassLoader(new URL[]{new File(pathToJar).toURI().toURL()}, Thread.currentThread().getContextClassLoader());

            JarFile jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> e = jarFile.entries();

//            //result init and caching

            JarEntry jarEntry;
            String className;

            //scan jar entries, convert name to canonical class name and try to load the class

            while (e.hasMoreElements()) {
                jarEntry = e.nextElement();

                if (jarEntry.getName().endsWith(".class")) {
                    try {
                        //convert file name to canonical class name
                        className = jarEntry.getName().replaceAll("/", "\\.").replace(".class", "");

                        //load and add
                        classes.add(classLoader.loadClass(className));
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Class '" + jarEntry.getName().replaceAll("/", "\\.") + "' could not be found while loading jar:\n" + pathToJar);
                    }
                }
            }

            return classes;

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(ErrorMessages.get("invalidJarPath") + e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(ErrorMessages.get("unreadableJar") + e);
        } catch (IOException e) {
            throw new TechnicalException(ErrorMessages.get("invalidJarPath") + e);
        }
    }
}

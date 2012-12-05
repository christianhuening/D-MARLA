package PluginLoader.Implementation;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import sun.misc.Launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class helps with the loading of plugins
 */
class PluginHelper {

    ClassLoader classLoader = null;

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

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getPath().endsWith(".jar")) {
                    result.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    result.addAll(findJarsRecursively(file.getPath()));
                }
            }
        }

        return result;
    }

    /**
     * Creates a new classloader, in which all classes from the given jar are loaded.
     *
     * @param pathToJar the location of the jar != null
     * @return the classloader
     * @throws EnvironmentPluginAPI.Exceptions.TechnicalException
     *                                  if the jar is not readable
     * @throws IllegalArgumentException if no path is found under the given path
     */
    public List<Class> loadJar(String pathToJar) throws TechnicalException, PluginNotReadableException {

        List<Class> classes = listClassesFromJar(pathToJar);
        //then make all classes of the plugin known to the class loader
        for (Class<?> clazz : classes) {
            try {
                Class.forName(clazz.getName(), true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new PluginNotReadableException(e.getMessage(), pathToJar);
            }
        }

        System.err.println("Classloader im PluginHelper: " + classLoader);
        System.err.println("loading plugin into: " + Thread.currentThread());
        System.err.println("setting context classloader in " + Thread.currentThread());
        Thread.currentThread().setContextClassLoader(classLoader);
        return classes;
    }


    /**
     * Returns a list containing all class files found in the jar described by the parameter.
     * The classes are not fully initialized but from now on known to the JVM.
     *
     * @param pathToJar the location of the jar != null
     * @return empty, if no class files are in the jar
     * @throws EnvironmentPluginAPI.Exceptions.TechnicalException
     *                                  if the jar is not readable
     * @throws IllegalArgumentException if no path is found under the given path
     */
    public List<Class> listClassesFromJar(String pathToJar) throws TechnicalException {
        //create file access resources needed
        List<Class> classes = new LinkedList<Class>();

        try {
            classLoader = new URLClassLoader(new URL[]{new File(pathToJar).toURI().toURL()}, Launcher.getLauncher().getClassLoader());
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
                        classes.add(Class.forName(className, false, classLoader));
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


    /**
     * Looks for a compatible constructors in a class object.
     * <br/><br/>
     * For the definition of compatible see isAssignable
     *
     * @param toExamine      The class to look for the constructor in != null
     * @param typesToLookFor the
     * @return
     */
    public Constructor findSuitableConstructor(Class toExamine, Class... typesToLookFor) {
        Class[] argTypes;

        for (Constructor ctor : toExamine.getConstructors()) {
            argTypes = ctor.getParameterTypes();

            if (isAssignable(argTypes, typesToLookFor)) {
                return ctor;
            }
        }

        return null;
    }

    /**
     * Determines if all types in argTypes are compatible to the classes in types.
     * <br/><br/>
     * Compatible means:<br/>
     * 1. Same length of list<br/>
     * 2. Each type in argTypes is the same type or a subtype of the class in the same index in types
     *
     * @param argTypes a list of argument types
     * @param types    the types to look for
     * @return see description
     */
    public boolean isAssignable(Class[] argTypes, Class... types) {
        if (argTypes.length != types.length) return false;

        for (int i = 0; i < argTypes.length; i++) {
            if (!types[i].isAssignableFrom(argTypes[i])) return false;
        }

        return true;
    }
}

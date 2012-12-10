package PluginLoader.Implementation;

import java.util.Iterator;
import java.util.List;

/**
 *  This class is used to transport a plugin's classes and the class loader, that was used to load them. This is needed
 *  because the class loader has to be set in different contexts of the application, especially the network adapter.
 */
public class Plugin implements Iterable<Class> {

    private ClassLoader classLoader;
    private List<Class> classes;

    public Plugin(ClassLoader classLoader, List<Class> classes) {
        this.classLoader = classLoader;
        this.classes = classes;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Class> iterator() {
        return classes.iterator();
    }
}

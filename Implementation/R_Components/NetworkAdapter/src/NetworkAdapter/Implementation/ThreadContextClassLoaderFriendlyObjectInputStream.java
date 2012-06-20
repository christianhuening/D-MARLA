package NetworkAdapter.Implementation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 19.06.12
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */

/**
 * An ObjectInputStream implementation that takes into account the context classloader when loading classes.
 */
public class ThreadContextClassLoaderFriendlyObjectInputStream extends ObjectInputStream {

    private static Pattern arrayBracketsFinderOfDoom = Pattern.compile("(.+?)([\\[]]+)");

    @Override
    public Class resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {
        ClassLoader currentTccl = null;
        try {
            currentTccl = Thread.currentThread().getContextClassLoader();

            return Class.forName(desc.getName(), false, currentTccl);
            //return currentTccl.loadClass(desc.getName());
        } catch (ClassNotFoundException e) {
            /*String cleanedUpName = toClassName(desc.getName());

            Matcher cleanedUpNameMatcher = arrayBracketsFinderOfDoom.matcher(cleanedUpName);

            if(cleanedUpNameMatcher.matches()) {
                cleanedUpName = cleanedUpNameMatcher.group(1);
                return Array.newInstance(currentTccl.loadClass(cleanedUpName), cleanedUpNameMatcher.group(2).length()/2).getClass();
            }

            return currentTccl.loadClass(cleanedUpName);*/

            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return super.resolveClass(desc);
    }


    public ThreadContextClassLoaderFriendlyObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    /**
     * Converts to a Java class name from a descriptor.
     * Taken from: http://www.docjar.com/html/api/javassist/bytecode/Descriptor.java.html
     * @param descriptor        type descriptor.
     *
     */

    public static String toClassName(String descriptor) {
        int arrayDim = 0;
        int i = 0;
        char c = descriptor.charAt(0);
        while (c == '[') {
            ++arrayDim;
            c = descriptor.charAt(++i);
        }

        String name;
        if (c == 'L') {
            int i2 = descriptor.indexOf(';', i++);
            name = descriptor.substring(i, i2).replace('/', '.');
            i = i2;
        } else if (c == 'V')
            name = "void";
        else if (c == 'I')
            name = "int";
        else if (c == 'B')
            name = "byte";
        else if (c == 'J')
            name = "long";
        else if (c == 'D')
            name = "double";
        else if (c == 'F')
            name = "float";
        else if (c == 'C')
            name = "char";
        else if (c == 'S')
            name = "short";
        else if (c == 'Z')
            name = "boolean";
        else
            throw new RuntimeException("bad descriptor: " + descriptor);

        if (i + 1 != descriptor.length())
            throw new RuntimeException("multiple descriptors?: " + descriptor);

        if (arrayDim == 0)
            return name;
        else {
            StringBuffer sbuf = new StringBuffer(name);
            do {
                sbuf.append("[]");
            } while (--arrayDim > 0);

            return sbuf.toString();
        }
    }
}

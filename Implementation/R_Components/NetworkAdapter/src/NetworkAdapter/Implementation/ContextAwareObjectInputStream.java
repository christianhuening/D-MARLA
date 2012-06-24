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
public class ContextAwareObjectInputStream extends ObjectInputStream {

    private ClassLoader currentTccl;

    public ContextAwareObjectInputStream(InputStream in) throws IOException {
        super(in);
        currentTccl = Thread.currentThread().getContextClassLoader();
        System.err.println("inputstream started in thread " + Thread.currentThread());
        System.err.println("current classloader: " + currentTccl);
    }

    @Override
    public Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        currentTccl = Thread.currentThread().getContextClassLoader();
        System.err.println("looking in Thread: " + Thread.currentThread() + " for " + desc.getName() + " [" + currentTccl + "]");
        try {
            return super.resolveClass(desc);
        } catch (ClassNotFoundException e) {
            System.err.println("not found, looking in Thread: " + Thread.currentThread() + " for " + desc.getName() + " [" + currentTccl + "]");
            return Class.forName(desc.getName(), true, currentTccl);
        }
    }

}

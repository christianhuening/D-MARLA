package ZeroTypes.RemoteInterface;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;


public class ClientSocketFactory extends RMISocketFactory implements Serializable {

    private InetAddress ipInterface = null;

    public ClientSocketFactory() {
    }

    public ClientSocketFactory(InetAddress ipInterface) {
        this.ipInterface = ipInterface;
    }

    public ServerSocket createServerSocket(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 50, ipInterface);
        } catch (Exception e) {
            System.out.println(e);
        }
        return (serverSocket);
    }

    public Socket createSocket(String dummy, int port) throws IOException {
        return (new Socket(ipInterface, port));
    }

    public boolean equals(Object that) {
        return (that != null && that.getClass() == this.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.NetworkEventType;
import NetworkAdapter.Messages.NACKMessage;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 02.12.12
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class TestNetworkMessageHandler extends Thread implements INetworkMessageReceivedEventHandler {

    private int messageCount;
    private int targetMessageCount;

    public TestNetworkMessageHandler(String name, int targetMessageCount) {
        super(name);
        this.targetMessageCount = targetMessageCount;
        this.messageCount = 0;
    }


    @Override
    public void run() {
        boolean finished = false;
        while (!finished) {

            synchronized (this) {
                System.err.println("checking incoming messages. count is " + messageCount);
                if (messageCount == targetMessageCount) {
                    finished = true;
                } else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        }
    }

    /**
     * this method will be called on event listeners, when a network message arrives
     *
     * @param message
     */
    @Override
    public void onMessageReceived(NetworkMessage message) {

        synchronized (this) {
            if (message instanceof NACKMessage) {
                messageCount++;
            }
            notify();
        }

    }

    @Override
    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {

    }

    public int getMessageCount() {
        return messageCount;
    }
}

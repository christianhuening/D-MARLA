package NetworkAdapter.Implementation;

import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.NetworkEventType;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class provides the basic implementation of the network adapter logic, since a lot of their code is shared
 * between server and client.
 */
abstract class AbstractNetworkAdapterUseCase {
    //we organize the listeners for each message type in a separated list, to prevent messages from being delivered to
    // the wrong listeners on one hand and also better performance on the other.
    private Map<Class, SubscriberCollection> subscribers;

    Type[] types;

    public AbstractNetworkAdapterUseCase() {
        subscribers = new Hashtable<Class, SubscriberCollection>();
    }

    public <T extends NetworkMessage> void subscribeForNetworkMessageReceivedEvent(INetworkMessageReceivedEventHandler<T> eventHandler, Class messageType) {

        if (!subscribers.containsKey(messageType)) {
            subscribers.put(messageType, new SubscriberCollection());
        }

        SubscriberCollection subscriberCollection = subscribers.get(messageType);

        subscriberCollection.addSubscriber(eventHandler);
    }

    protected <T extends NetworkMessage> void informSubscribers(T message)  {

        for (Class key : subscribers.keySet()) {
            if (key.isAssignableFrom(message.getClass())) {
                subscribers.get(key).deliverMessage(message);
            }
        }

    }

    protected void fireNetworkEvent(NetworkEventType eventType, int clientId) {
       for(SubscriberCollection subscriberCollection : subscribers.values()) {
           for(INetworkMessageReceivedEventHandler networkMessageReceivedEventHandler : subscriberCollection) {
               networkMessageReceivedEventHandler.onNetworkEvent(eventType, clientId);
           }
       }
    }
}

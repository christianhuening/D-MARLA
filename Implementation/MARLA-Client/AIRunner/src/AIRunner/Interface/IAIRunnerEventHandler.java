package AIRunner.Interface;

import NetworkAdapter.Interface.NetworkEventType;

/**
 *
 */
public interface IAIRunnerEventHandler {

    public void onAIRunnerEvent(AIRunnerEventType eventType);

    public void onSessionStart(int games);

    public void onException(Exception exception);
}

package AIRunner.Interface;

/**
 * Different types of possible events.
 */
public enum AIRunnerEventType {
    Connected,
    Disconnected,
    SessionStarted,
    SessionEnded,
    CycleEnded,
    ConnectionLost,
    TechnicalError,
}

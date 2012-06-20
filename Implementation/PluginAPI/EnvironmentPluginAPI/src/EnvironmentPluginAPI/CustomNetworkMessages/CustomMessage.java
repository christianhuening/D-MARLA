package EnvironmentPluginAPI.CustomNetworkMessages;

/**
 * MARLA uses this annotation to recognize environment specific custom implementations of a CustomNetworkMessages.
 */
public @interface CustomMessage {
      MessageType value();
}

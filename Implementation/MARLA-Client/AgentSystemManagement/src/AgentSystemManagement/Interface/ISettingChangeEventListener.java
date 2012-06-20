package AgentSystemManagement.Interface;

/**
 *  Callback which is to be executed, when a setting for a certain AgentSystem changes.
 */
public interface ISettingChangeEventListener {

    public void onSettingChanged(String settingName, String newValue);

}

import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentComboBoxModel extends DefaultComboBoxModel {

    private List<TEnvironmentDescription> environmentDescriptions;

    public EnvironmentComboBoxModel(){
        environmentDescriptions = new ArrayList<TEnvironmentDescription>();
    }

    public void addEnvironmentDescription(TEnvironmentDescription desc){
        environmentDescriptions.add(desc);
        this.addElement(desc);
    }

    public void addEnvironmentDescriptions(List<TEnvironmentDescription> descs){
        for(TEnvironmentDescription desc : descs){
            this.addEnvironmentDescription(desc);
        }
    }

    public TEnvironmentDescription getEnvironmentDescription(int index){
        return environmentDescriptions.get(index);
    }


}

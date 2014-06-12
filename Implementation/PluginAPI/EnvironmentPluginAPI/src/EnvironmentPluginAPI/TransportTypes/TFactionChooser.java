package EnvironmentPluginAPI.TransportTypes;

import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

import java.io.Serializable;

/**
 * Created by Chris on 12.06.2014.
 */
public class TFactionChooser implements Serializable, IEnvironmentConfiguration {
        private String faction;

        public TFactionChooser(String faction){
            this.faction = faction;
        }

        public String toString(){
            return faction;
        }
}

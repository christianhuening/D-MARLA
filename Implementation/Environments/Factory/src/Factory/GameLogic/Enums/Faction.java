package Factory.GameLogic.Enums;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:30
 * To change this template use File | Settings | File Templates.
 */
public enum Faction {
    RED {
        @Override
        public String toString() {
            return "R";
        }
    },
    BLUE {
        @Override
        public String toString() {
            return "B";
        }
    },
    NEUTRAL {
        @Override
        public String toString() {
            return "N";
        }
    }
}

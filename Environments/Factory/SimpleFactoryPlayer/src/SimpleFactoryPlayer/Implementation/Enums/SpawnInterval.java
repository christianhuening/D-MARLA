package SimpleFactoryPlayer.Implementation.Enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 08.06.12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public enum SpawnInterval {
    VERY_SHORT,
    //SHORT,
    //MIDDLE,
    LONG;

// ------------------------------ FIELDS ------------------------------

    private static final Map<Integer,SpawnInterval> lookup
            = new HashMap<Integer,SpawnInterval>();

    private int code;

// -------------------------- STATIC METHODS --------------------------

    static {
        for(SpawnInterval s : EnumSet.allOf(SpawnInterval.class))
            lookup.put(s.getCode(), s);
    }

    public static SpawnInterval get(int code) {
        return lookup.get(code);
    }

// -------------------------- PUBLIC METHODS --------------------------

    public int getCode() { return ordinal(); }
}

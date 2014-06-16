package HierarchicalFactoryPlayer.Enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 06.06.12
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public enum FriendFoe {
    FRIEND,
    EXHAUSTED_FRIEND,
    FOE,
    NONE;

// ------------------------------ FIELDS ------------------------------

    private static final Map<Integer,FriendFoe> lookup
            = new HashMap<Integer,FriendFoe>();

    private int code;

// -------------------------- STATIC METHODS --------------------------

    static {
        for(FriendFoe s : EnumSet.allOf(FriendFoe.class))
            lookup.put(s.getCode(), s);
    }

    public static FriendFoe get(int code) {
        return lookup.get(code);
    }

// -------------------------- PUBLIC METHODS --------------------------

    public int getCode() { return ordinal(); }
}
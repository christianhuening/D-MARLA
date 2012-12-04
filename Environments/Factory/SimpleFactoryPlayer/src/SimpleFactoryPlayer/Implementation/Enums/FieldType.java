package SimpleFactoryPlayer.Implementation.Enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 07.06.12
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */
public enum FieldType {
    NORMAL,
    INFLUENCE,
    FACTORY;

// ------------------------------ FIELDS ------------------------------

    private static final Map<Integer,FieldType> lookup
            = new HashMap<Integer,FieldType>();

    private int code;

// -------------------------- STATIC METHODS --------------------------

    static {
        for(FieldType s : EnumSet.allOf(FieldType.class))
            lookup.put(s.getCode(), s);
    }

    public static FieldType get(int code) {
        return lookup.get(code);
    }

// -------------------------- PUBLIC METHODS --------------------------

    public int getCode() { return ordinal(); }
}

package Factory.CustomMessages;

import EnvironmentPluginAPI.CustomNetworkMessages.CustomMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.MessageType;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.Exceptions.ConsistencyFaultException;
import Factory.GameLogic.TransportTypes.TAction;
import Factory.GameLogic.TransportTypes.TActionsInTurn;
import Factory.GameLogic.TransportTypes.TUnit;
import Factory.Interfaces.IHasConsistencyCheck;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This message is used to transport ActionLists across the network connection.
 */
public class ActionListMessage extends NetworkMessage implements IHasConsistencyCheck, Serializable {
// ------------------------------ FIELDS ------------------------------

    private TActionsInTurn actions;

    private byte[] raw;

    public TActionsInTurn getAction() {
        return actions;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public ActionListMessage(int clientId, TActionsInTurn actionsInTurn) {
        super(clientId);
        actions = actionsInTurn;

        if(!isConsistent()) {
            throw new ConsistencyFaultException();
        }
    }

    /**
     * only for serialization!!
     */
    public ActionListMessage() throws ConsistencyFaultException {
        super(-1);
    }

    private TActionsInTurn decodeTActionsInTurn(ByteBuffer buffer) {
        List<TAction> actions = new ArrayList<TAction>();

        while (buffer.remaining() > 0) {
            actions.add(decodeTAction(buffer));
        }

        return new TActionsInTurn(actions);
    }

    private TAction decodeTAction(ByteBuffer buffer) {
        TUnit unit = decodeTUnit(buffer);
        Direction direction = Direction.values()[buffer.getInt()];

        return new TAction(unit, direction);
    }

    private TUnit decodeTUnit(ByteBuffer buffer) {
        long unitIdMSB = buffer.getLong();
        long unitIdLSB = buffer.getLong();
        Faction faction = Faction.values()[buffer.getInt()];

        return new TUnit(new UUID(unitIdMSB, unitIdLSB), faction);
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionListMessage)) return false;

        ActionListMessage that = (ActionListMessage) o;

        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return actions != null ? actions.hashCode() : 0;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------


    @Override
    public boolean isConsistent() {
        return actions.isConsistent();
    }

// -------------------------- PRIVATE METHODS --------------------------

    private byte[] encodeTActionsInTurn(TActionsInTurn actionsInTurn) {
        byte[] actions = new byte[0];

        for (TAction action : actionsInTurn.getActions()) {
            actions = NetworkMessage.concatByteArrays(actions, encodeTAction(action));
        }

        return actions;
    }

    private byte[] encodeTAction(TAction action) {
        byte[] unit = encodeTUnit(action.getUnit());
        byte[] direction = ByteBuffer.allocate(4).putInt(action.getDirection().ordinal()).array();

        return NetworkMessage.concatByteArrays(unit, direction);
    }

    private byte[] encodeTUnit(TUnit unit) {
        byte[] unitIdMSB = ByteBuffer.allocate(8).putLong(unit.getUnitId().getMostSignificantBits()).array();
        byte[] unitIdLSB = ByteBuffer.allocate(8).putLong(unit.getUnitId().getLeastSignificantBits()).array();
        byte[] factionOrdinal = ByteBuffer.allocate(4).putInt(unit.getControllingFaction().ordinal()).array();

        byte[] unitUUID = NetworkMessage.concatByteArrays(unitIdMSB, unitIdLSB);

        return NetworkMessage.concatByteArrays(unitUUID, factionOrdinal);
    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        raw = encodeTActionsInTurn(actions);
//        out.writeInt(raw.length);
//        System.out.println("action list encoded length of raw: " + raw.length);
//        out.write(raw);
//        System.out.println("action list writing succeeded");
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        int length = in.readInt();
//        raw = new byte[length];
//        in.readFully(raw, 0, length);
//        actions = decodeTActionsInTurn(ByteBuffer.wrap(raw));
//        System.out.println("action list received length: " + length);
//    }
}

package Factory.CustomMessages;

import EnvironmentPluginAPI.CustomNetworkMessages.*;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.Exceptions.ConsistencyFaultException;
import Factory.GameLogic.TransportTypes.*;
import Factory.Interfaces.IHasConsistencyCheck;
import org.joda.time.DateTime;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This is used to transport representations of the current state of the game via the network adapters.
 */
public class GameStateMessage extends NetworkMessage implements IHasConsistencyCheck, Serializable {
// ------------------------------ FIELDS ------------------------------

    private TGameState gameState;

    private byte[] raw;

// --------------------------- CONSTRUCTORS ---------------------------

    public GameStateMessage(int clientId, TGameState gameState) {
        super(clientId);
        this.gameState = gameState;

        if (!isConsistent()) {
            throw new ConsistencyFaultException();
        }
    }

    @Override
    public boolean isConsistent() {
        return this.gameState.isConsistent();
    }

    /**
     * only for serializtion!!
     */
    public GameStateMessage() {
        super(-1);
    }

    private TGameState decodeTGameState(ByteBuffer buffer) {
        TPlayer activePlayer = decodeTPlayer(buffer);

        int turn = buffer.getInt();
        int round = buffer.getInt();

        int dateTimeStringLength = buffer.getInt();
        byte[] dateTimeStringByteArray = new byte[dateTimeStringLength];
        for (int i = 0; i < dateTimeStringLength; i++) {
            dateTimeStringByteArray[i] = buffer.get();
        }
        DateTime timeStarted = new DateTime(new String(dateTimeStringByteArray));

        int factoryArrayLength = buffer.get();
        List<TFactory> factoryList = new LinkedList<TFactory>();
        for (int i = 0; i < factoryArrayLength; i++) {
            factoryList.add(decodeTFactory(buffer));
        }

        int abstractFieldArrayX = buffer.getInt();
        int abstractFieldArrayY = buffer.getInt();

        TAbstractField[][] abstractFields = new TAbstractField[abstractFieldArrayX][abstractFieldArrayY];

        for (int i = 0; i < abstractFieldArrayX; i++) {
            for (int j = 0; j < abstractFieldArrayY; j++) {
                abstractFields[i][j] = decodeTAbstractField(buffer);
            }
        }

        boolean won = false;
        if (buffer.hasRemaining()) {
            won = true;
        }

        return new TGameState(won, activePlayer, turn, round, timeStarted, factoryList, abstractFields);
    }

    private TFactory decodeTFactory(ByteBuffer buffer) {
        int remainingRoundsForRespawn = buffer.getInt();

        int currentInfluence = buffer.getInt();

        Faction owningFaction = Faction.values()[buffer.get()];

        int factoryID = buffer.getInt();

        return new TFactory(remainingRoundsForRespawn, currentInfluence, owningFaction, factoryID);
    }

    private TAbstractField decodeTAbstractField(ByteBuffer buffer) {
        TUnit occupant = null;

        int occupied = buffer.get();
        if (occupied == 1) {
            occupant = decodeTUnit(buffer);
        } else if (occupied < 0 || occupied > 1) {
            throw new DecodingMessageFailedException("not correctly encoded if field is occupied.");
        }

        byte typeOfField = buffer.get();
        switch (typeOfField) {
            case 0:
                return decodeTFactoryField(buffer, occupant);
            case 1:
                return decodeTInfluenceField(buffer, occupant);
            case 2:
                return decodeTNormalField(occupant);
            default:
                throw new DecodingMessageFailedException("field type incorrectly encoded");
        }
    }

    private TUnit decodeTUnit(ByteBuffer buffer) {
        long unitIdMSB = buffer.getLong();
        long unitIdLSB = buffer.getLong();
        Faction faction = Faction.values()[buffer.getInt()];

        return new TUnit(new UUID(unitIdMSB, unitIdLSB), faction);
    }

    private TFactoryField decodeTFactoryField(ByteBuffer buffer, TUnit occupant) {
        int factoryId = buffer.getInt();

        return new TFactoryField(occupant, factoryId);
    }

    private TInfluenceField decodeTInfluenceField(ByteBuffer buffer, TUnit occupant) {
        int factoryId = buffer.getInt();

        return new TInfluenceField(occupant, factoryId);
    }

    private TNormalField decodeTNormalField(TUnit occupant) {
        return new TNormalField(occupant);
    }

    private TPlayer decodeTPlayer(ByteBuffer buffer) {
        int playerNameLength = buffer.getInt();
        byte[] playerNameByteArray = new byte[playerNameLength];
        for (int i = 0; i < playerNameLength; i++) {
            playerNameByteArray[i] = buffer.get();
        }
        String playerName = new String(playerNameByteArray);

        Faction playerFaction = Faction.values()[buffer.getInt()];

        return new TPlayer(playerName, playerFaction);
    }

// -------------------------- PUBLIC METHODS --------------------------

    public TGameState getEnvironmentState() {
        return gameState;
    }

// -------------------------- PRIVATE METHODS --------------------------

    private byte[] encodeTGameState(TGameState gameState) {
        byte[] result = new byte[0];

        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(gameState.getTurn()).array());
        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(gameState.getRound()).array());

        byte[] startingTimeByteArray = gameState.getGameStartedAt().toString().getBytes();
        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(startingTimeByteArray.length).array());
        result = NetworkMessage.concatByteArrays(result, startingTimeByteArray);

        result = NetworkMessage.concatByteArrays(result, new byte[]{(byte) gameState.getFactories().size()});
        for (TFactory factory : gameState.getFactories()) {
            result = NetworkMessage.concatByteArrays(result, encodeTFactory(factory));
        }

        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(gameState.getMapFields().length).array());
        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(gameState.getMapFields()[0].length).array());
        for (int i = 0; i < gameState.getMapFields().length; i++) {
            for (int j = 0; j < gameState.getMapFields()[0].length; j++) {
                result = NetworkMessage.concatByteArrays(result, encodeTAbstractField(gameState.getMapFields()[i][j]));
            }
        }

        if (gameState.hasClientMetGoal()) {
            result = NetworkMessage.concatByteArrays(result, ONE);
        }

        return result;
    }

    private byte[] encodeTFactory(TFactory factory) {
        byte[] result = new byte[0];

        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(factory.getRemainingRoundsForRespawn()).array());
        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(factory.getCurrentInfluence()).array());
        result = NetworkMessage.concatByteArrays(result, new byte[]{(byte) factory.getOwningFaction().ordinal()});
        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(factory.getFactoryID()).array());

        return result;
    }

    private byte[] encodeTAbstractField(TAbstractField abstractField) {
        byte[] result;

        if (abstractField.getOccupant() != null) {
            result = NetworkMessage.ONE;
            result = NetworkMessage.concatByteArrays(result, encodeTUnit(abstractField.getOccupant()));
        } else {
            result = NetworkMessage.ZERO;
        }

        if (abstractField instanceof TFactoryField) {
            result = NetworkMessage.concatByteArrays(result, NetworkMessage.ZERO);
            result = NetworkMessage.concatByteArrays(result, encodeTFactoryField((TFactoryField) abstractField));
        } else if (abstractField instanceof TInfluenceField) {
            result = NetworkMessage.concatByteArrays(result, NetworkMessage.ONE);
            result = NetworkMessage.concatByteArrays(result, encodeTInfluenceField((TInfluenceField) abstractField));
        } else if (abstractField instanceof TNormalField) {
            result = NetworkMessage.concatByteArrays(result, NetworkMessage.TWO);
            result = NetworkMessage.concatByteArrays(result, encodeTNormalField((TNormalField) abstractField));
        }

        return result;
    }

    private byte[] encodeTUnit(TUnit unit) {
        byte[] unitIdMSB = ByteBuffer.allocate(8).putLong(unit.getUnitId().getMostSignificantBits()).array();
        byte[] unitIdLSB = ByteBuffer.allocate(8).putLong(unit.getUnitId().getLeastSignificantBits()).array();
        byte[] factionOrdinal = ByteBuffer.allocate(4).putInt(unit.getControllingFaction().ordinal()).array();

        byte[] unitUUID = NetworkMessage.concatByteArrays(unitIdMSB, unitIdLSB);

        return NetworkMessage.concatByteArrays(unitUUID, factionOrdinal);
    }

    private byte[] encodeTFactoryField(TFactoryField field) {
        return ByteBuffer.allocate(4).putInt(field.getFactoryID()).array();
    }

    private byte[] encodeTInfluenceField(TInfluenceField field) {
        return ByteBuffer.allocate(4).putInt(field.getFactoryID()).array();
    }

    private byte[] encodeTNormalField(TNormalField field) {
        return NetworkMessage.EMPTY_BYTES;
    }

    private byte[] encodeTPlayer(TPlayer player) {
        byte[] result = new byte[0];

        byte[] playerNameByteArray = player.getName().getBytes();

        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(playerNameByteArray.length).array());
        result = NetworkMessage.concatByteArrays(result, playerNameByteArray);

        result = NetworkMessage.concatByteArrays(result, ByteBuffer.allocate(4).putInt(player.getFaction().ordinal()).array());

        return result;
    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        raw = encodeTGameState(gameState);
//        out.writeInt(raw.length);
//        System.out.println("game state encoded length of raw: " + raw.length);
//        out.write(raw);
//        System.out.println("game state writing succeeded");
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        int length = in.readInt();
//        raw = new byte[length];
//        in.readFully(raw, 0, length);
//        gameState = decodeTGameState(ByteBuffer.wrap(raw));
//        System.out.println("game state received length: " + length);
//    }
}
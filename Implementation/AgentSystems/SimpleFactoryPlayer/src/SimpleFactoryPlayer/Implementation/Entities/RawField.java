package SimpleFactoryPlayer.Implementation.Entities;

import SimpleFactoryPlayer.Implementation.Enums.FieldType;
import SimpleFactoryPlayer.Implementation.Enums.FriendFoe;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 06.06.12
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class RawField {
    private FriendFoe unit;

    private FriendFoe FieldController;
    private FieldType fieldType;
    private int remainingTimeToSpawn;


    public FriendFoe getFieldController() {
        return FieldController;
    }

    public void setFieldController(FriendFoe fieldController) {
        FieldController = fieldController;
    }

    public FriendFoe getUnit() {
        return unit;
    }

    public void setUnit(FriendFoe unit) {
        this.unit = unit;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public int getRemainingTimeToSpawn() {
        return remainingTimeToSpawn;
    }

    public void setRemainingTimeToSpawn(int remainingTimeToSpawn) {
        this.remainingTimeToSpawn = remainingTimeToSpawn;
    }

    @Override
    public String toString() {
        return "RawField{" +
                "unit=" + unit +
                ", FieldController=" + FieldController +
                ", fieldType=" + fieldType +
                ", remainingTimeToSpawn=" + remainingTimeToSpawn +
                '}';
    }
}

package HierarchicalFactoryPlayer.Entities;

import HierarchicalFactoryPlayer.Enums.FieldType;
import HierarchicalFactoryPlayer.Enums.FriendFoe;

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
    private String evaluation;


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

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getEvaluation() {
        return evaluation;
    }
}

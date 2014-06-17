package HierarchicalFactoryPlayer.Entities;


public class QuadTreeTuple {
    private final int unitRatio;
    private final int fieldRatio;

    public QuadTreeTuple(int unitRatio, int fieldRatio) {
        this.unitRatio = unitRatio;
        this.fieldRatio = fieldRatio;
    }

    public int getFieldRatio() {
        return fieldRatio;
    }

    public int getUnitRatio() {
        return unitRatio;
    }
}

package SimpleFactoryPlayer.Implementation.Entities;

import Factory.GameLogic.Enums.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 06.06.12
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class RawState {

    private RawField middle;
    private RawField top;
    private RawField rightTop;
    private RawField right;
    private RawField rightDown;
    private RawField down;
    private RawField leftDown;
    private RawField left;
    private RawField leftTop;
    private Direction signal;

    public RawState(){
    }



    public List<RawField> getFieldListRepresentation(){
        List<RawField> list = new ArrayList<RawField>();
        list.add(middle);
        list.add(top);
        list.add(rightTop);
        list.add(right);
        list.add(rightDown);
        list.add(down);
        list.add(leftDown);
        list.add(left);
        list.add(leftTop);
        return list;
    }

    public void setFieldListRepresentation(List<RawField> rawFieldList){
        middle=rawFieldList.remove(0);
        top = rawFieldList.remove(0);
        rightTop = rawFieldList.remove(0);
        right = rawFieldList.remove(0);
        rightDown = rawFieldList.remove(0);
        down = rawFieldList.remove(0);
        leftDown = rawFieldList.remove(0);
        left = rawFieldList.remove(0);
        leftTop = rawFieldList.remove(0);
    }

    @Override
    public String toString() {
        return "RawState{" +
                "middle=" + middle +
                ", top=" + top +
                ", rightTop=" + rightTop +
                ", right=" + right +
                ", rightDown=" + rightDown +
                ", down=" + down +
                ", leftDown=" + leftDown +
                ", left=" + left +
                ", leftTop=" + leftTop +
                ", signal=" + signal +
                '}';
    }

    //=============== GETTER SETTER ===========================================


    public RawField getMiddle() {
        return middle;
    }

    public void setMiddle(RawField middle) {
        this.middle = middle;
    }

    public RawField getTop() {
        return top;
    }

    public void setTop(RawField top) {
        this.top = top;
    }

    public RawField getRightTop() {
        return rightTop;
    }

    public void setRightTop(RawField rightTop) {
        this.rightTop = rightTop;
    }

    public RawField getRight() {
        return right;
    }

    public void setRight(RawField right) {
        this.right = right;
    }

    public RawField getRightDown() {
        return rightDown;
    }

    public void setRightDown(RawField rightDown) {
        this.rightDown = rightDown;
    }

    public RawField getDown() {
        return down;
    }

    public void setDown(RawField down) {
        this.down = down;
    }

    public RawField getLeftDown() {
        return leftDown;
    }

    public void setLeftDown(RawField leftDown) {
        this.leftDown = leftDown;
    }

    public RawField getLeft() {
        return left;
    }

    public void setLeft(RawField left) {
        this.left = left;
    }

    public RawField getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(RawField leftTop) {
        this.leftTop = leftTop;
    }

    public Direction getSignal() {
        return signal;
    }

    public void setSignal(Direction signal) {
        this.signal = signal;
    }
}

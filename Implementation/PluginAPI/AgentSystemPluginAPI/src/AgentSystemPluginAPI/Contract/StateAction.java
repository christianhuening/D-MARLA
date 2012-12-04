package AgentSystemPluginAPI.Contract;

/**
 * This class is used as a helper object for retrieving data from the database. It resembles a State (or StateAction if
 * an action has been set) equivalent to the reinforcement learning ones.
 *
 */
public class StateAction implements ICompressed, Comparable<StateAction> {

    private final String stateDescription;
    private String actionDescription = null;

    public StateAction(String stateDescription, String actionDescription) {
        this(stateDescription);
        this.actionDescription = actionDescription;
    }

    public StateAction(String stateDescription) {

        this.stateDescription = stateDescription;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    @Override
    public String getCompressedRepresentation() {
        return (actionDescription != null) ? stateDescription + actionDescription : stateDescription;
    }

    @Override
    public String toString() {
        return getCompressedRepresentation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateAction that = (StateAction) o;

        if (actionDescription != null ? !actionDescription.equals(that.actionDescription) : that.actionDescription != null)
            return false;
        if (!stateDescription.equals(that.stateDescription)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stateDescription.hashCode();
        result = 31 * result + (actionDescription != null ? actionDescription.hashCode() : 0);
        return result;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p/>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p/>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p/>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p/>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this object.
     */
    @Override
    public int compareTo(StateAction o) {
        String us = this.stateDescription + (this.actionDescription != null ? actionDescription : "");
        String them = o.stateDescription + (o.actionDescription != null ? o.actionDescription : "");
        return us.compareTo(them);
    }
}

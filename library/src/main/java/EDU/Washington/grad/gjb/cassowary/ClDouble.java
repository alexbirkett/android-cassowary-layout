// $Id: ClDouble.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClDouble
//

package EDU.Washington.grad.gjb.cassowary;

public class ClDouble extends Number {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ClDouble(double val) {
        value = val;
    }

    public ClDouble() {
        this(0.0);
    }

    public final Object clone() {
        return new ClDouble(value);
    }

    public final double doubleValue() {
        return value;
    }

    public final int intValue() {
        return (int) value;
    }

    public final long longValue() {
        return (long) value;
    }

    public final float floatValue() {
        return (float) value;
    }

    public final byte byteValue() {
        return (byte) value;
    }

    public final short shortValue() {
        return (short) value;
    }

    public final void setValue(double val) {
        value = val;
    }

    public final String toString() {
        return Double.toString(value);
    }

    public final boolean equals(Object o) {
        try {
            return value == ((ClDouble) o).value;
        } catch (Exception err) {
            return false;
        }
    }

    public final int hashCode() {
        System.err.println("ClDouble.hashCode() called!");
        return (int) Double.doubleToLongBits(value);
    }

    private double value;
}

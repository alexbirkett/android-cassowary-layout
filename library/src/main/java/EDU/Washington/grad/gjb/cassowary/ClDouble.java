/*
 * Copyright (C) 1988 1999 Greg J. Badros and Alan Borning
 * Copyright (C) 2014 Agens AS
 *
 * Original Smalltalk Implementation by Alan Borning
 * This Java Implementation by Greg J. Badros
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

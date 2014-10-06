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

public class ClSymbolicWeight {

    public ClSymbolicWeight(int cLevels) {
        _values = new double[cLevels];
        // FIXGJB: ok to assume these get initialized to 0?
        // for (int i = 0; i < cLevels; i++) {
        // _values[i] = 0;
        // }
    }

    public ClSymbolicWeight(double w1, double w2, double w3) {
        _values = new double[3];
        _values[0] = w1;
        _values[1] = w2;
        _values[2] = w3;
    }

    public ClSymbolicWeight(double[] weights) {
        final int cLevels = weights.length;
        _values = new double[cLevels];
        for (int i = 0; i < cLevels; i++) {
            _values[i] = weights[i];
        }
    }

    public static final ClSymbolicWeight clsZero = new ClSymbolicWeight(0.0,
            0.0, 0.0);

    public Object clone() {
        return new ClSymbolicWeight(_values);
    }

    public ClSymbolicWeight times(double n) {
        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] *= n;
        }
        return clsw;
    }

    public ClSymbolicWeight divideBy(double n) {
        // assert(n != 0);
        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] /= n;
        }
        return clsw;
    }

    public ClSymbolicWeight add(ClSymbolicWeight cl) {
        // assert(cl.cLevels() == cLevels());

        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] += cl._values[i];
        }
        return clsw;
    }

    public ClSymbolicWeight subtract(ClSymbolicWeight cl) {
        // assert(cl.cLevels() == cLevels());

        ClSymbolicWeight clsw = (ClSymbolicWeight) clone();
        for (int i = 0; i < _values.length; i++) {
            clsw._values[i] -= cl._values[i];
        }
        return clsw;
    }

    public boolean lessThan(ClSymbolicWeight cl) {
        // assert cl.cLevels() == cLevels()
        for (int i = 0; i < _values.length; i++) {
            if (_values[i] < cl._values[i])
                return true;
            else if (_values[i] > cl._values[i])
                return false;
        }
        return false; // they are equal
    }

    public boolean lessThanOrEqual(ClSymbolicWeight cl) {
        // assert cl.cLevels() == cLevels()
        for (int i = 0; i < _values.length; i++) {
            if (_values[i] < cl._values[i])
                return true;
            else if (_values[i] > cl._values[i])
                return false;
        }
        return true; // they are equal
    }

    public boolean equal(ClSymbolicWeight cl) {
        for (int i = 0; i < _values.length; i++) {
            if (_values[i] != cl._values[i])
                return false;
        }
        return true; // they are equal
    }

    public boolean greaterThan(ClSymbolicWeight cl) {
        return !this.lessThanOrEqual(cl);
    }

    public boolean greaterThanOrEqual(ClSymbolicWeight cl) {
        return !this.lessThan(cl);
    }

    public boolean isNegative() {
        return this.lessThan(clsZero);
    }

    public double asDouble() {
        // ClSymbolicWeight clsw = (ClSymbolicWeight) clone(); // LM--Not used
        double sum = 0;
        double factor = 1;
        double multiplier = 1000;
        for (int i = _values.length - 1; i >= 0; i--) {
            sum += _values[i] * factor;
            factor *= multiplier;
        }
        return sum;
    }

    public String toString() {
        StringBuffer bstr = new StringBuffer("[");
        for (int i = 0; i < _values.length - 1; i++) {
            bstr.append(_values[i]);
            bstr.append(",");
        }
        bstr.append(_values[_values.length - 1]);
        bstr.append("]");
        return bstr.toString();
    }

    public int cLevels() {
        return _values.length;
    }

    private double[] _values;

}

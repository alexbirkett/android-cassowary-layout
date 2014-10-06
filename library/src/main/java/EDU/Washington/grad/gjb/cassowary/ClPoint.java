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

public class ClPoint {
    public ClPoint(double x, double y) {
        _clv_x = new ClVariable(x);
        _clv_y = new ClVariable(y);
    }

    public ClPoint(double x, double y, int a) {
        _clv_x = new ClVariable("x" + a, x);
        _clv_y = new ClVariable("y" + a, y);
    }

    public ClPoint(ClVariable clv_x, ClVariable clv_y) {
        _clv_x = clv_x;
        _clv_y = clv_y;
    }

    public ClVariable X() {
        return _clv_x;
    }

    public ClVariable Y() {
        return _clv_y;
    }

    // use only before adding into the solver
    public void SetXY(double x, double y) {
        _clv_x.set_value(x);
        _clv_y.set_value(y);
    }

    public void SetXY(ClVariable clv_x, ClVariable clv_y) {
        _clv_x = clv_x;
        _clv_y = clv_y;
    }

    public double Xvalue() {
        return X().value();
    }

    public double Yvalue() {
        return Y().value();
    }

    public String toString() {
        return "(" + _clv_x.toString() + ", " + _clv_y.toString() + ")";
    }

    private ClVariable _clv_x;

    private ClVariable _clv_y;

}

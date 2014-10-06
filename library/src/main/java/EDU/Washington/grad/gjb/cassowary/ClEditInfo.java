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

// ClEditInfo is a privately-used class
// that just wraps a constraint, its positive and negative
// error variables, and its prior edit constant.
// It is used as values in _editVarMap, and replaces
// the parallel vectors of error variables and previous edit
// constants from the Smalltalk version of the code.

class ClEditInfo {
    public ClEditInfo(ClConstraint cn_, ClSlackVariable eplus_,
            ClSlackVariable eminus_, double prevEditConstant_, int i_) {
        cn = cn_;
        clvEditPlus = eplus_;
        clvEditMinus = eminus_;
        prevEditConstant = prevEditConstant_;
        i = i_;
    }

    public int Index() {
        return i;
    }

    public ClConstraint Constraint() {
        return cn;
    }

    public ClSlackVariable ClvEditPlus() {
        return clvEditPlus;
    }

    public ClSlackVariable ClvEditMinus() {
        return clvEditMinus;
    }

    public double PrevEditConstant() {
        return prevEditConstant;
    }

    public void SetPrevEditConstant(double prevEditConstant_) {
        prevEditConstant = prevEditConstant_;
    }

    private ClConstraint cn;
    private ClSlackVariable clvEditPlus;
    private ClSlackVariable clvEditMinus;
    private double prevEditConstant;
    private int i;

}

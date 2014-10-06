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

abstract class ClEditOrStayConstraint extends ClConstraint {

    public ClEditOrStayConstraint(ClVariable var, ClStrength strength,
            double weight) {
        super(strength, weight);
        _variable = var;
        _expression = new ClLinearExpression(_variable, -1.0, _variable.value());
    }

    public ClEditOrStayConstraint(ClVariable var, ClStrength strength) {
        this(var, strength, 1.0);
    }

    public ClEditOrStayConstraint(ClVariable var) {
        this(var, ClStrength.required, 1.0);
        _variable = var;
    }

    public ClVariable variable() {
        return _variable;
    }

    public ClLinearExpression expression() {
        return _expression;
    }

    /*
     * private void setVariable(ClVariable v) { _variable = v; }
     */
    protected ClVariable _variable;
    // cache the expression
    private ClLinearExpression _expression;

}

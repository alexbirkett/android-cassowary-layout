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

public abstract class ClConstraint {

    public ClConstraint(ClStrength strength, double weight) {
        _strength = strength;
        _weight = weight;
    }

    public ClConstraint(ClStrength strength) {
        _strength = strength;
        _weight = 1.0;
    }

    public ClConstraint() {
        _strength = ClStrength.required;
        _weight = 1.0;
    }

    public abstract ClLinearExpression expression();

    public boolean isEditConstraint() {
        return false;
    }

    public boolean isInequality() {
        return false;
    }

    public boolean isRequired() {
        return _strength.isRequired();
    }

    public boolean isStayConstraint() {
        return false;
    }

    public ClStrength strength() {
        return _strength;
    }

    public double weight() {
        return _weight;
    }

    public String toString() {
        return _strength.toString() + " {" + weight() + "} (" + expression();
    }

    public void setAttachedObject(Object o) {
        _attachedObject = o;
    }

    public Object getAttachedObject() {
        return _attachedObject;
    }

    /*
     * private void setStrength(ClStrength strength) { _strength = strength; }
     * 
     * private void setWeight(double weight) { _weight = weight; }
     */
    private ClStrength _strength;
    private double _weight;

    private Object _attachedObject;
}

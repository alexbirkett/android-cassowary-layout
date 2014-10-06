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

import java.util.*;

public class ClVariable extends ClAbstractVariable {

    public ClVariable(String name, double value) {
        super(name);
        _value = value;
        if (_ourVarMap != null) {
            _ourVarMap.put(name, this);
        }
    }

    public ClVariable(String name) {
        super(name);
        _value = 0.0;
        if (_ourVarMap != null) {
            _ourVarMap.put(name, this);
        }
    }

    public ClVariable(double value) {
        _value = value;
    }

    public ClVariable() {
        _value = 0.0;
    }

    public ClVariable(long number, String prefix, double value) {
        super(number, prefix);
        _value = value;
    }

    public ClVariable(long number, String prefix) {
        super(number, prefix);
        _value = 0.0;
    }

    public boolean isDummy() {
        return false;
    }

    public boolean isExternal() {
        return true;
    }

    public boolean isPivotable() {
        return false;
    }

    public boolean isRestricted() {
        return false;
    }

    public String toString() {
        return "[" + name() + ":" + _value + "]";
    }

    // change the value held -- should *not* use this if the variable is
    // in a solver -- instead use addEditVar() and suggestValue() interface
    public final double value() {
        return _value;
    }

    public final void set_value(double value) {
        _value = value;
    }

    // permit overriding in subclasses in case something needs to be
    // done when the value is changed by the solver
    // may be called when the value hasn't actually changed -- just
    // means the solver is setting the external variable
    public void change_value(double value) {
        _value = value;
    }

    public void setAttachedObject(Object o) {
        _attachedObject = o;
    }

    public Object getAttachedObject() {
        return _attachedObject;
    }

    public static void setVarMap(Hashtable<String, ClVariable> map) {
        _ourVarMap = map;
    }

    public static Hashtable<String, ClVariable> getVarMap() {
        return _ourVarMap;
    }

    private static Hashtable<String, ClVariable> _ourVarMap;

    private double _value;

    private Object _attachedObject;

}

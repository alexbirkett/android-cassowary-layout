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

public abstract class ClAbstractVariable {
    public ClAbstractVariable(String name) {
        // hash_code = iVariableNumber;
        _name = name;
        iVariableNumber++;
    }

    public ClAbstractVariable() {
        // hash_code = iVariableNumber;
        _name = "v" + iVariableNumber;
        iVariableNumber++;
    }

    public ClAbstractVariable(long varnumber, String prefix) {
        // hash_code = iVariableNumber;
        _name = prefix + varnumber;
        iVariableNumber++;
    }

    public String name() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public boolean isDummy() {
        return false;
    }

    public abstract boolean isExternal();

    public abstract boolean isPivotable();

    public abstract boolean isRestricted();

    public abstract String toString();

    public static int numCreated() {
        return iVariableNumber;
    }

    // for debugging
    // public final int hashCode() { return hash_code; }

    private String _name;

    // for debugging
    // private int hash_code;

    private static int iVariableNumber;

}

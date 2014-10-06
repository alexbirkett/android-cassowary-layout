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

class ClObjectiveVariable extends ClAbstractVariable {
    public ClObjectiveVariable(String name) {
        super(name);
    }

    public ClObjectiveVariable(long number, String prefix) {
        super(number, prefix);
    }

    public String toString()
    // { return "[" + name() + ":obj:" + hashCode() + "]"; }
    {
        return "[" + name() + ":obj]";
    }

    public boolean isExternal() {
        return false;
    }

    public boolean isPivotable() {
        return false;
    }

    public boolean isRestricted() {
        return false;
    }

}

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

public class ClStayConstraint extends ClEditOrStayConstraint {

    public ClStayConstraint(ClVariable var, ClStrength strength, double weight) {
        super(var, strength, weight);
    }

    public ClStayConstraint(ClVariable var, ClStrength strength) {
        super(var, strength, 1.0);
    }

    public ClStayConstraint(ClVariable var) {
        super(var, ClStrength.weak, 1.0);
    }

    public boolean isStayConstraint() {
        return true;
    }

    public String toString() {
        return "stay " + super.toString();
    }

}

/*
 * Copyright (C) 2014 Agens AS
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

package no.agens.cassowarylayout;



import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.SimplexSolver;
import org.pybee.cassowary.Variable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import no.agens.cassowarylayout.util.CassowaryUtil;
import no.agens.cassowarylayout.util.TimerUtil;

/**
 * Created by alex on 25/09/2014.
 */
public abstract class Node {

    private static final String LOG_TAG = "CassowaryNode";

    protected SimplexSolver solver;

    protected HashMap<String, Variable> variables = new HashMap<String, Variable>();
    protected HashMap<String, Constraint> constraints = new HashMap<String, Constraint>();

    public Node(SimplexSolver solver) {
        this.solver = solver;
    }

    public void setVariableToValue(String nameVariable, double value) {
        long timeBefore = System.currentTimeMillis();
        Constraint constraint = constraints.get(nameVariable);
        constraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getVariable(nameVariable), constraint, value, solver);
        constraints.put(nameVariable, constraint);
        Log.d(LOG_TAG, "setVariableToValue name " + nameVariable + " value " + value + " took " + TimerUtil.since(timeBefore));
    }

    public void setVariableToAtMost(String nameVariable, double value) {
        Constraint constraint = constraints.get(nameVariable);
        constraint =  CassowaryUtil.createOrUpdateLeqInequalityConstraint(getVariable(nameVariable), constraint, value, solver);
        constraints.put(nameVariable, constraint);
    }

    public boolean hasVariable(String variableName) {
        return variables.containsKey(variableName);
    }

    public Variable getVariable(String name) {

        Variable variable = variables.get(name);

        if (variable == null) {
            variable = new Variable();
            createImplicitConstraints(name, variable);
            variables.put(name, variable);
        }
        return variable;
    }

    protected abstract void createImplicitConstraints(String variableName, Variable variable);

    public double getVariableValue(String variableName) {
        return getVariable(variableName).value();
    }
}

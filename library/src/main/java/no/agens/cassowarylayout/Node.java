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

    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String CENTERX = "centerX";
    public static final String CENTERY = "centerY";
    public static final String INTRINSIC_WIDTH = "intrinsicWidth";
    public static final String INTRINSIC_HEIGHT = "intrinsicHeight";
    public static final String CONTENT_HEIGHT = "contentHeight";

    public Node(SimplexSolver solver) {
        this.solver = solver;
    }

    public Variable getLeft() {
        return getVariable(LEFT);
    }

    public Variable getTop() {
        return getVariable(TOP);
    }

    public Variable getHeight() {
        return getVariable(HEIGHT);
    }

    public Variable getWidth() {
        return getVariable(WIDTH);
    }

    public Variable getBottom() {
        return getVariable(BOTTOM);
    }

    public Variable getRight() {
        return getVariable(RIGHT);
    }

    public Variable getCenterX() {
        return getVariable(CENTERX);
    }

    public Variable getCenterY() {
        return getVariable(CENTERY);
    }

    public void setIntrinsicWidth(int intrinsicWidth) {
        setVariableToValue(INTRINSIC_WIDTH, intrinsicWidth);
    }

    public void setIntrinsicHeight(int intrinsicHeight) {
        setVariableToValue(INTRINSIC_HEIGHT, intrinsicHeight);
    }

    public void setVariableToValue(String nameVariable, double value) {
        long timeBefore = System.nanoTime();
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

    public boolean hasIntrinsicHeight() {
        return hasVariable(INTRINSIC_HEIGHT);

    }
    public Variable getIntrinsicHeight() {
        return getVariable(INTRINSIC_HEIGHT);
    }

    public boolean hasIntrinsicWidth() {
        return hasVariable(INTRINSIC_WIDTH);
    }
    public Variable getIntrinsicWidth() {
        return getVariable(INTRINSIC_WIDTH);
    }

    public Variable getVariable(String name) {

        name = getCanonicalName(name);
        Variable variable = variables.get(name);

        if (variable == null) {
            variable = new Variable();
            createImplicitConstraints(name, variable);
            variables.put(name, variable);
        }
        return variable;
    }

    public boolean hasVariable(String name) {
        name = getCanonicalName(name);
        return variables.containsKey(name);
    }

    protected abstract void createImplicitConstraints(String variableName, Variable variable);

    private String getCanonicalName(String name) {
        String canonicalName = name;
        if ("x".equals(name)) {
            canonicalName = LEFT;
        } else if ("y".equals(name)) {
            canonicalName = TOP;
        } else if ("x2".equals(name)) {
            canonicalName = RIGHT;
        } else if ("y2".equals(name)) {
            canonicalName = BOTTOM;
        }
        return canonicalName;
    }

    public double getVariableValue(String variableName) {
        return getVariable(variableName).value();
    }
}

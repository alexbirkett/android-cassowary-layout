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


import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;


import java.util.HashMap;

import no.agens.cassowarylayout.util.CassowaryUtil;

/**
 * Created by alex on 25/09/2014.
 */
public abstract class Node {

    protected ClSimplexSolver solver;

    protected HashMap<String, ClVariable> variables = new HashMap<String, ClVariable>();
    protected HashMap<String, ClConstraint> constraints = new HashMap<String, ClConstraint>();

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

    public Node(ClSimplexSolver solver) {
        this.solver = solver;
    }

    public ClVariable getLeft() {
        return getVariable(LEFT);
    }

    public ClVariable getTop() {
        return getVariable(TOP);
    }

    public ClVariable getHeight() {
        return getVariable(HEIGHT);
    }

    public ClVariable getWidth() {
        return getVariable(WIDTH);
    }

    public ClVariable getBottom() {
        return getVariable(BOTTOM);
    }

    public ClVariable getRight() {
        return getVariable(RIGHT);
    }

    public ClVariable getCenterX() {
        return getVariable(CENTERX);
    }

    public ClVariable getCenterY() {
        return getVariable(CENTERY);
    }

    public void setIntrinsicWidth(int intrinsicWidth) {
        setVariableToValue(INTRINSIC_WIDTH, intrinsicWidth);
    }

    public void setIntrinsicHeight(int intrinsicHeight) {
        setVariableToValue(INTRINSIC_HEIGHT, intrinsicHeight);
    }

    public void setVariableToValue(String nameVariable, double value) {
        ClConstraint constraint = constraints.get(nameVariable);
        constraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getVariable(nameVariable), constraint, value, solver);
        constraints.put(nameVariable, constraint);
    }


    public void setVariableToAtMost(String nameVariable, double value) {
        ClConstraint constraint = constraints.get(nameVariable);
        constraint =  CassowaryUtil.createOrUpdateLeqInequalityConstraint(getVariable(nameVariable), constraint, value, solver);
        constraints.put(nameVariable, constraint);
    }

    public boolean hasIntrinsicHeight() {
        return hasVariable(INTRINSIC_HEIGHT);

    }
    public ClVariable getIntrinsicHeight() {
        return getVariable(INTRINSIC_HEIGHT);
    }

    public boolean hasIntrinsicWidth() {
        return hasVariable(INTRINSIC_WIDTH);
    }
    public ClVariable getIntrinsicWidth() {
        return getVariable(INTRINSIC_WIDTH);
    }

    public ClVariable getVariable(String name) {

        if (WIDTH.equals(name)) {
            int i = 0;
            i++;
        }
        name = getCanonicalName(name);
        ClVariable variable = variables.get(name);

        if (variable == null) {
            variable = new ClVariable();
            createImplicitConstraints(name, variable);
            variables.put(name, variable);
        }
        return variable;
    }

    public boolean hasVariable(String name) {
        name = getCanonicalName(name);
        return variables.containsKey(name);
    }

    protected abstract void createImplicitConstraints(String variableName, ClVariable variable);

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
        return getVariable(variableName).getValue();
    }
}

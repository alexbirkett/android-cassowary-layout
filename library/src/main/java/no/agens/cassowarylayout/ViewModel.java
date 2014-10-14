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


import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;
import org.klomp.cassowary.clconstraint.ClLinearInequality;

/**
 * Created by alex on 25/09/2014.
 */
public class ViewModel {

    private ClSimplexSolver solver;
    private ContainerModel containerModel;

    public ViewModel(ClSimplexSolver solver, ContainerModel containerModel) {
        this.solver = solver;
        this.containerModel = containerModel;
        createDefaultConstraints();
    }

    private ClVariable x = new ClVariable();
    private ClVariable y = new ClVariable();
    private ClVariable width = new ClVariable();
    private ClVariable height = new ClVariable();
    private ClVariable y2;
    private ClVariable x2;
    private ClVariable centerX;
    private ClVariable centerY;
    private ClVariable intrinsicWidth;
    private ClVariable intrinsicHeight;
    private ClConstraint intrinsicWidthConstraint;
    private ClConstraint intrinsicHeightConstraint;

    public ClVariable getX() {
        return x;
    }

    public ClVariable getY() {
        return y;
    }

    public ClVariable getHeight() {
        return height;
    }

    public ClVariable getWidth() {
        return width;
    }

    public ClVariable getY2() {
        if (y2 == null) {
            y2 = new ClVariable();
            solver.addConstraint(new ClLinearEquation(y2, new ClLinearExpression(getY()).plus(getHeight()), ClStrength.required));
        }
        return y2;
    }

    public ClVariable getX2() {
        if (x2 == null) {
            x2 = new ClVariable();
            solver.addConstraint(new ClLinearEquation(x2, new ClLinearExpression(getX()).plus(getWidth()), ClStrength.required));
        }
        return x2;
    }

    public ClVariable getCenterX() {
        if (centerX == null) {
            centerX = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerX, new ClLinearExpression(getWidth()).divide(2).plus(getX()), ClStrength.required));
        }
        return centerX;
    }

    public ClVariable getCenterY() {
        if (centerY == null) {
            centerY = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerY, new ClLinearExpression(getHeight()).divide(2).plus(getY()), ClStrength.required));
        }
        return centerY;
    }

    public ClVariable getVariableByName(String name) {
        ClVariable variable = null;
        if ("left".equals(name) || "x".equals(name)) {
            variable = getX();
        } else if ("top".equals(name) || "y".equals(name)) {
            variable = getY();
        } else if ("bottom".equals(name) || "y2".equals(name)) {
            variable = getY2();
        } else if ("right".equals(name) || "x2".equals(name)) {
            variable = getX2();
        } else if ("height".equals(name)) {
            variable = getHeight();
        } else if ("width".equals(name)) {
            variable = getWidth();
        } else if ("centerX".equals(name)) {
            variable = getCenterX();
        } else if ("centerY".equals(name)) {
            variable = getCenterY();
        } else if ("intrinsicHeight".equals(name)) {
            variable = createIntrinsicHeightIfRequired();
        } else if ("intrinsicWidth".equals(name)) {
            variable = createIntrinsicWidthIfRequired();
        }
        return variable;
    }

    public ClVariable createIntrinsicWidthIfRequired() {
        if (intrinsicWidth == null) {
            intrinsicWidth = new ClVariable();
            solver.addStay(intrinsicWidth);
        }
        return intrinsicWidth;
    }

    public ClVariable createIntrinsicHeightIfRequired() {
        if (intrinsicHeight == null) {
            intrinsicHeight = new ClVariable();
            solver.addStay(intrinsicHeight);
        }
        return intrinsicHeight;
    }

    public void setIntrinsicWidth(int intrinsicWidth) {
        if (intrinsicWidthConstraint != null) {
            solver.removeConstraint(intrinsicWidthConstraint);
        }

        if (intrinsicWidthConstraint == null) {
            intrinsicWidthConstraint = new ClLinearEquation(getIntrinsicWidth(), new ClLinearExpression(intrinsicWidth), ClStrength.required);
        }

        solver.addConstraint(intrinsicWidthConstraint);
    }

    public void setIntrinsicHeight(int intrinsicHeight) {
        if (intrinsicHeightConstraint != null) {
            solver.removeConstraint(intrinsicHeightConstraint);
        }

        if (intrinsicHeightConstraint == null) {
            intrinsicHeightConstraint = new ClLinearEquation(getIntrinsicHeight(), new ClLinearExpression(intrinsicHeight), ClStrength.required);
        }

        solver.addConstraint(intrinsicHeightConstraint);
    }

    public ClVariable getIntrinsicHeight() {
        return intrinsicHeight;
    }

    public ClVariable getIntrinsicWidth() {
        return intrinsicWidth;
    }

    private void createDefaultConstraints() {
        createWidthConstraint();
        createHeightConstraint();
    }

    private void createWidthConstraint() {
        ClConstraint constraint = new ClLinearInequality(getX2(), CL.LEQ, containerModel.getWidth());
        solver.addConstraint(constraint);
    }

    private void createHeightConstraint() {
        ClConstraint constraint = new ClLinearInequality(getY2(), CL.LEQ, containerModel.getHeight());
        solver.addConstraint(constraint);
    }
}

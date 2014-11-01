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

import no.agens.cassowarylayout.util.CassowaryUtil;

/**
 * Created by alex on 25/09/2014.
 */
public class Node {

    private ClSimplexSolver solver;
    private ContainerNode containerNode;

    public Node(ClSimplexSolver solver, ContainerNode containerNode) {
        this.solver = solver;
        this.containerNode = containerNode;
        createDefaultConstraints();
    }

    private ClVariable left = new ClVariable();
    private ClVariable top = new ClVariable();
    private ClVariable width = new ClVariable();
    private ClVariable height = new ClVariable();
    private ClVariable bottom;
    private ClVariable right;
    private ClVariable centerX;
    private ClVariable centerY;
    private ClVariable intrinsicWidth;
    private ClVariable intrinsicHeight;
    private ClConstraint intrinsicWidthConstraint;
    private ClConstraint intrinsicHeightConstraint;

    public ClVariable getLeft() {
        return left;
    }

    public ClVariable getTop() {
        return top;
    }

    public ClVariable getHeight() {
        return height;
    }

    public ClVariable getWidth() {
        return width;
    }

    public ClVariable getBottom() {
        if (bottom == null) {
            bottom = new ClVariable();
            solver.addConstraint(new ClLinearEquation(bottom, new ClLinearExpression(getTop()).plus(getHeight()), ClStrength.required));
        }
        return bottom;
    }

    public ClVariable getRight() {
        if (right == null) {
            right = new ClVariable();
            solver.addConstraint(new ClLinearEquation(right, new ClLinearExpression(getLeft()).plus(getWidth()), ClStrength.required));
        }
        return right;
    }

    public ClVariable getCenterX() {
        if (centerX == null) {
            centerX = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerX, new ClLinearExpression(getWidth()).divide(2).plus(getLeft()), ClStrength.required));
        }
        return centerX;
    }

    public ClVariable getCenterY() {
        if (centerY == null) {
            centerY = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerY, new ClLinearExpression(getHeight()).divide(2).plus(getTop()), ClStrength.required));
        }
        return centerY;
    }

    public ClVariable getVariableByName(String name) {
        ClVariable variable = null;
        if ("left".equals(name) || "x".equals(name)) {
            variable = getLeft();
        } else if ("top".equals(name) || "y".equals(name)) {
            variable = getTop();
        } else if ("bottom".equals(name) || "y2".equals(name)) {
            variable = getBottom();
        } else if ("right".equals(name) || "x2".equals(name)) {
            variable = getRight();
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
        }
        return intrinsicWidth;
    }

    public ClVariable createIntrinsicHeightIfRequired() {
        if (intrinsicHeight == null) {
            intrinsicHeight = new ClVariable();
        }
        return intrinsicHeight;
    }

    public void setIntrinsicWidth(int intrinsicWidth) {
        intrinsicWidthConstraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getIntrinsicWidth(), intrinsicWidthConstraint, intrinsicWidth, solver);
    }

    public void setIntrinsicHeight(int intrinsicHeight) {
        intrinsicHeightConstraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getIntrinsicHeight(), intrinsicHeightConstraint, intrinsicHeight, solver);
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
        ClConstraint constraint = new ClLinearInequality(getRight(), CL.LEQ, containerNode.getWidth());
        solver.addConstraint(constraint);
    }

    private void createHeightConstraint() {
        ClConstraint constraint = new ClLinearInequality(getBottom(), CL.LEQ, containerNode.getHeight());
        solver.addConstraint(constraint);
    }
}

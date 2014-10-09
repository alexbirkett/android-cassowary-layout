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


import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClLinearEquation;

/**
 * Created by alex on 25/09/2014.
 */
public class ViewModel {

    private ClSimplexSolver solver;

    public ViewModel(ClSimplexSolver solver) {
        this.solver = solver;
    }
    private ClVariable x = new ClVariable();
    private ClVariable y = new ClVariable();
    private ClVariable width = new ClVariable();
    private ClVariable height = new ClVariable();
    private ClVariable y2;
    private ClVariable x2;
    private ClVariable centerX;
    private ClVariable centerY;

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
}

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


import android.test.AndroidTestCase;

import junit.framework.TestCase;

import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.Expression;
import org.pybee.cassowary.SimplexSolver;
import org.pybee.cassowary.Variable;

import no.agens.cassowarylayout.util.DimensionParser;

/**
 * Created by alex on 25/09/2014.
 */
public class CassowaryConstraintParserTest extends AndroidTestCase {



    public void testExpression() {


        final Variable blueX = new Variable();
        final Variable blueY = new Variable();
        final Variable blueHeight = new Variable();
        final Variable blueWidth = new Variable();

        final Variable greenX = new Variable();
        final Variable greenY = new Variable();
        final Variable greenHeight = new Variable();
        final Variable greenWidth = new Variable();

        CassowaryVariableResolver variableResolver = new CassowaryVariableResolver() {
            @Override
            public Variable resolveVariable(String variableName) {
                if ("blue.x".equals(variableName)) {
                    return blueX;
                } else if ("blue.y".equals(variableName)) {
                    return blueY;
                } else if ("blue.height".equals(variableName)) {
                    return blueHeight;
                } else if ("blue.width".equals(variableName)) {
                    return blueWidth;
                } else if ("green.x".equals(variableName)) {
                    return greenX;
                } else if ("green.y".equals(variableName)) {
                    return greenY;
                } else if ("green.height".equals(variableName)) {
                    return greenHeight;
                } else if ("green.width".equals(variableName)) {
                    return greenWidth;
                }
                return null;
            }

            @Override
            public Expression resolveConstant(String constantName) {
                Expression expression = null;
                Double value;

                try {
                    value = new Double(Double.parseDouble(constantName));
                } catch (NumberFormatException e) {
                    value = DimensionParser.getDimension(constantName, getContext());

                }

                if (value != null) {
                    expression = new Expression(value);
                }
                return expression;
            }
        };


        Constraint constraint = CassowaryConstraintParser.parseConstraint("green.width == 10", variableResolver);

        SimplexSolver solver = new SimplexSolver();
        solver.addConstraint(constraint);
        solver.solve();

        assertEquals(10, greenWidth.value());


    }
}

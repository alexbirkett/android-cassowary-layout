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

import junit.framework.TestCase;

/**
 * Created by alex on 15/10/2014.
 */
public class ConstraintParserTest extends TestCase {

    public void testEqualsWithDoubleEqualsSymbol() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y == blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.EQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testEqualsWithEq() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y EQ blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.EQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testLessThanOrEqualsWithLeq() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y LEQ blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.LEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testLessThanOrEqualsWithLeqSymbol() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y <= blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.LEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testGreaterThanOrEqualsWithWithGeq() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y GEQ blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testGreaterThanOrEqualsWithGeqSymbol() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testWithoutStrength() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y)");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testWeak() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y) !weak");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.WEAK, constraint.getStrength());
    }

    public void testMedium() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y) !medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }

    public void testStrong() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y) !strong");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.STRONG, constraint.getStrength());
    }

    public void testRequired() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y) !required");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.REQUIRED, constraint.getStrength());
    }

    public void testStrengthWithoutSpaceBeforeBang() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >= blue.y - (yellow.y * green.y)!medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }

    public void testWithoutSpaceBeforeOperator() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y>= blue.y - (yellow.y * green.y) !medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }

    public void testWithoutSpaceAfterOperator() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y >=blue.y - (yellow.y * green.y) !medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }

    public void testWithoutSpaceBeforeAndAfterOperator() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y>=blue.y - (yellow.y * green.y) !medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }

    public void testWithoutAnySpace() {
        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint("green.y>=blue.y - (yellow.y * green.y)!medium");

        assertEquals("blue.y - (yellow.y * green.y)", constraint.getExpression());
        assertEquals("green.y", constraint.getVariable());
        assertEquals(ConstraintParser.Constraint.Operator.GEQ, constraint.getOperator());
        assertEquals(ConstraintParser.Constraint.Strength.MEDIUM, constraint.getStrength());
    }
}

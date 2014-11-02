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


import android.util.Log;


import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.Expression;
import org.pybee.cassowary.Strength;
import org.pybee.cassowary.Variable;

import java.util.List;
import java.util.Stack;

import no.agens.cassowarylayout.util.ExpressionTokenizer;
import no.agens.cassowarylayout.util.InfixToPostfix;

/**
 * Created by alex on 25/09/2014.
 */
public class CassowaryConstraintParser {


    private static final String LOG_TAG = "CassowaryConstraintParser";


    public static Constraint parseConstraint(String constraintString, CassowaryVariableResolver variableResolver) {

        Log.d(LOG_TAG, "CassowaryConstraintParser.parseConstraint " + constraintString);


        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint(constraintString);

        Expression expression = resolveExpression(constraint.getExpression(), variableResolver);
        Variable variable = variableResolver.resolveVariable(constraint.getVariable());

        switch(constraint.getOperator()) {
            case EQ:
                return new Constraint(variable, Constraint.Operator.EQ, expression, getStrength(constraint.getStrength()));
            case GEQ:
                return new Constraint(variable, Constraint.Operator.GEQ, expression, getStrength(constraint.getStrength()));
            case LEQ:
                return new Constraint(variable, Constraint.Operator.LEQ, expression, getStrength(constraint.getStrength()));

        }
        return null;
    }

    private static Strength getStrength(ConstraintParser.Constraint.Strength strength) {
        switch (strength) {
            case WEAK:
                return Strength.WEAK;
            case MEDIUM:
                return Strength.MEDIUM;
            case STRONG:
                return Strength.STRONG;
            case REQUIRED:
                return Strength.REQUIRED;
        }
        return Strength.MEDIUM;
    }

    public static Expression resolveExpression(String expressionString, CassowaryVariableResolver variableResolver) {

        List<String> postFixExpression = InfixToPostfix.infixToPostfix(ExpressionTokenizer.tokenizeExpression(expressionString));

        Stack<Expression> linearExpressionsStack = new Stack<Expression>();

        for (String expression : postFixExpression) {
            if ("+".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().plus(linearExpressionsStack.pop()));
            } else if ("-".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().subtractFrom(linearExpressionsStack.pop()));
            } else if ("/".equals(expression)) {
                Expression denominator = linearExpressionsStack.pop();
                Expression numerator = linearExpressionsStack.pop();
                linearExpressionsStack.push(numerator.divide(denominator));
            } else if ("*".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().times(linearExpressionsStack.pop()));
            } else {
                Expression linearExpression =  variableResolver.resolveConstant(expression);
                if (linearExpression == null) {
                    linearExpression = new Expression(variableResolver.resolveVariable(expression));
                }

                linearExpressionsStack.push(linearExpression);
            }
        }

        return linearExpressionsStack.pop();
    }
}

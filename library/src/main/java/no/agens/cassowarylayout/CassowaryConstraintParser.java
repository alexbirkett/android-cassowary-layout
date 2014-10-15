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

import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;
import org.klomp.cassowary.clconstraint.ClLinearInequality;

import java.util.List;
import java.util.Stack;

import no.agens.cassowarylayout.util.ExpressionTokenizer;
import no.agens.cassowarylayout.util.InfixToPostfix;

/**
 * Created by alex on 25/09/2014.
 */
public class CassowaryConstraintParser {


    private static final String LOG_TAG = "CassowaryConstraintParser";


    public static ClConstraint parseConstraint(String constraintString, CassowaryVariableResolver variableResolver) {

        Log.d(LOG_TAG, "CassowaryConstraintParser.parseConstraint " + constraintString);


        ConstraintParser.Constraint constraint = ConstraintParser.parseConstraint(constraintString);

        ClLinearExpression expression = resolveExpression(constraint.getExpression(), variableResolver);
        ClVariable variable = variableResolver.resolveVariable(constraint.getVariable());

        switch(constraint.getOperator()) {
            case EQ:
                return new ClLinearEquation(variable, expression, getStrength(constraint.getStrength()));
            case GEQ:
                return new ClLinearInequality(variable, CL.GEQ, expression, getStrength(constraint.getStrength()));
            case LEQ:
                return new ClLinearInequality(variable, CL.LEQ, expression, getStrength(constraint.getStrength()));

        }
        return null;
    }

    private static ClStrength getStrength(ConstraintParser.Constraint.Strength strength) {
        switch (strength) {
            case WEAK:
                return ClStrength.weak;
            case MEDIUM:
                return ClStrength.medium;
            case STRONG:
                return ClStrength.strong;
            case REQUIRED:
                return ClStrength.required;
        }
        return ClStrength.required;
    }

    public static ClLinearExpression resolveExpression(String expressionString, CassowaryVariableResolver variableResolver) {

        List<String> postFixExpression = InfixToPostfix.infixToPostfix(ExpressionTokenizer.tokenizeExpression(expressionString));

        Stack<ClLinearExpression> linearExpressionsStack = new Stack<ClLinearExpression>();

        for (String expression : postFixExpression) {
            if ("+".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().plus(linearExpressionsStack.pop()));
            } else if ("-".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().subtractFrom(linearExpressionsStack.pop()));
            } else if ("/".equals(expression)) {
                ClLinearExpression denominator = linearExpressionsStack.pop();
                ClLinearExpression numerator = linearExpressionsStack.pop();
                linearExpressionsStack.push(numerator.divide(denominator));
            } else if ("*".equals(expression)) {
                linearExpressionsStack.push(linearExpressionsStack.pop().times(linearExpressionsStack.pop()));
            } else {
                ClLinearExpression linearExpression =  variableResolver.resolveConstant(expression);
                if (linearExpression == null) {
                    linearExpression = new ClLinearExpression(variableResolver.resolveVariable(expression));
                }

                linearExpressionsStack.push(linearExpression);
            }
        }

        return linearExpressionsStack.pop();
    }
}

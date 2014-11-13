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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 15/10/2014.
 */
public class ConstraintParser {

    private static final String LOG_TAG = "ConstraintParser";

    private static final Pattern pattern = Pattern.compile("\\s*(.*?)\\s*(<=|==|>=|[GL]?EQ)\\s*(.*?)\\s*(!(required|strong|medium|weak))?");


    public static class Constraint {

        enum Operator {
            LEQ,
            EQ,
            GEQ
        }

        enum Strength {
            WEAK,
            MEDIUM,
            STRONG,
            REQUIRED
        }

        private String variable;
        private String expression;
        private Strength strength;
        private Operator operator;

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public Strength getStrength() {
            return strength;
        }

        public void setStrength(Strength strength) {
            this.strength = strength;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }
    }

    public static Constraint parseConstraint(String constraintString) {
        Constraint constraint = new Constraint();

        Matcher matcher = pattern.matcher(constraintString);

        matcher.find();

        if (matcher.matches()) {
            String variable = matcher.group(1);
            if (variable != null) {
                constraint.setVariable(variable);
            }

            constraint.setOperator(parseOperator(matcher.group(2)));

            String expression = matcher.group(3);
            if (expression != null) {
                constraint.setExpression(expression);
            }

            constraint.setStrength(parseStrength(matcher.group(4)));
        }

        return constraint;
    }

    private static Constraint.Operator parseOperator(String operatorString) {

        Constraint.Operator operator = null;
        if ("EQ".equals(operatorString) || "==".equals(operatorString)) {
            operator = Constraint.Operator.EQ;
        } else if ("GEQ".equals(operatorString) || ">=".equals(operatorString)) {
            operator = Constraint.Operator.GEQ;
        } else if ("LEQ".equals(operatorString) || "<=".equals(operatorString)) {
            operator = Constraint.Operator.LEQ;
        }
        return operator;
    }

    private static Constraint.Strength parseStrength(String strengthString) {

        Constraint.Strength strength =  Constraint.Strength.REQUIRED;
        if ("!required".equals(strengthString)) {
            strength = Constraint.Strength.REQUIRED;
        } else if ("!strong".equals(strengthString)) {
            strength = Constraint.Strength.STRONG;
        } else if ("!medium".equals(strengthString)) {
            strength = Constraint.Strength.MEDIUM;
        } else if ("!weak".equals(strengthString)) {
            strength = Constraint.Strength.WEAK;
        }
        return strength;
    }

}

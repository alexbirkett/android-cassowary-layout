package no.agens.cassowarylayout;/*
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



import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.birkett.kiwi.Constraint;
import no.birkett.kiwi.Expression;
import no.birkett.kiwi.NonlinearExpressionException;
import no.birkett.kiwi.Strength;
import no.birkett.kiwi.Symbolics;
import no.birkett.kiwi.Term;
import no.birkett.kiwi.Variable;

/**
 * Created by alex on 25/09/2014.
 */
public class ConstraintParser {

    private static final Pattern PATTERN = Pattern.compile("\\s*(.*?)\\s*(<=|==|>=|[GL]?EQ)\\s*(.*?)\\s*(!(required|strong|medium|weak))?");

    final static String OPS = "-+/*^";

    public interface CassowaryVariableResolver {

        Variable resolveVariable(String variableName);
        Expression resolveConstant(String name);
    }

    public static Constraint parseConstraint(String constraintString, CassowaryVariableResolver variableResolver) throws NonlinearExpressionException {

        Matcher matcher = PATTERN.matcher(constraintString);
        matcher.find();
        if (matcher.matches()) {
            Variable variable = variableResolver.resolveVariable(matcher.group(1));
            Expression expression = resolveExpression(matcher.group(3), variableResolver);
            double strength = parseStrength(matcher.group(4));

            String operator = matcher.group(2);
            switch (operator) {
                case "EQ":
                case "==":
                    return Symbolics.equals(expression, variable).setStrength(strength);
                case "GEQ":
                case ">=":
                    return Symbolics.greaterThanOrEqualTo(expression, variable).setStrength(strength);
                case "LEQ":
                case "<=":
                    return Symbolics.lessThanOrEqualTo(expression, variable).setStrength(strength);
            }
        } else {
            throw new RuntimeException("could not parse " +   constraintString);
        }
        throw new RuntimeException("could not parse " +   constraintString);

    }

    private static double parseStrength(String strengthString) {

        double strength =  Strength.REQUIRED;
        if ("!required".equals(strengthString)) {
            strength = Strength.REQUIRED;
        } else if ("!strong".equals(strengthString)) {
            strength = Strength.STRONG;
        } else if ("!medium".equals(strengthString)) {
            strength = Strength.MEDIUM;
        } else if ("!weak".equals(strengthString)) {
            strength = Strength.WEAK;
        }
        return strength;
    }

    public static Expression resolveExpression(String expressionString, CassowaryVariableResolver variableResolver) throws NonlinearExpressionException {

        List<String> postFixExpression = infixToPostfix(tokenizeExpression(expressionString));

        Stack<Expression> expressionStack = new Stack<Expression>();

        for (String expression : postFixExpression) {
            if ("+".equals(expression)) {
                expressionStack.push(Symbolics.add(expressionStack.pop(), (expressionStack.pop())));
            } else if ("-".equals(expression)) {
                Expression a = expressionStack.pop();
                Expression b = expressionStack.pop();

                expressionStack.push(Symbolics.subtract(b, a));
            } else if ("/".equals(expression)) {
                Expression denominator = expressionStack.pop();
                Expression numerator = expressionStack.pop();
                expressionStack.push(Symbolics.divide(numerator, denominator));
            } else if ("*".equals(expression)) {
                expressionStack.push(Symbolics.multiply(expressionStack.pop(), (expressionStack.pop())));
            } else {
                Expression linearExpression =  variableResolver.resolveConstant(expression);
                if (linearExpression == null) {
                    linearExpression = new Expression(new Term(variableResolver.resolveVariable(expression)));
                }
                expressionStack.push(linearExpression);
            }
        }

        return expressionStack.pop();
    }

    public static List<String> infixToPostfix(List<String> tokenList) {

        Stack<Integer> s = new Stack<Integer>();

        List<String> postFix = new ArrayList<String>();
        for (String token : tokenList) {
            char c = token.charAt(0);
            int idx = OPS.indexOf(c);
            if (idx != -1 && token.length() == 1) {
                if (s.isEmpty())
                    s.push(idx);
                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
                            postFix.add(Character.toString(OPS.charAt(s.pop())));
                        else break;
                    }
                    s.push(idx);
                }
            } else if (c == '(') {
                s.push(-2);
            } else if (c == ')') {
                while (s.peek() != -2)
                    postFix.add(Character.toString(OPS.charAt(s.pop())));
                s.pop();
            } else {
                postFix.add(token);
            }
        }
        while (!s.isEmpty())
            postFix.add(Character.toString(OPS.charAt(s.pop())));
        return postFix;
    }

    public static List<String> tokenizeExpression(String expressionString) {
        ArrayList<String> tokenList = new ArrayList<String>();

        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = 0; i < expressionString.length(); i++) {
            char c = expressionString.charAt(i);
            switch (c) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '(':
                case ')':
                    if (stringBuilder.length() > 0) {
                        tokenList.add(stringBuilder.toString());
                        stringBuilder.setLength(0);
                    }
                    tokenList.add(Character.toString(c));
                    break;
                case ' ':
                    // ignore space
                    break;
                default:
                    stringBuilder.append(c);
            }

        }
        if (stringBuilder.length() > 0) {
            tokenList.add(stringBuilder.toString());
        }

        return tokenList;
    }

}

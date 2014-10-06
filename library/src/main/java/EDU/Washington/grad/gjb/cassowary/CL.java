/*
 * Copyright (C) 1988 1999 Greg J. Badros and Alan Borning
 * Copyright (C) 2014 Agens AS
 *
 * Original Smalltalk Implementation by Alan Borning
 * This Java Implementation by Greg J. Badros
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

package EDU.Washington.grad.gjb.cassowary;

/**
 * The enumerations from ClLinearInequality,
 * and `global' functions that we want easy to access
 */
public class CL {
    protected final static boolean fDebugOn = false;
    protected final static boolean fTraceOn = false;
    protected final static boolean fGC = false;

    protected static void debugprint(String s) {
        System.err.println(s);
    }

    protected static void traceprint(String s) {
        System.err.println(s);
    }

    protected static void fnenterprint(String s) {
        System.err.println("* " + s);
    }

    protected static void fnexitprint(String s) {
        System.err.println("- " + s);
    }

    protected void assertion(boolean f, String description)
            throws ExCLInternalError {
        if (!f) {
            throw new ExCLInternalError("Assertion failed:" + description);
        }
    }

    protected void assertion(boolean f) throws ExCLInternalError {
        if (!f) {
            throw new ExCLInternalError("Assertion failed");
        }
    }

    // public static final byte GEQ = 1;
    // public static final byte LEQ = 2;
    public enum Op {
        GEQ, // >=
        LEQ  // <=
    }

    public static ClLinearExpression Plus(ClLinearExpression e1,
            ClLinearExpression e2) {
        return e1.plus(e2);
    }

    public static ClLinearExpression Plus(ClLinearExpression e1, double e2) {
        return e1.plus(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Plus(double e1, ClLinearExpression e2) {
        return (new ClLinearExpression(e1)).plus(e2);
    }

    public static ClLinearExpression Plus(ClVariable e1, ClLinearExpression e2) {
        return (new ClLinearExpression(e1)).plus(e2);
    }

    public static ClLinearExpression Plus(ClLinearExpression e1, ClVariable e2) {
        return e1.plus(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Plus(ClVariable e1, double e2) {
        return (new ClLinearExpression(e1)).plus(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Plus(double e1, ClVariable e2) {
        return (new ClLinearExpression(e1)).plus(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Minus(ClLinearExpression e1,
            ClLinearExpression e2) {
        return e1.minus(e2);
    }

    public static ClLinearExpression Minus(double e1, ClLinearExpression e2) {
        return (new ClLinearExpression(e1)).minus(e2);
    }

    public static ClLinearExpression Minus(ClLinearExpression e1, double e2) {
        return e1.minus(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Times(ClLinearExpression e1,
            ClLinearExpression e2) throws ExCLNonlinearExpression {
        return e1.times(e2);
    }

    public static ClLinearExpression Times(ClLinearExpression e1, ClVariable e2)
            throws ExCLNonlinearExpression {
        return e1.times(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Times(ClVariable e1, ClLinearExpression e2)
            throws ExCLNonlinearExpression {
        return (new ClLinearExpression(e1)).times(e2);
    }

    public static ClLinearExpression Times(ClLinearExpression e1, double e2)
            throws ExCLNonlinearExpression {
        return e1.times(new ClLinearExpression(e2));
    }

    public static ClLinearExpression Times(double e1, ClLinearExpression e2)
            throws ExCLNonlinearExpression {
        return (new ClLinearExpression(e1)).times(e2);
    }

    public static ClLinearExpression Times(double n, ClVariable clv)
            throws ExCLNonlinearExpression {
        return (new ClLinearExpression(clv, n));
    }

    public static ClLinearExpression Times(ClVariable clv, double n)
            throws ExCLNonlinearExpression {
        return (new ClLinearExpression(clv, n));
    }

    public static ClLinearExpression Divide(ClLinearExpression e1,
            ClLinearExpression e2) throws ExCLNonlinearExpression {
        return e1.divide(e2);
    }

    public static boolean approx(double a, double b) {
        double epsilon = 1.0e-8;
        if (a == 0.0) {
            return (Math.abs(b) < epsilon);
        } else if (b == 0.0) {
            return (Math.abs(a) < epsilon);
        } else {
            return (Math.abs(a - b) < Math.abs(a) * epsilon);
        }
    }

    public static boolean approx(ClVariable clv, double b) {
        return approx(clv.value(), b);
    }

    static boolean approx(double a, ClVariable clv) {
        return approx(a, clv.value());
    }
}

package no.agens.cassowarylayout.util;

import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;
import org.klomp.cassowary.clconstraint.ClLinearInequality;

/**
 * Created by alex on 08/10/2014.
 */
public class CassowaryUtil {

    public static ClLinearEquation createWeakEqualityConstraint() {
        return new ClLinearEquation(new ClLinearExpression(null, -1.0, 0), ClStrength.weak);
    }

    public static ClLinearInequality createWeakInequalityConstraint(ClVariable variable, byte op, double value) {
        ClLinearExpression expression = new ClLinearExpression(value);
        return new ClLinearInequality(variable, op, expression, ClStrength.strong);
    }

    public static void updateConstraint(ClLinearConstraint constraint, ClVariable variable, double value) {
        ClLinearExpression expression = constraint.expression();
        expression.setConstant(value);
        expression.setVariable(variable, -1);
    }


    public static ClConstraint createOrUpdateLeqInequalityConstraint(ClVariable variable, ClConstraint constraint, double value, ClSimplexSolver solver) {
        if (constraint != null) {
            double currentValue = constraint.expression().getConstant();
            // This will not detect if the variable or strength has changed.
            if (!(constraint instanceof ClLinearInequality) || currentValue != value) {
                solver.removeConstraint(constraint);
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = new ClLinearInequality(variable, CL.LEQ, value, ClStrength.strong);
            solver.addConstraint(constraint);
        }

        return constraint;
    }

    public static ClConstraint createOrUpdateLinearEquationConstraint(ClVariable variable, ClConstraint constraint, double value, ClSimplexSolver solver) {
        if (constraint != null) {
            double currentValue = constraint.expression().getConstant();
            // This will not detect if the variable, strength or operation has changed
            if (!(constraint instanceof ClLinearEquation) || currentValue != value) {
                solver.removeConstraint(constraint);
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = new ClLinearEquation(variable, value, ClStrength.strong);
            solver.addConstraint(constraint);
        }

        return constraint;
    }
}

package no.agens.cassowarylayout.util;

import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
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
}

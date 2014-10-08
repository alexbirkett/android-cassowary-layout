package no.agens.cassowarylayout.util;

import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;

/**
 * Created by alex on 08/10/2014.
 */
public class CassowaryUtil {
    public static ClLinearEquation createConstraint() {
        return new ClLinearEquation(new ClLinearExpression(null, -1.0, 0), ClStrength.weak);
    }

    public static void updateConstraint(ClLinearEquation constraint, ClVariable variable, double value) {
        ClLinearExpression expression = constraint.expression();
        expression.set_constant(value);
        expression.setVariable(variable, -1);
    }
}

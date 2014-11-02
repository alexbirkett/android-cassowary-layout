package no.agens.cassowarylayout.util;


import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.ConstraintNotFound;
import org.pybee.cassowary.Expression;
import org.pybee.cassowary.SimplexSolver;
import org.pybee.cassowary.Strength;
import org.pybee.cassowary.Variable;

/**
 * Created by alex on 08/10/2014.
 */
public class CassowaryUtil {

    public static Constraint createWeakEqualityConstraint() {
        return new Constraint(new Expression(null, -1.0, 0), Strength.WEAK);
    }

    public static Constraint createWeakInequalityConstraint(Variable variable, Constraint.Operator op, double value) {
        Expression expression = new Expression(value);
        return new Constraint(variable, op, expression, Strength.STRONG);
    }

    public static void updateConstraint(Constraint constraint, Variable variable, double value) {
        Expression expression = constraint.expression();
        expression.set_constant(value);
        expression.setVariable(variable, -1);
    }


    public static Constraint createOrUpdateLeqInequalityConstraint(Variable variable, Constraint constraint, double value, SimplexSolver solver) {
        if (constraint != null) {
            double currentValue = constraint.expression().constant();
            // This will not detect if the variable or strength has changed.
            if (currentValue != value) {
                try {
                    solver.removeConstraint(constraint);
                } catch (ConstraintNotFound constraintNotFound) {
                    constraintNotFound.printStackTrace();
                }
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = new Constraint(variable, Constraint.Operator.LEQ, value, Strength.STRONG);
            solver.addConstraint(constraint);
        }

        return constraint;
    }

    public static Constraint createOrUpdateLinearEquationConstraint(Variable variable, Constraint constraint, double value, SimplexSolver solver) {
        if (constraint != null) {
            double currentValue = constraint.expression().constant();
            // This will not detect if the variable, strength or operation has changed
            if (currentValue != value) {
                try {
                    solver.removeConstraint(constraint);
                } catch (ConstraintNotFound constraintNotFound) {
                    constraintNotFound.printStackTrace();
                }
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = new Constraint(variable, Constraint.Operator.EQ, value, Strength.STRONG);
            solver.addConstraint(constraint);
        }

        return constraint;
    }
}

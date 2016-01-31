package no.agens.cassowarylayout.util;


import no.birkett.kiwi.Constraint;
import no.birkett.kiwi.DuplicateConstraintException;
import no.birkett.kiwi.Expression;
import no.birkett.kiwi.Solver;
import no.birkett.kiwi.Strength;
import no.birkett.kiwi.Symbolics;
import no.birkett.kiwi.UnknownConstraintException;
import no.birkett.kiwi.UnsatisfiableConstraintException;
import no.birkett.kiwi.Variable;

/**
 * Created by alex on 08/10/2014.
 */
public class CassowaryUtil {

    public static Constraint createOrUpdateLeqInequalityConstraint(Variable variable, Constraint constraint, double value, Solver solver) throws DuplicateConstraintException, UnsatisfiableConstraintException {
        if (constraint != null) {
            double currentValue = constraint.getExpression().getConstant();
            // This will not detect if the variable or strength has changed.
            if (currentValue != value) {
                try {
                    solver.removeConstraint(constraint);
                } catch (UnknownConstraintException e) {
                    e.printStackTrace();
                }
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = Symbolics.lessThanOrEqualTo(value, variable);
            solver.addConstraint(constraint);
        }

        return constraint;
    }

    public static Constraint createOrUpdateLinearEquationConstraint(Variable variable, Constraint constraint, double value, Solver solver) throws DuplicateConstraintException, UnsatisfiableConstraintException {
        if (constraint != null) {
            double currentValue = constraint.getExpression().getConstant();
            // This will not detect if the variable, strength or operation has changed
            if (currentValue != value) {
                try {
                    solver.removeConstraint(constraint);
                } catch (UnknownConstraintException constraintNotFound) {
                    constraintNotFound.printStackTrace();
                }
                constraint = null;
            }
        }

        if (constraint == null) {
            constraint = Symbolics.equals(value, variable);
            solver.addConstraint(constraint);
        }

        return constraint;
    }
}

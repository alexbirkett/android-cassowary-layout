package no.agens.cassowarylayout;

import org.pybee.cassowary.SimplexSolver;

import org.pybee.cassowary.Constraint;
import org.pybee.cassowary.Expression;
import org.pybee.cassowary.Strength;
import org.pybee.cassowary.Variable;

/**
 * Created by alex on 02/11/14.
 */
public class ChildNode extends Node {

    public static final String LOW = "low";
    public static final String HIGH = "high";
    public static final String DISTANCE = "distance";
    public static final String CENTER = "center";
    public static final String INTRINSIC_DISTANCE = "intrinsic_distance";

    public ChildNode(SimplexSolver solver) {
        super(solver);
    }

    @Override
    protected void createImplicitConstraints(String variableName, Variable variable) {

        if (HIGH.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getVariable(LOW)).plus(getVariable(DISTANCE)), Strength.REQUIRED));
        } else if (CENTER.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getVariable(DISTANCE)).divide(2).plus(getVariable(LOW)), Strength.REQUIRED));
        }

    }

}
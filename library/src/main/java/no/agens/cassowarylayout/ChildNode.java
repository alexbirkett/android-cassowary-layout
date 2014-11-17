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

    public static final String START = "start";
    public static final String END = "end";
    public static final String LENGTH = "length";
    public static final String CENTER = "center";
    public static final String INTRINSIC_LENGTH = "intrinsic_length";

    public ChildNode(SimplexSolver solver) {
        super(solver);
    }

    @Override
    protected void createImplicitConstraints(String variableName, Variable variable) {

        if (END.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getVariable(START)).plus(getVariable(LENGTH)), Strength.REQUIRED));
        } else if (CENTER.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getVariable(LENGTH)).divide(2).plus(getVariable(START)), Strength.REQUIRED));
        }

    }

}
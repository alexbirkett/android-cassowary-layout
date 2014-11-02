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
    private Node containerNode;

    public ChildNode(SimplexSolver solver, Node containerNode) {
        super(solver);
        this.containerNode = containerNode;
        getRight();
        getBottom();
    }

    @Override
    protected void createImplicitConstraints(String variableName, Variable variable) {

        if (RIGHT.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getLeft()).plus(getWidth()), Strength.REQUIRED));
            solver.addConstraint(new Constraint(variable, Constraint.Operator.LEQ, containerNode.getWidth()));
        } else if (BOTTOM.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getTop()).plus(getHeight()), Strength.REQUIRED));
            solver.addConstraint(new Constraint(variable, Constraint.Operator.LEQ, containerNode.getHeight()));
        } else if (CENTERX.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getWidth()).divide(2).plus(getLeft()), Strength.REQUIRED));
        } else if (CENTERY.equals(variableName)) {
            solver.addConstraint(new Constraint(variable, Constraint.Operator.EQ, new Expression(getHeight()).divide(2).plus(getTop()), Strength.REQUIRED));
        }

    }

}
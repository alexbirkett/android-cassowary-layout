package no.agens.cassowarylayout;

import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClLinearEquation;
import org.klomp.cassowary.clconstraint.ClLinearInequality;

/**
 * Created by alex on 02/11/14.
 */
public class ChildNode extends Node {
    private Node containerNode;

    public ChildNode(ClSimplexSolver solver, Node containerNode) {
        super(solver);
        this.containerNode = containerNode;
        getRight();
        getBottom();
    }

    @Override
    protected void createImplicitConstraints(String variableName, ClVariable variable) {

        if (RIGHT.equals(variableName)) {
            solver.addConstraint(new ClLinearEquation(variable, new ClLinearExpression(getLeft()).plus(getWidth()), ClStrength.required));
            solver.addConstraint(new ClLinearInequality(variable, CL.LEQ, containerNode.getWidth()));
        } else if (BOTTOM.equals(variableName)) {
            solver.addConstraint(new ClLinearEquation(variable, new ClLinearExpression(getTop()).plus(getHeight()), ClStrength.required));
            solver.addConstraint(new ClLinearInequality(variable, CL.LEQ, containerNode.getHeight()));
        } else if (CENTERX.equals(variableName)) {
            solver.addConstraint(new ClLinearEquation(variable, new ClLinearExpression(getWidth()).divide(2).plus(getLeft()), ClStrength.required));
        } else if (CENTERY.equals(variableName)) {
            solver.addConstraint(new ClLinearEquation(variable, new ClLinearExpression(getHeight()).divide(2).plus(getTop()), ClStrength.required));
        }

    }

}
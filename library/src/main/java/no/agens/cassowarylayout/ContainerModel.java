package no.agens.cassowarylayout;

import org.klomp.cassowary.CL;
import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClSimplexSolver;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;
import org.klomp.cassowary.clconstraint.ClLinearInequality;

import no.agens.cassowarylayout.util.CassowaryUtil;

/**
 * Created by alex on 10/10/2014.
 */
public class ContainerModel {

    private ClSimplexSolver solver;

    private ClConstraint containerWidthConstraint;
    private ClConstraint containerHeightConstraint;

    public ContainerModel(ClSimplexSolver solver) {
        this.solver = solver;
    }

    private ClVariable width = new ClVariable();
    private ClVariable height = new ClVariable();

    private ClVariable centerX;
    private ClVariable centerY;

    public ClVariable getHeight() {
        return height;
    }

    public ClVariable getWidth() {
        return width;
    }

    public ClVariable getCenterX() {
        if (centerX == null) {
            centerX = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerX, new ClLinearExpression(getWidth()).divide(2), ClStrength.required));
        }
        return centerX;
    }

    public ClVariable getCenterY() {
        if (centerY == null) {
            centerY = new ClVariable();
            solver.addConstraint(new ClLinearEquation(centerY, new ClLinearExpression(getHeight()).divide(2), ClStrength.required));
        }
        return centerY;
    }

    public void setContainerHeight(int height) {
        containerHeightConstraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getHeight(), containerHeightConstraint, height, solver);
    }

    public void setContainerHeightToAtMost(int height) {
        containerHeightConstraint = CassowaryUtil.createOrUpdateLeqInequalityConstraint(getHeight(), containerHeightConstraint, height, solver);
    }

    public void setContainerWidth(int width) {
        containerWidthConstraint = CassowaryUtil.createOrUpdateLinearEquationConstraint(getWidth(), containerWidthConstraint, width, solver);
    }

    public void setContainerWidthToAtMost(int width) {
        containerWidthConstraint = CassowaryUtil.createOrUpdateLeqInequalityConstraint(getWidth(), containerWidthConstraint, width, solver);
    }


}

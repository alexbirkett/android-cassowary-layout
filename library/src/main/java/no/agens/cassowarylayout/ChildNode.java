package no.agens.cassowarylayout;


import no.birkett.kiwi.Constraint;
import no.birkett.kiwi.DuplicateConstraintException;
import no.birkett.kiwi.Expression;
import no.birkett.kiwi.Solver;
import no.birkett.kiwi.Strength;
import no.birkett.kiwi.Symbolics;
import no.birkett.kiwi.UnsatisfiableConstraintException;
import no.birkett.kiwi.Variable;

/**
 * Created by alex on 02/11/14.
 */
public class ChildNode extends Node {

    public ChildNode(Solver solver) {
        super(solver);
    }

    @Override
    protected void createImplicitConstraints(String variableName, Variable variable) throws DuplicateConstraintException, UnsatisfiableConstraintException {

        if (RIGHT.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.add(getLeft(), getWidth())));
         } else if (BOTTOM.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.add(getTop(), getHeight())));
        } else if (CENTERX.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.add(getLeft(), Symbolics.divide(getWidth(), 2))));
        } else if (CENTERY.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.add(getTop(), Symbolics.divide(getHeight(), 2))));
        }

    }

}
// $Id: ClLinearConstraint.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ClLinearConstraint
//

package EDU.Washington.grad.gjb.cassowary;

class ClLinearConstraint extends ClConstraint {

    public ClLinearConstraint(ClLinearExpression cle, ClStrength strength,
            double weight) {
        super(strength, weight);
        _expression = cle;
    }

    public ClLinearConstraint(ClLinearExpression cle, ClStrength strength) {
        super(strength, 1.0);
        _expression = cle;
    }

    public ClLinearConstraint(ClLinearExpression cle) {
        super(ClStrength.required, 1.0);
        _expression = cle;
    }

    public ClLinearExpression expression() {
        return _expression;
    }

    protected void setExpression(ClLinearExpression expr) {
        _expression = expr;
    }

    protected ClLinearExpression _expression;
}

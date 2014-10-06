// $Id: ExCLInternalError.java,v 1.1 2008/08/12 22:32:42 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ExCLInternalError
//

package EDU.Washington.grad.gjb.cassowary;

public class ExCLInternalError extends ExCLError {
    private static final long serialVersionUID = 1L;

    public ExCLInternalError(String s) {
        description_ = s;
    }

    public String description() {
        return "(ExCLInternalError) " + description_;
    }

    private String description_;
}

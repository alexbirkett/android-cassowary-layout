// $Id: ExCLError.java,v 1.1 2008/08/12 22:32:43 larrymelia Exp $
//
// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// This Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// (C) 1998, 1999 Greg J. Badros and Alan Borning
// See ../LICENSE for legal details regarding this software
//
// ExCLError
//

package EDU.Washington.grad.gjb.cassowary;

public class ExCLError extends Exception {
    private static final long serialVersionUID = 1L;

    public String description() {
        return "(ExCLError) An error has occured in CL";
    }

}

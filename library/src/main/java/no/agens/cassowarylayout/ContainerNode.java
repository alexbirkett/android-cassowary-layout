/*
 * Copyright (C) 2014 Agens AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.agens.cassowarylayout;

import no.birkett.kiwi.DuplicateConstraintException;
import no.birkett.kiwi.Solver;
import no.birkett.kiwi.Symbolics;
import no.birkett.kiwi.UnsatisfiableConstraintException;
import no.birkett.kiwi.Variable;

/**
 * Created by alex on 10/10/2014.
 */
public class ContainerNode extends Node {

    public ContainerNode(Solver solver) {
        super(solver);
    }

    @Override
    protected void createImplicitConstraints(String variableName, Variable variable) throws DuplicateConstraintException, UnsatisfiableConstraintException {

        if (CENTERX.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.divide(getWidth(), 2)));
        } else if (CENTERY.equals(variableName)) {
            solver.addConstraint(Symbolics.equals(variable, Symbolics.divide(getHeight(), 2)));
        }
    }

}

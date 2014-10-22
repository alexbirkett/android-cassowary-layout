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


import junit.framework.TestCase;

import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClVariable;
import org.klomp.cassowary.clconstraint.ClConstraint;

/**
 * Created by alex on 25/09/2014.
 */
public class CassowaryConstraintParserTest extends TestCase {



    public void testExpression() {


        final ClVariable blueX = new ClVariable();
        final ClVariable blueY = new ClVariable();
        final ClVariable blueHeight = new ClVariable();
        final ClVariable blueWidth = new ClVariable();

        final ClVariable greenX = new ClVariable();
        final ClVariable greenY = new ClVariable();
        final ClVariable greenHeight = new ClVariable();
        final ClVariable greenWidth = new ClVariable();

        CassowaryVariableResolver variableResolver = new CassowaryVariableResolver() {
            @Override
            public ClVariable resolveVariable(String variableName) {
                if ("blue.x".equals(variableName)) {
                    return blueX;
                } else if ("blue.y".equals(variableName)) {
                    return blueY;
                } else if ("blue.height".equals(variableName)) {
                    return blueHeight;
                } else if ("blue.width".equals(variableName)) {
                    return blueWidth;
                } else if ("green.x".equals(variableName)) {
                    return greenX;
                } else if ("green.y".equals(variableName)) {
                    return greenY;
                } else if ("blue.height".equals(variableName)) {
                    return greenHeight;
                } else if ("blue.width".equals(variableName)) {
                    return greenWidth;
                }
                return null;
            }

            @Override
            public ClLinearExpression resolveConstant(String name) {
                return null;
            }
        };


        ClConstraint constraint = CassowaryConstraintParser.parseConstraint("green.y <= blue.y !required", variableResolver);


    }
}

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


package no.agens.cassowarylayout.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

import no.agens.cassowarylayout.CassowaryConstraintParser;


/**
 * Created by alex on 26/09/2014.
 */
public class InfixToPostFixTest extends TestCase {


    public void testInfixToPostFix() {

        String[] infix = {"3", "+", "4", "*", "2", "/", "(", "1", "-", "5", ")", "^", "2", "^", "3"};

        List<String> postFix = CassowaryConstraintParser.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "4");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "*");
        assertEquals(postFix.get(index++), "1");
        assertEquals(postFix.get(index++), "5");
        assertEquals(postFix.get(index++), "-");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "^");
        assertEquals(postFix.get(index++), "^");
        assertEquals(postFix.get(index++), "/");
        assertEquals(postFix.get(index++), "+");
    }

    public void testInfixToPostFix1PlusTwoTimes3() {

        String[] infix = {"1", "+", "2", "*", "3" };

        List<String> postFix = CassowaryConstraintParser.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "1");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "*");
        assertEquals(postFix.get(index++), "+");

    }

    public void testInfixToPostFix1PlusTwoTimes3Braces() {

        String[] infix = {"(", "100", "+", "2", ")","*", "3" };

        List<String> postFix = CassowaryConstraintParser.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "100");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "+");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "*");


    }

    public void testRosettaCodeTestCase() {
        String[] infix = {"3", "+", "4", "*", "2","/", "(", "1", "-", "5", ")", "^", "2", "^", "3" };

        List<String> postFix = CassowaryConstraintParser.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "4");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "*");
        assertEquals(postFix.get(index++), "1");
        assertEquals(postFix.get(index++), "5");
        assertEquals(postFix.get(index++), "-");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "^");
        assertEquals(postFix.get(index++), "^");
        assertEquals(postFix.get(index++), "/");
        assertEquals(postFix.get(index++), "+");

    }

}

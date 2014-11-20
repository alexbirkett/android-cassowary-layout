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

import java.util.List;

import no.agens.cassowarylayout.ConstraintParser;
import no.agens.cassowarylayout.ConstraintParser;

/**
 * Created by alex on 25/09/2014.
 */
public class ExpressionTokenizerTest extends TestCase {

    public void testTokenWithSpaces() {
        List<String> tokens = ConstraintParser.tokenizeExpression("( blue.x + blue.w ) - (green.w * green.height) / 2");
        int i = 0;
        assertEquals("(", tokens.get(i++));
        assertEquals("blue.x", tokens.get(i++));
        assertEquals("+", tokens.get(i++));
        assertEquals("blue.w", tokens.get(i++));
        assertEquals(")", tokens.get(i++));
        assertEquals("-", tokens.get(i++));
        assertEquals("(", tokens.get(i++));
        assertEquals("green.w", tokens.get(i++));
        assertEquals("*", tokens.get(i++));
        assertEquals("green.height", tokens.get(i++));
        assertEquals(")", tokens.get(i++));
        assertEquals("/", tokens.get(i++));
        assertEquals("2", tokens.get(i++));
    }

    public void testTokenWithoutSpaces() {
        List<String> tokens = ConstraintParser.tokenizeExpression("(blue.x+blue.w)-(green.w*green.height)/2");
        int i = 0;
        assertEquals("(", tokens.get(i++));
        assertEquals("blue.x", tokens.get(i++));
        assertEquals("+", tokens.get(i++));
        assertEquals("blue.w", tokens.get(i++));
        assertEquals(")", tokens.get(i++));
        assertEquals("-", tokens.get(i++));
        assertEquals("(", tokens.get(i++));
        assertEquals("green.w", tokens.get(i++));
        assertEquals("*", tokens.get(i++));
        assertEquals("green.height", tokens.get(i++));
        assertEquals(")", tokens.get(i++));
        assertEquals("/", tokens.get(i++));
        assertEquals("2", tokens.get(i++));
    }
}

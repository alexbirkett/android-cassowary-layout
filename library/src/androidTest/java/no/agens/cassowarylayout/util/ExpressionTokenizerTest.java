package no.agens.cassowarylayout.util;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by alex on 25/09/2014.
 */
public class ExpressionTokenizerTest extends TestCase {

    public void testTokenWithSpaces() {
        List<String> tokens = ExpressionTokenizer.tokenizeExpression("( blue.x + blue.w ) - (green.w * green.height) / 2");
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
        List<String> tokens = ExpressionTokenizer.tokenizeExpression("(blue.x+blue.w)-(green.w*green.height)/2");
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

package no.agens.cassowarylayout.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;


/**
 * Created by alex on 26/09/2014.
 */
public class InfixToPostFixTest extends TestCase {


    public void testInfixToPostFix() {

        String[] infix = {"3", "+", "4", "*", "2", "/", "(", "1", "-", "5", ")", "^", "2", "^", "3"};

        List<String> postFix = InfixToPostfix.infixToPostfix(Arrays.asList(infix));

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

        List<String> postFix = InfixToPostfix.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "1");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "*");
        assertEquals(postFix.get(index++), "+");

    }

    public void testInfixToPostFix1PlusTwoTimes3Braces() {

        String[] infix = {"(", "100", "+", "2", ")","*", "3" };

        List<String> postFix = InfixToPostfix.infixToPostfix(Arrays.asList(infix));

        int index = 0;

        assertEquals(postFix.get(index++), "100");
        assertEquals(postFix.get(index++), "2");
        assertEquals(postFix.get(index++), "+");
        assertEquals(postFix.get(index++), "3");
        assertEquals(postFix.get(index++), "*");


    }

    public void testRosettaCodeTestCase() {
        String[] infix = {"3", "+", "4", "*", "2","/", "(", "1", "-", "5", ")", "^", "2", "^", "3" };

        List<String> postFix = InfixToPostfix.infixToPostfix(Arrays.asList(infix));

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

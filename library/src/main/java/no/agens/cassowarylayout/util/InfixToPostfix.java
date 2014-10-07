package no.agens.cassowarylayout.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by alex on 26/09/2014.
 * Shunting-yard_algorithm
 * http://rosettacode.org/wiki/Parsing/Shunting-yard_algorithm#Java
 */
public class InfixToPostfix {

    final static String OPS = "-+/*^";

    public static List<String> infixToPostfix(List<String> tokenList) {

        Stack<Integer> s = new Stack<Integer>();

        List<String> postFix = new ArrayList<String>();
        for (String token : tokenList) {
            char c = token.charAt(0);
            int idx = OPS.indexOf(c);
            if (idx != -1 && token.length() == 1) {
                if (s.isEmpty())
                    s.push(idx);
                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
                            postFix.add(Character.toString(OPS.charAt(s.pop())));
                        else break;
                    }
                    s.push(idx);
                }
            } else if (c == '(') {
                s.push(-2);
            } else if (c == ')') {
                while (s.peek() != -2)
                    postFix.add(Character.toString(OPS.charAt(s.pop())));
                s.pop();
            } else {
                postFix.add(token);
            }
        }
        while (!s.isEmpty())
            postFix.add(Character.toString(OPS.charAt(s.pop())));
        return postFix;
    }

}

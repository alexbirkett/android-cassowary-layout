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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 25/09/2014.
 */
public class ExpressionTokenizer {

    public static List<String> tokenizeExpression(String expressionString) {
        ArrayList<String> tokenList = new ArrayList<String>();

        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = 0; i < expressionString.length(); i++) {
            char c = expressionString.charAt(i);
            switch (c) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '(':
                case ')':
                    if (stringBuilder.length() > 0) {
                        tokenList.add(stringBuilder.toString());
                        stringBuilder.setLength(0);
                    }
                    tokenList.add(Character.toString(c));
                    break;
                case ' ':
                    // ignore space
                    break;
                default:
                    stringBuilder.append(c);
            }

        }
        if (stringBuilder.length() > 0) {
            tokenList.add(stringBuilder.toString());
        }

        return tokenList;
    }
}

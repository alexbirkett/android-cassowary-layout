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

import android.test.AndroidTestCase;
import android.util.TypedValue;

/**
 * Created by alex on 09/09/2014.
 */
public class DimensionParserTest extends AndroidTestCase {

    public void testWrapContent() {
        assertEquals(-2D, DimensionParser.getDimension("wrapContent", getContext()));
    }

    public void testMatchParent() {
        assertEquals(-1D, DimensionParser.getDimension("matchParent", getContext()));
    }

    public void test10dp() {
        double expectedResult = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics());
        assertEquals(expectedResult, DimensionParser.getDimension("10dp", getContext()));
    }

    public void test10px() {
        double expectedResult = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10, getContext().getResources().getDisplayMetrics());
        assertEquals(expectedResult, DimensionParser.getDimension("10px", getContext()));
    }

    public void test10sp() {
        double expectedResult = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getContext().getResources().getDisplayMetrics());
        assertEquals(expectedResult, DimensionParser.getDimension("10sp", getContext()));
    }

    public void test10mm() {
        double expectedResult = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 10, getContext().getResources().getDisplayMetrics());
        assertEquals(expectedResult, DimensionParser.getDimension("10mm", getContext()));
    }

}

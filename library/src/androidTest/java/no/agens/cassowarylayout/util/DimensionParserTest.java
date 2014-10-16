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

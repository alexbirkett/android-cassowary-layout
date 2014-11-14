package no.agens.cassowarylayout.util;

import android.view.View;

/**
 * Created by alex on 14/11/14.
 */
public class MeasureSpecUtils {
    public static String getModeAsString(int modeOrSpec) {
        String modeAsString;
        int mode = View.MeasureSpec.getMode(modeOrSpec);
        if (mode == View.MeasureSpec.EXACTLY) {
            modeAsString = "EXACTLY";
        } else if (mode == View.MeasureSpec.AT_MOST) {
            modeAsString = "AT_MOST";
        } else if (mode == View.MeasureSpec.UNSPECIFIED) {
            modeAsString = "UNSPECIFIED";
        } else {
            modeAsString = "unknown mode " + modeOrSpec;
        }

        return modeAsString;
    }
}

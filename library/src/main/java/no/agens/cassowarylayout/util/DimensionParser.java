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

import android.content.Context;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 09/09/2014.
 */
public class DimensionParser {

    private static Pattern pattern = Pattern.compile("^(wrapContent|matchParent)|(\\d+)(px|dp|sp|pt|in|mm)");

    public static Double getDimension(String widthHeightString, Context context) {

        Double widthHeight = null;

        Matcher matcher = pattern.matcher(widthHeightString);

        matcher.find();

        if (matcher.matches()) {
            if  (matcher.group(2) == null) {
                widthHeight = (double)RelativeLayout.LayoutParams.MATCH_PARENT;
                if ("wrapContent".equals(matcher.group(1))) {
                    widthHeight = (double)RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
            } else {
                String value = matcher.group(2);
                String unit = matcher.group(3);
                try {
                    widthHeight = (double)TypedValue.applyDimension(getUnitFromString(unit), Integer.parseInt(value), context.getResources().getDisplayMetrics());
                    //widthHeight = Integer.parseInt(widthHeightString.substring(0, widthHeightString.));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        return widthHeight;

    }

    private static int getUnitFromString(String unitString) {

        int unit = TypedValue.COMPLEX_UNIT_DIP;
        if ("px".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_PX;
        } else if ("dp".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_DIP;
        } else if ("sp".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_SP;
        } else if ("pt".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_PT;
        } else if ("in".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_IN;
        } else if ("mm".equals(unitString)) {
            unit = TypedValue.COMPLEX_UNIT_MM;
        }
        return unit;
    }
}

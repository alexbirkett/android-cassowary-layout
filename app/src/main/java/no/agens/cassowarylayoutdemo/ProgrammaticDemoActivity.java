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

package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayout.ViewIdResolver;

public class ProgrammaticDemoActivity extends Activity {

    private static String[] CONSTRAINTS = {
            "blue.x == 0",
            "blue.y == 0",
            "blue.width == 100dp",
            "blue.height == blue.width",
            "green.width == blue.width",
            "green.height == blue.height",
            "green.x == blue.y2",
            "green.y == blue.y2",
            "red.width == green.width",
            "red.height == green.height",
            "red.x == green.x2",
            "red.y == green.y2"
    };

    private CassowaryLayout cassowaryLayout;

    private View red;
    private View green;
    private View blue;

    private int nextId = 1000;

    private ViewIdResolver viewIdResolver = new ViewIdResolver() {

        @Override
        public int getViewIdByName(String viewName) {
            if ("red".equals(viewName)) {
                return red.getId();
            } else if ("blue".equals(viewName)) {
                return blue.getId();
            } else if ("green".equals(viewName)) {
                return green.getId();
            }
            return 0;
        }

        @Override
        public String getViewNameById(int id) {
           if (id == red.getId()) {
               return "red";
           } else if (id == blue.getId()) {
               return "blue";
           } else if (id == green.getId()) {
               return "green";
           }
           return "";
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cassowaryLayout = new CassowaryLayout(this, viewIdResolver);

        red = new View(this);
        // Each child view *MUST* have a unique ID
        red.setId(nextId++);
        red.setBackgroundColor(getResources().getColor(R.color.red));
        blue = new View(this);
        blue.setId(nextId++);
        blue.setBackgroundColor(getResources().getColor(R.color.blue));
        green = new View(this);
        green.setId(nextId++);
        green.setBackgroundColor(getResources().getColor(R.color.green));

        cassowaryLayout.addView(red);
        cassowaryLayout.addView(green);
        cassowaryLayout.addView(blue);
        cassowaryLayout.getCassowaryModel().addConstraints(CONSTRAINTS);
        setContentView(cassowaryLayout);
    }

}

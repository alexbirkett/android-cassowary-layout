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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayout.Node;

public class DynamicWidth extends Activity {

    private double delta;

    private CassowaryLayout layout;
    private ScrollView scrollView;

    private static final String DRAGGER_POSITION = "draggerPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_width);

        layout = (CassowaryLayout)findViewById(R.id.layout);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);

        findViewById(R.id.dragger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();

                Node draggerNode = layout.getNodeById(v.getId());

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        delta = X - draggerNode.getVariableValue(DRAGGER_POSITION);
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        draggerNode.setVariableToValue(DRAGGER_POSITION, X - delta);
                        layout.requestLayout();
                        break;
                }

                return true;
            }
        });
    }

}

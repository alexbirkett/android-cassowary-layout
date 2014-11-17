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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ExampleMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_menu);
        findViewById(R.id.programmatic_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, ProgrammaticDemo.class));
            }
        });
        findViewById(R.id.xml_layout_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, XmlLayoutDemo.class));
            }
        });

        findViewById(R.id.center_view_relative_to_sibling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CenterViewRelativeToSibling.class));
            }
        });

        findViewById(R.id.center_in_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CenterInParent.class));
            }
        });

        findViewById(R.id.parallax_scrolling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, ParallaxScrolling.class));
            }
        });

        findViewById(R.id.dynamic_width).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, DynamicWidth.class));
            }
        });

        findViewById(R.id.cassowary_layout_match_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CassowaryLayoutMatchParent.class));
            }
        });

        findViewById(R.id.cassowary_layout_wrap_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CassowaryLayoutWrapContent.class));
            }
        });

        findViewById(R.id.cassowary_layout_in_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CassowaryLayoutInLinearLayout.class));
            }
        });

        findViewById(R.id.linear_layout_in_cassowary_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, LinearLayoutInCassowaryLayout.class));
            }
        });

        findViewById(R.id.intrinsic_height_text_in_wrap_content_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, IntrinsicHeightTextInWrapContentContainer.class));
            }
        });

        findViewById(R.id.cassowary_layouts_in_cassowary_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, CassowaryLayoutsInCassowaryLayout.class));
            }
        });

        findViewById(R.id.chess_board).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleMenuActivity.this, ChessBoard.class));
            }
        });

    }

}

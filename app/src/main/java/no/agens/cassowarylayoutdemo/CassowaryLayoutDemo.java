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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CassowaryLayoutDemo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cassowary_layout_demo);
        findViewById(R.id.programmatic_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, ProgrammaticDemo.class));
            }
        });
        findViewById(R.id.xml_layout_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, XmlLayoutDemo.class));
            }
        });

        findViewById(R.id.center_view_relative_to_sibling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, CenterViewRelativeToSibling.class));
            }
        });

        findViewById(R.id.center_in_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, CenterInParent.class));
            }
        });

        findViewById(R.id.dynamic_width).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, DynamicWidth.class));
            }
        });

        findViewById(R.id.cassowary_layout_match_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, CassowaryLayoutMatchParent.class));
            }
        });

        findViewById(R.id.cassowary_layout_wrap_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, CassowaryLayoutWrapContent.class));
            }
        });

        findViewById(R.id.cassowary_layout_in_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, CassowaryLayoutInLinearLayout.class));
            }
        });

        findViewById(R.id.linear_layout_in_cassowary_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CassowaryLayoutDemo.this, LinearLayoutInCassowaryLayout.class));
            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cassowary_layout_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

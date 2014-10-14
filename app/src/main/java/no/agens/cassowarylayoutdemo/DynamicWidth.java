package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.klomp.cassowary.ClLinearExpression;
import org.klomp.cassowary.ClStrength;
import org.klomp.cassowary.clconstraint.ClConstraint;
import org.klomp.cassowary.clconstraint.ClLinearEquation;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayout.ViewModel;
import no.agens.cassowarylayoutdemo.R;

public class DynamicWidth extends Activity {

    private double delta;

    private CassowaryLayout layout;
    private ClConstraint constraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_width);

        layout = (CassowaryLayout)findViewById(R.id.layout);

        findViewById(R.id.dragger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();

                ViewModel viewModel = layout.getViewModelById(v.getId());

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        delta = X - viewModel.getX().getValue();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (constraint != null) {
                            layout.removeConstraint(constraint);
                        }

                        constraint = new ClLinearEquation(viewModel.getX(), new ClLinearExpression(X - delta), ClStrength.strong);
                        layout.addConstraint(constraint);
                        layout.requestLayout();
                        break;
                }

                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dynamic_width, menu);
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

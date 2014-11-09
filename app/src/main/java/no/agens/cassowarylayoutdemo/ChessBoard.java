package no.agens.cassowarylayoutdemo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayout.Node;


public class ChessBoard extends Activity {


    private static final String DRAGGER_POSITION = "draggerPosition";

    private CassowaryLayout layout;
    private double delta;
    private View dragger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        layout = (CassowaryLayout)findViewById(R.id.layout);

        dragger = findViewById(R.id.dragger);

        final Node draggerNode = layout.getNodeById(dragger.getId());


    /*    ValueAnimator animation = ValueAnimator.ofFloat(400f, 500f);
        animation.setDuration(1000);


        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float)animation.getAnimatedValue();
                draggerNode.setVariableToValue(DRAGGER_POSITION, value);
                layout.getCassowaryModel().solve();
                layout.setChildPositionsFromCassowaryModel();
            }
        });

        animation.start();*/

        findViewById(R.id.dragger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();

                Node draggerNode = layout.getNodeById(v.getId());

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        delta = X - draggerNode.getVariableValue(DRAGGER_POSITION);
                        break;
                    case MotionEvent.ACTION_UP:
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

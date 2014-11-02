package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import no.agens.cassowarylayout.CassowaryLayout;


public class ParallaxScrolling extends Activity {

    private static final String LOG_TAG = "ParallaxScrolling";

    private ScrollView scrollView;
    private CassowaryLayout cassowaryLayout;

    private static final String SCROLL_POSITION = "scrollPosition";

    private int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax_scrolling);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        cassowaryLayout = (CassowaryLayout)findViewById(R.id.cassowary_layout);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollY = scrollView.getScrollY(); //for verticalScrollView
                cassowaryLayout.getCassowaryModel().getContainerNode().setVariableToValue(SCROLL_POSITION, getScrollPosition(scrollY)) ;
                cassowaryLayout.getCassowaryModel().solve();
                cassowaryLayout.setChildPositionsFromCassowaryModel();
            }
        });
        cassowaryLayout.getCassowaryModel().getContainerNode().setVariableToValue(SCROLL_POSITION, 0) ;
        screenHeight = getScreenHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parallax_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }

    private double getScrollPosition(int scrollY) {
        double scrollPosition =  (double)scrollY / (double)(cassowaryLayout.getHeight() - screenHeight);
        Log.d(LOG_TAG, "scroll position " + scrollPosition);
        return scrollPosition;
    }

}

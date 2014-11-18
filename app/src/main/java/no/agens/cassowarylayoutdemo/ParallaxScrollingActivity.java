package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayout.Node;


public class ParallaxScrollingActivity extends Activity {

    private static final String LOG_TAG = "ParallaxScrolling";

    private ScrollView scrollView;
    private CassowaryLayout cassowaryLayout;

    private static final String SCROLL_POSITION = "scrollPosition";
    private static final String SCREEN_WIDTH = "screenWidth";
    private static final String SCREEN_HEIGHT = "screenHeight";
    private static final String SCROLL_Y = "scrollY";

    private int screenHeight;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax_scrolling);
        setScreenHeightAndWidth();
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        cassowaryLayout = (CassowaryLayout)findViewById(R.id.cassowary_layout);

        cassowaryLayout.addSetupCallback(new CassowaryLayout.CassowaryLayoutSetupCallback() {
            @Override
            public void onCassowaryLayoutSetupComplete(CassowaryLayout layout) {
                final Node containerNode = cassowaryLayout.getCassowaryModel().getContainerNode();

                scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                    @Override
                    public void onScrollChanged() {

                        int scrollY = scrollView.getScrollY();
                        containerNode.setVariableToValue(SCROLL_POSITION, getScrollPosition(scrollY));
                        containerNode.setVariableToValue(SCROLL_Y, scrollY);
                        cassowaryLayout.getCassowaryModel().solve();
                        cassowaryLayout.setChildPositionsFromCassowaryModel();
                    }
                });
                containerNode.setVariableToValue(SCROLL_POSITION, 0);
                containerNode.setVariableToValue(SCROLL_Y, 0);
                containerNode.setVariableToValue(SCREEN_HEIGHT, screenHeight);
                containerNode.setVariableToValue(SCREEN_WIDTH, screenWidth);
            }
        });

    }

    private void setScreenHeightAndWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenHeight = point.y;
        screenWidth = point.x;
    }

    private double getScrollPosition(int scrollY) {
        double scrollPosition =  (double)scrollY / (double)(cassowaryLayout.getHeight() - screenHeight);
        Log.d(LOG_TAG, "scroll position " + scrollPosition);
        return scrollPosition;
    }

}

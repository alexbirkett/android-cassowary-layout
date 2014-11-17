package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class CassowaryLayoutsInListViewActivity extends Activity {

    private static final int ITEM_COUNT = 300;
    private static final int ITEM_TYPE_COUNT = 2;
    private static final String LOG_TAG = "CassowaryLayoutsInListViewActivity";

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return new Object();
        }

        @Override
        public int getItemViewType (int position) {
            return getIdFromPosition(position);
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_TYPE_COUNT;
        }

        @Override
        public long getItemId(int position) {
            return getIdFromPosition(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(getLayoutIdPosition(position), parent, false);
            }
            return view;
        }

        private int getIdFromPosition(int position) {
            int id = (position % ITEM_TYPE_COUNT);

            Log.d(LOG_TAG, "position " + position + " id " + id);
            return id;
        }

        private int getLayoutIdPosition(int position) {
            int id = getIdFromPosition(position);
            switch (id) {
                case 0:
                    return R.layout.item_stairs;
                case 1:
                    return R.layout.item_squares;
                default:
                    throw new RuntimeException("unknown id");
            }
        }
    }

    private Adapter adapter = new Adapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cassowary_layouts_in_list_view);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

}

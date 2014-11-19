package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * Created by alex on 19/11/2014.
 */
public abstract class LayoutParamsSwitcherActivity extends Activity {


    private View contentView;

    private RadioGroup.OnCheckedChangeListener buttonListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.width_wrap_content:
                    contentView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    break;
                case R.id.width_match_parent:
                    contentView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    break;
                case R.id.height_wrap_content:
                    contentView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    break;
                case R.id.height_match_parent:
                    contentView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    break;
            }
            contentView.requestLayout();
        }
    };

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater li = LayoutInflater.from(this);
        setContentView(li.inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View contentView) {
        this.contentView = contentView;
        LayoutInflater li = LayoutInflater.from(this);

        ViewGroup layoutParamsSwitcher = (ViewGroup)li.inflate(R.layout.activity_layout_params_switcher, null);
        layoutParamsSwitcher.addView(contentView);

        super.setContentView(layoutParamsSwitcher);
        addListeners();

        contentView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        contentView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private void addListeners() {
        ((RadioGroup) findViewById(R.id.layout_width_radio_group)).setOnCheckedChangeListener(buttonListener);
        ((RadioGroup) findViewById(R.id.layout_height_radio_group)).setOnCheckedChangeListener(buttonListener);
    }

}

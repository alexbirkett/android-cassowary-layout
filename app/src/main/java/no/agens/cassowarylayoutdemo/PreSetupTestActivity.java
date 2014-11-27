package no.agens.cassowarylayoutdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import no.agens.cassowarylayout.CassowaryLayout;


public class PreSetupTestActivity extends LayoutParamsSwitcherActivity {


    private CassowaryLayout cassowaryLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create a cassowary layout that is not setup i.e. it does not have a cassowary model
        cassowaryLayout = new CassowaryLayout(this);
        cassowaryLayout.setBackgroundColor(getResources().getColor(R.color.red));
        setContentView(cassowaryLayout);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_setup_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.set_ratio) {
            editRatio();
        }

        return super.onOptionsItemSelected(item);
    }

    private void editRatio() {

        // quick and dirty dialog to set ratio
        LayoutInflater li = LayoutInflater.from(this);
        final EditText input = (EditText) li.inflate(R.layout.ratio_edit_text, null);
        input.setText(cassowaryLayout.getPreSetupWidthHeightRatio() + "");

        new AlertDialog.Builder(PreSetupTestActivity.this)
                .setTitle(R.string.enter_ratio)
                .setView(input)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        float ratio = Float.parseFloat(value);
                        cassowaryLayout.setPreSetupWidthHeightRatio(ratio);

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }
}

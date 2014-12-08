package no.agens.cassowarylayoutdemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import no.agens.cassowarylayout.CassowaryLayout;
import no.agens.cassowarylayoutdemo.util.EditRatioDialog;

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
            EditRatioDialog.show(cassowaryLayout.getAspectRatioWidthFactor(),
                    cassowaryLayout.getAspectRatioHeightFactor(), this, new EditRatioDialog.EditRatioDialogCallback() {
                        @Override
                        public void setAspectRatioWidthFactor(float aspectRatioWidthFactor) {
                            cassowaryLayout.setAspectRatioWidthFactor(aspectRatioWidthFactor);
                        }

                        @Override
                        public void setAspectRatioHeightFactor(float aspectRatioHeightFactor) {
                            cassowaryLayout.setAspectRatioHeightFactor(aspectRatioHeightFactor);
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }
}

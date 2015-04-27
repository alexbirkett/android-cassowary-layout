package no.agens.cassowarylayoutdemo.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import no.agens.cassowarylayoutdemo.R;

/**
 * Created by alex on 08/12/2014.
 */
public class EditRatioDialog {

    private EditRatioDialog() {
    }

    public interface EditRatioDialogCallback {
        void setAspectRatioWidthFactor(float aspectRatioWidthFactor);
        void setAspectRatioHeightFactor(float aspectRatioHeightFactor);
    }


    public static void show(float aspectRatioWidthFactor,
                            float aspectRatioHeightFactor,
                            final Context context,
                            final EditRatioDialogCallback callback) {

        // quick and dirty dialog to set ratio
        LayoutInflater li = LayoutInflater.from(context);

        @SuppressLint("InflateParams")
        ViewGroup dialogAspectRatioEdit = (ViewGroup)li.inflate(R.layout.dialog_aspect_ratio_edit, null);
        final EditText aspectRatioWidthFactorEditText = (EditText)  dialogAspectRatioEdit.findViewById(R.id.aspectRatioWidthFactor);
        final EditText aspectRatioHeightFactorEditText = (EditText)  dialogAspectRatioEdit.findViewById(R.id.aspectRatioHeightFactor);

        aspectRatioWidthFactorEditText.setText(Float.toString(aspectRatioWidthFactor));
        aspectRatioHeightFactorEditText.setText(Float.toString(aspectRatioHeightFactor));

        new AlertDialog.Builder(context)
                .setTitle(R.string.enter_ratio)
                .setView(dialogAspectRatioEdit)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        callback.setAspectRatioWidthFactor(getValueFromEditText(aspectRatioWidthFactorEditText));
                        callback.setAspectRatioHeightFactor(getValueFromEditText(aspectRatioHeightFactorEditText));
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    private static float getValueFromEditText(EditText editText) {
        return Float.parseFloat(editText.getText().toString());
    }
}

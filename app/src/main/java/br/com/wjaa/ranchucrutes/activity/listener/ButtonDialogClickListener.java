package br.com.wjaa.ranchucrutes.activity.listener;

import android.content.DialogInterface;

import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;

/**
 * Created by wagner on 27/07/15.
 */
public class ButtonDialogClickListener implements DialogInterface.OnClickListener {

    private DialogCallback callback;

    public ButtonDialogClickListener(DialogCallback callback){
        this.callback = callback;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                dialog.cancel();
                callback.confirm();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                callback.cancel();
                break;

            default:
                break;
        }

    }

}
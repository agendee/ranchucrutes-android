package br.com.wjaa.ranchucrutes.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.callback.DialogCallback;
import br.com.wjaa.ranchucrutes.activity.listener.ButtonDialogClickListener;

/**
 * Created by wagner on 27/07/15.
 */
public class AndroidUtils {


    private static ProgressDialog dialog;

    public static Intent openActivity(Activity context, Class<?> activity ){
        Intent i = new Intent(context, activity);
        context.startActivityForResult(i, 1);
        return i;
    }


    public static void showMessageDlg(String title, String msg, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView tv = (TextView) v.findViewById(R.id.titleDefault);
        tv.setText(title);
        alertDialog.setCustomTitle(v);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg +"</font>"));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public static void showMessageDlg(String title, String msg, Context context, final DialogCallback dialogCallback){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView tv = (TextView) v.findViewById(R.id.titleDefault);
        tv.setText(title);
        alertDialog.setCustomTitle(v);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg +"</font>"));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialogCallback.confirm();
            }
        });
        alertDialog.show();
    }

    public static void showConfirmDlg(String title, String msg, Context context, DialogCallback callback ){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView tv = (TextView) v.findViewById(R.id.titleDefault);
        tv.setText(title);
        alertDialog.setCustomTitle(v);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg + "</font>"));
        DialogInterface.OnClickListener listener = new ButtonDialogClickListener(callback);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", listener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", listener);
        alertDialog.show();
    }



    public static ProgressDialog showWaitDlg(String msg, Context context){
        if (dialog == null){
            dialog = new ProgressDialog(context);
        }
        if (dialog.isShowing()){
            closeWaitDlg();
        }
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView tv = (TextView) v.findViewById(R.id.titleDefault);
        tv.setText("Aguarde!");
        dialog.setCustomTitle(v);

        dialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg + "</font>"));
        dialog.show();
        return dialog;
    }

    public static void closeWaitDlg(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }


    public static void showMessageDlgOnUiThread(final String titulo, final String msg, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showMessageDlg(titulo,msg,activity);
            }
        });
    }

    public static void showMessageDlgOnUiThread(final String titulo, final String msg, final Activity activity, final DialogCallback dialogCallback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showMessageDlg(titulo,msg,activity, dialogCallback);
            }
        });
    }

    public static void showWaitDlgOnUiThread(final String s, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showWaitDlg(s,activity);
            }
        });
    }
}

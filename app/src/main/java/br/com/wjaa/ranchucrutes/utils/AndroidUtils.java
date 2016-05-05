package br.com.wjaa.ranchucrutes.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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



    public enum AlertType{
        ALERT_ERRO(R.id.titleErro, R.layout.custom_title_error),
        ALERT_SUCESSO(R.id.titleSucesso,R.layout.custom_title_success),
        ALERT_WARNING(R.id.titleWarning,R.layout.custom_title_warning);
        private int resource;
        private int layout;
        AlertType(int resource, int layout){
            this.resource = resource;
            this.layout = layout;
        }

        public int getLayout() {
            return layout;
        }

        public int getResource() {
            return resource;
        }
    }

    public static Intent openActivity(Activity context, Class<?> activity ){
        Intent i = new Intent(context, activity);

        context.startActivityForResult(i, 1);
        return i;
    }

    public static Intent openActivity(Activity context, Class<?> activity, int requestCode  ){
        Intent i = new Intent(context, activity);

        context.startActivityForResult(i, requestCode);
        return i;
    }

    public static Intent openActivity(Activity context, Class<?> activity, Bundle bundle ){
        Intent i = new Intent(context, activity);
        i.putExtras(bundle);
        context.startActivityForResult(i, 1);
        return i;
    }

    public static Intent openActivityFromFragment(Fragment fragment, Class<?> activity, Bundle bundle ){
        FragmentActivity context = fragment.getActivity();
        Intent i = new Intent(context, activity);
        if (bundle != null){
            i.putExtras(bundle);
        }
        context.startActivityFromFragment(fragment, i, 1);
        return i;
    }

    public static Intent openActivityFromFragment(Fragment fragment, Class<?> activity){
        return openActivityFromFragment(fragment,activity,null);
    }


    public static void showMessageDlg(String title, String msg, Context context, AlertType alertType){
        showMessageDlg(title, msg, context, alertType, null);
    }

    public static void showMessageDlg(String title, String msg, Context context, AlertType alertType, final DialogCallback dialogCallback){
        if ( alertType == null ){
            alertType = AlertType.ALERT_SUCESSO;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View v = ((Activity)context).getLayoutInflater().inflate(alertType.getLayout(), null);
        TextView tv = (TextView) v.findViewById(alertType.getResource());
        tv.setText(Html.fromHtml("<font color='#FFFFFF'>" + title + "</font>"));

        alertDialog.setCustomTitle(v);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg + "</font>"));


        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (dialogCallback != null) {
                    dialogCallback.confirm();
                }
            }
        });

        alertDialog.show();
    }

    public static void showConfirmDlg(String title, String msg, Context context, DialogCallback callback ){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View v = ((Activity)context).getLayoutInflater().inflate(AlertType.ALERT_SUCESSO.getLayout(), null);
        TextView tv = (TextView) v.findViewById(AlertType.ALERT_SUCESSO.getResource());
        tv.setText(title);
        tv.setText(Html.fromHtml("<font color='#FFFFFF'>" + title + "</font>"));
        alertDialog.setCustomTitle(v);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg + "</font>"));
        DialogInterface.OnClickListener listener = new ButtonDialogClickListener(callback);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", listener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", listener);
        alertDialog.show();
    }

    public static void showConfirmDlgOnUiThread(final String title, final String msg,
                                                final Activity context, final DialogCallback callback ){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConfirmDlg(title, msg, context, callback);
            }
        });
    }



    public static ProgressDialog showWaitDlg(String msg, Context context){
        if (dialog == null || !dialog.getContext().equals(context)){
            dialog = new ProgressDialog(context);
        }
        if (dialog.isShowing()){
            closeWaitDlg();
        }
        View v = ((Activity)context).getLayoutInflater().inflate(AlertType.ALERT_SUCESSO.getLayout(), null);
        TextView tv = (TextView) v.findViewById(AlertType.ALERT_SUCESSO.getResource());
        tv.setText(Html.fromHtml("<font color='#FFFFFF'>Aguarde!</font>"));
        dialog.setCustomTitle(v);

        dialog.setMessage(Html.fromHtml("<font color='#000000'>" + msg + "</font>"));

        //caso a activity nao estiver mais viva
        if (!((Activity) context).isFinishing()){
            dialog.show();
        }
        return dialog;
    }

    public static void closeWaitDlg(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }


    public static void showMessageDlgOnUiThread(final String titulo, final String msg, final Activity activity,
                                                final AlertType alertType) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showMessageDlg(titulo, msg, activity, alertType);
            }
        });
    }

    public static void showMessageDlgOnUiThread(final String titulo, final String msg, final Activity activity,
                                                final AlertType alertType, final DialogCallback dialogCallback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showMessageDlg(titulo,msg,activity, alertType, dialogCallback);
            }
        });
    }

    public static void showWaitDlgOnUiThread(final String s, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.showWaitDlg(s, activity);
            }
        });
    }

    public static void showMessageSuccessDlg(String msg, Context context){
        showMessageDlg(context.getResources().getString(R.string.titulo_sucesso), msg, context, AlertType.ALERT_SUCESSO);
    }
    public static void showMessageErroDlg(String msg, Context context){
        showMessageDlg(context.getResources().getString(R.string.titulo_erro), msg, context, AlertType.ALERT_ERRO);
    }

    public static void showMessageErroDlg(String msg, Activity context, DialogCallback dialogCallback) {
        showMessageDlg(context.getResources().getString(R.string.titulo_erro), msg, context, AlertType.ALERT_ERRO, dialogCallback);
    }

    public static void showMessageWarningDlg(String msg, Context context){
        showMessageDlg(context.getResources().getString(R.string.titulo_warning), msg, context, AlertType.ALERT_WARNING);
    }

    public static void showMessageSuccessDlgOnUiThread(String msg, Activity context){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_sucesso), msg,
                context, AlertType.ALERT_SUCESSO);
    }
    public static void showMessageErroDlgOnUiThread(String msg, Activity context){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_erro), msg,
                context, AlertType.ALERT_ERRO);
    }
    public static void showMessageWarningDlgOnUiThread(String msg, Activity context){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_warning), msg,
                context, AlertType.ALERT_WARNING);
    }
    public static void showMessageSuccessDlgOnUiThread(String msg, Activity context, DialogCallback dialogCallback){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_sucesso), msg,
                context, AlertType.ALERT_SUCESSO, dialogCallback);
    }
    public static void showMessageErroDlgOnUiThread(String msg, Activity context, DialogCallback dialogCallback){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_erro), msg,
                context, AlertType.ALERT_ERRO, dialogCallback);
    }
    public static void showMessageWarningDlgOnUiThread(String msg, Activity context, DialogCallback dialogCallback){
        showMessageDlgOnUiThread(context.getResources().getString(R.string.titulo_warning), msg,
                context, AlertType.ALERT_WARNING, dialogCallback);
    }

    public static void snackAlert(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static boolean internetNotActive(Context c) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            return false;
        }
        return true;
    }

    public static boolean internetActive(Context c) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}

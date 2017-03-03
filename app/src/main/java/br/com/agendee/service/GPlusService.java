package br.com.agendee.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;


/**
 * Created by wagner on 25/09/15.
 */
public interface GPlusService extends View.OnClickListener {

    void onCreate(Activity context);
    void onStart() ;

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onStop();
}

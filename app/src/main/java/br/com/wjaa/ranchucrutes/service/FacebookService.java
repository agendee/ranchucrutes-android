package br.com.wjaa.ranchucrutes.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by wagner on 10/09/15.
 */
public interface FacebookService {

    void onCreate(Fragment frament);
    void onResume();
    void onStop();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onViewCreated(View view, Bundle savedInstanceState);

}

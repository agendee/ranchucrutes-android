package br.com.wjaa.ranchucrutes.wrapper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;

/**
 * Created by wagner on 08/09/15.
 */
@SuppressLint("ValidFragment")
public class NativeFragmentWrapper extends android.support.v4.app.Fragment {

    private final Fragment nativeFragment;

    public NativeFragmentWrapper(Fragment nativeFragment) {
        super();
        this.nativeFragment = nativeFragment;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        nativeFragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        nativeFragment.onActivityResult(requestCode, resultCode, data);
    }
}

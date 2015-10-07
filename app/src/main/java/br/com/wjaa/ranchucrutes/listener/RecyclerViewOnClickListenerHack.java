package br.com.wjaa.ranchucrutes.listener;

import android.view.View;

/**
 * Created by wagner on 05/10/15.
 */
public interface RecyclerViewOnClickListenerHack {
    void onClickListener(View view, int position);
    void onLongPressClickListener(View view, int position);
}

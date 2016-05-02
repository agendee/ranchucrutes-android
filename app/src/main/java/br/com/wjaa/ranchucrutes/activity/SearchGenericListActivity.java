package br.com.wjaa.ranchucrutes.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.StringUtils;

/**
 * Created by wagner on 15/03/16.
 */
public class SearchGenericListActivity extends SearchListActivity {

    public void filter( String q ){
        mListFilter.clear();
        if (StringUtils.isBlank(q)){
            cloneList(mList, mListFilter);

            adapter.notifyDataSetChanged();
            return;
        }

        for( int i = 0, tamI = mList.size(); i < tamI; i++ ){
            if(StringUtils.isNotBlank(mList.get(i).getName()) && mList.get(i).getName().toLowerCase().contains( q.toLowerCase() ) ){
                mListFilter.add( mList.get(i) );
            }
        }

        mRecyclerView.setVisibility(mListFilter.isEmpty() ? View.GONE : View.VISIBLE);
        if( mListFilter.isEmpty() ){

            if (clContainer.findViewById(1) == null){
                TextView tv = new TextView( this );
                tv.setText( "Nenhum resultado encontrado." );
                tv.setTextColor( getResources().getColor( android.R.color.black) );
                tv.setId(1);
                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                tv.setGravity(Gravity.CENTER);

                clContainer.addView(tv);
            }
        }
        else if( clContainer.findViewById(1) != null ) {
            clContainer.removeView( clContainer.findViewById(1) );
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected CharSequence getQueryHint() {
        return "Pesquise aqui";
    }

}

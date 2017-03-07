package br.com.agendee.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import br.com.agendee.R;
import br.com.agendee.utils.StringUtils;
import br.com.agendee.view.SearchingListModel;

/**
 * Created by wagner on 15/03/16.
 */
public class SearchGenericListActivity extends SearchListActivity {

    @Override
    protected List<SearchingListModel> getListCache() {
        return null;
    }
    private final int idFake = 1;
    public void filter(String q ){
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

            if (clContainer.findViewById(idFake) == null){
                TextView tv = new TextView( this );
                tv.setText( "Nenhum resultado encontrado." );
                tv.setTextColor( getResources().getColor( android.R.color.black) );
                tv.setId(idFake);
                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                tv.setGravity(Gravity.CENTER);

                clContainer.addView(tv);
            }
        }
        else if( clContainer.findViewById(idFake) != null ) {
            clContainer.removeView( clContainer.findViewById(idFake) );
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected boolean isExpandActionView() {
        return false;
    }

    @Override
    protected CharSequence getQueryHint() {
        return this.queryText;
    }

}

package br.com.wjaa.ranchucrutes.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.wjaa.ranchucrutes.fragment.ProfissionalAgendaFragment;

/**
 * Created by viniciusthiengo on 5/18/15.
 */
public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles = {"01/01/2015", "02/01/2015", "03/01/2015", "04/01/2015", "05/01/2015"};


    public TabsAdapter(FragmentManager fm, Context c) {
        super(fm);

        mContext = c;
        double scale = c.getResources().getDisplayMetrics().density;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if(position == 0){ // ALL CARS
            frag = new ProfissionalAgendaFragment();
        }
        else if(position == 1){ // LUXURY CAR
            frag = new ProfissionalAgendaFragment();
        }
        else if(position == 2){ // SPORT CAR
            frag = new ProfissionalAgendaFragment();
        }
        else if(position == 3){ // OLD CAR
            frag = new ProfissionalAgendaFragment();
        }
        else if(position == 4){ // POPULAR CAR
            frag = new ProfissionalAgendaFragment();
        }

        Bundle b = new Bundle();
        b.putInt("position", position);

        frag.setArguments(b);

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        /*Drawable d = mContext.getResources().getDrawable( icons[position] );
        d.setBounds(0, 0, heightIcon, heightIcon);

        ImageSpan is = new ImageSpan( d );

        SpannableString sp = new SpannableString(" ");
        sp.setSpan( is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );


        return ( sp );*/
        return ( titles[position] );
    }
}

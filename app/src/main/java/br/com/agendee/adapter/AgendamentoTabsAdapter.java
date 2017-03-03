package br.com.agendee.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.agendee.fragment.ProfissionalAgendaFragment;

/**
 * Created by viniciusthiengo on 5/18/15.
 */
public class AgendamentoTabsAdapter extends FragmentPagerAdapter {
    private List<ProfissionalAgendaFragment> profissionalAgendaFragments = new ArrayList<>();
    private Context context;

    public AgendamentoTabsAdapter(FragmentManager fm, List<ProfissionalAgendaFragment> profissionalAgendaFragments, Activity context) {
        super(fm);
        this.profissionalAgendaFragments = profissionalAgendaFragments;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = profissionalAgendaFragments.get(position);
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return profissionalAgendaFragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title = profissionalAgendaFragments.get(position).getTitle();
      /*
        Drawable d = context.getResources().getDrawable(android.R.drawable.ic_media_play);
        d.setBounds(0, 0, 40, 40);

        ImageSpan is = new ImageSpan( d );

        SpannableString sp = new SpannableString(" ");
        sp.setSpan( is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );*/


        return "   " + title + "   ";

        /*

        if (position == 0){
            title += "    ->";
        }else if (position == profissionalAgendaFragments.size() -1){
            title = "<-    " + title;
        }else{
            title = "<-    " + title + "    ->";
        }
        return title;*/
    }



}

package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.fragment.ProfissionalAgendaFragment;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.RoboGuice;

/**
 * Created by viniciusthiengo on 5/18/15.
 */
public class AgendamentoTabsAdapter extends FragmentPagerAdapter {
    private List<ProfissionalAgendaFragment> profissionalAgendaFragments = new ArrayList<>();

    public AgendamentoTabsAdapter(FragmentManager fm, List<ProfissionalAgendaFragment> profissionalAgendaFragments) {
        super(fm);
        this.profissionalAgendaFragments = profissionalAgendaFragments;
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
        /*Drawable d = mContext.getResources().getDrawable( icons[position] );
        d.setBounds(0, 0, heightIcon, heightIcon);

        ImageSpan is = new ImageSpan( d );

        SpannableString sp = new SpannableString(" ");
        sp.setSpan( is, 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );


        return ( sp );*/
        return ( profissionalAgendaFragments.get(position).getTitle());
    }



}

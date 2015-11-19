package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.AgendamentoActivity;
import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.fragment.ProfissionalAgendaFragment;
import br.com.wjaa.ranchucrutes.service.AgendamentoService;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.RoboGuice;
import roboguice.inject.ContentView;

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

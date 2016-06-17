package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.ClinicaVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;

/**
 * Created by wagner on 6/14/16.
 */
public class ProfissionalListAdapter extends ArrayAdapter<ProfissionalBasicoVo> {


    private List<ProfissionalBasicoVo> profissionais;
    private ClinicaVo clinicaVo;
    private Context context;

    public ProfissionalListAdapter(List<ProfissionalBasicoVo> profissionais, ClinicaVo clinicaVo, Activity context) {
        super(context, -1, profissionais);
        this.profissionais = profissionais;
        this.clinicaVo = clinicaVo;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ProfissionalBasicoVo p = profissionais.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_default, parent, false);

        TextView titulo = (TextView) rowView.findViewById(R.id.textItemList);
        TextView subTitulo = (TextView) rowView.findViewById(R.id.textSubItemList);
        TextView sub2Titulo = (TextView) rowView.findViewById(R.id.textSub2ItemList);

        titulo.setText(p.getNome());
        if (StringUtils.isNotBlank(clinicaVo.getNome())){
            subTitulo.setText(clinicaVo.getNome());
            sub2Titulo.setText(p.getEspec());
            sub2Titulo.setVisibility(View.VISIBLE);
        }else{
            subTitulo.setText(p.getEspec());
            sub2Titulo.setVisibility(View.GONE);
        }
        return rowView;
    }
}

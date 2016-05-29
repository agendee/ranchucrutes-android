package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.DetalhesConsultaActivity;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;

/**
 * Created by wagner on 05/11/15.
 */
public class ConveniosSelectedListAdapter extends ArrayAdapter<ConvenioCategoriaVo> {

    private List<ConvenioCategoriaVo> convenios;
    private Context context;

    public ConveniosSelectedListAdapter(Context context, List<ConvenioCategoriaVo> convenios) {
        super(context, -1, convenios);
        this.convenios = convenios;
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ConvenioCategoriaVo agendamentoVo = convenios.get(position);
       LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_removable, parent, false);

        TextView titulo = (TextView) rowView.findViewById(R.id.textItemList);
        TextView subTitulo = (TextView) rowView.findViewById(R.id.textSubItemList);
        ImageView imgRemove = (ImageView) rowView.findViewById(R.id.imgRemoveItem);
        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(agendamentoVo);
            }
        });

        titulo.setText(agendamentoVo.getNome());
        if ( agendamentoVo.getConvenioVo() != null ){
            subTitulo.setText(agendamentoVo.getConvenioVo().getNome());
        }else{
            subTitulo.setText("");
        }
        return rowView;
    }
}

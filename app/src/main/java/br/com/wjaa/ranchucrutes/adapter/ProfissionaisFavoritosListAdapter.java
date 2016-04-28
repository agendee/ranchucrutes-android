package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.activity.DetalhesConsultaActivity;
import br.com.wjaa.ranchucrutes.entity.ProfissionalFavoritoEntity;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;

/**
 * Created by wagner on 4/20/16.
 */
public class ProfissionaisFavoritosListAdapter extends ArrayAdapter<ProfissionalFavoritoEntity> {

    private List<ProfissionalFavoritoEntity> profissionaisFavoritos;
    private Context context;

    public ProfissionaisFavoritosListAdapter(List<ProfissionalFavoritoEntity> profissionaisFavoritos, FragmentActivity context) {
        super(context, -1, profissionaisFavoritos);
        this.profissionaisFavoritos = profissionaisFavoritos;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ProfissionalFavoritoEntity pdf = profissionaisFavoritos.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_default, parent, false);

        TextView titulo = (TextView) rowView.findViewById(R.id.textItemList);
        TextView subTitulo = (TextView) rowView.findViewById(R.id.textSubItemList);
        titulo.setText(pdf.getNome());
        subTitulo.setText(pdf.getEspec());
        return rowView;
    }


}

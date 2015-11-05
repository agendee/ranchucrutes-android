package br.com.wjaa.ranchucrutes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.DateUtils;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;

/**
 * Created by wagner on 05/11/15.
 */
public class MinhasConsultasListAdapter extends ArrayAdapter<AgendamentoVo> {

    private AgendamentoVo[] objects;
    private Context context;

    public MinhasConsultasListAdapter(Context context, AgendamentoVo[] objects) {
        super(context, -1, objects);
        this.objects = objects;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AgendamentoVo agendamentoVo = objects[position];
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.minhas_consultas_item_list, parent, false);

        ImageView icStep1 = (ImageView) rowView.findViewById(R.id.icStep1);
        ImageView icStep2 = (ImageView) rowView.findViewById(R.id.icStep2);
        ImageView icStep3 = (ImageView) rowView.findViewById(R.id.icStep3);
        String statusConsulta = "Essa consulta está aguardando sua confirmação.";
        if (agendamentoVo.getCancelado()){
            icStep1.setImageResource(R.drawable.ic_cancel_red_36dp);
            icStep2.setVisibility(View.INVISIBLE);
            icStep3.setVisibility(View.INVISIBLE);
            statusConsulta = "Essa consulta foi cancelada!";
        }else{
            if (agendamentoVo.getDataConfirmacao() != null){
                icStep1.setImageResource(R.drawable.ic_panorama_fish_eye_green_24dp);
                statusConsulta = "Essa consulta está aguardando a confirmação do profissional.";
            }
            if (agendamentoVo.getDataConfirmacaoProfissional() != null){
                icStep2.setImageResource(R.drawable.ic_panorama_fish_eye_green_24dp);
                statusConsulta = "Consulta agendada.";
            }
            if (agendamentoVo.getDataConfirmacaoConsulta() != null){
                icStep3.setImageResource(R.drawable.ic_panorama_fish_eye_green_24dp);
                statusConsulta = "Consulta agendada e confirmada.";
            }
            if (agendamentoVo.getFinalizado()){
                icStep1.setImageResource(R.drawable.ic_check_circle_green_24dp);
                icStep2.setVisibility(View.INVISIBLE);
                icStep3.setVisibility(View.INVISIBLE);
                statusConsulta = "Consulta agendada e confirmada.";
            }
        }

        TextView especialidadeData = (TextView) rowView.findViewById(R.id.lbEspecialidadeData);
        especialidadeData.setText(agendamentoVo.getProfissional().getEspec() + " em " +
                DateUtils.formatddMMyyyyHHmm(agendamentoVo.getDataAgendamento()));

        TextView lbNomeMedico = (TextView) rowView.findViewById(R.id.lbNomeMedico);
        lbNomeMedico.setText(agendamentoVo.getProfissional().getNome());

        TextView lbEndereco = (TextView) rowView.findViewById(R.id.lbEndereco);
        lbEndereco.setText(agendamentoVo.getProfissional().getEndereco());

        TextView lbStatus = (TextView) rowView.findViewById(R.id.lbDetalhes);
        lbStatus.setText(statusConsulta);

        return rowView;
    }
}

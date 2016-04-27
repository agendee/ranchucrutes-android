package br.com.wjaa.ranchucrutes.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.StringUtils;
import br.com.wjaa.ranchucrutes.vo.ProfissionalBasicoVo;
import roboguice.activity.RoboActionBarActivity;

/**
 * Created by wagner on 4/26/16.
 */
public class DetalhesProfissionalHelper {


    public static void build(Activity context, String titulo,CollapsingToolbarLayout mCollapsingToolbarLayout, Toolbar toolbar, ProfissionalBasicoVo profissional) {

        mCollapsingToolbarLayout.setTitle(titulo);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(context.getResources().getColor(android.R.color.white));
        mCollapsingToolbarLayout.setExpandedTitleColor(context.getResources().getColor(android.R.color.white));
        mCollapsingToolbarLayout.setStatusBarScrimColor(context.getResources().getColor(android.R.color.white));

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            toolbar.setBackground(null);
        }

        if ( context instanceof RoboActionBarActivity){
            RoboActionBarActivity activity = (RoboActionBarActivity)context;
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        }

        new LoadImage(context,profissional.getNumeroRegistro()).start();
        TextView txtPDNome = ((TextView) context.findViewById(R.id.txtPDNome));
        String nome = profissional.getNome();
        txtPDNome.setText(nome);

        TextView crmProfissional = ((TextView) context.findViewById(R.id.crm));
        String crm = profissional.getNumeroRegistro() != null ? profissional.getNumeroRegistro().toString() : "--";
        crmProfissional.setText("CRM: " + crm);

        TextView especProfissional = ((TextView) context.findViewById(R.id.espec));
        especProfissional.setText(profissional.getEspec());

        TextView endProfissional = ((TextView) context.findViewById(R.id.endereco));
        endProfissional.setText(StringUtils.isNotBlank(profissional.getEndereco()) ? profissional.getEndereco() : "End: --");

        TextView telProfissional = ((TextView) context.findViewById(R.id.telefone));
        if (profissional.getTelefone() != null && !"".equals(profissional.getTelefone())){
            telProfissional.setText("Tel: " + profissional.getTelefone());
        }else{
            telProfissional.setText("Tel: --");
        }

    }

    static class LoadImage extends Thread{
        private Activity view;
        private Bitmap bitmap = null;
        private Integer idRegistro;


        LoadImage(Activity view, Integer idRegistro){
            this.view = view;
            this.idRegistro = idRegistro;
        }

        @Override
        public void run() {

            String imageUrl = "http://agendee.com.br/f/" + idRegistro + ".jpg";
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                view.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView i = (ImageView) view.findViewById(R.id.badge);
                        i.setImageBitmap(bitmap);
                        i.invalidate();

                    }
                });
            } catch (Exception e) {
                Log.e("Helper", "Erro ao buscar a imagem", e);
                view.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView i = (ImageView) view.findViewById(R.id.badge);
                        i.setImageResource(R.drawable.unknow);
                        i.invalidate();
                    }
                });

            }

        }
    }

}

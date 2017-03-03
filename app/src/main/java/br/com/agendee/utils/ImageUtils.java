package br.com.agendee.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import br.com.agendee.R;

/**
 * Created by wagner on 4/29/16.
 */
public class ImageUtils {



    public static void loadImage(Activity context, ImageView fotoUser, String urlFoto) {
        new LoadImage(context,fotoUser,urlFoto).start();
    }


    static class LoadImage extends Thread{
        private Activity context;
        private ImageView imageView;
        private String urlFoto;

        LoadImage(Activity context, ImageView imageView, String urlFoto){
            this.context = context;
            this.imageView = imageView;
            this.urlFoto = urlFoto;
        }

        @Override
        public void run() {
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlFoto).getContent());
                if (imageView != null){
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            imageView.invalidate();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("ImageUtils", "Erro ao buscar a imagem", e);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO ARRUMAR ESSE IMAGEN DEFAULT CASO OUTRAS CLASSES USEM
                      imageView.setImageResource(R.drawable.unknow);
                      imageView.invalidate();
                    }
                });

            }
        }
    }
}



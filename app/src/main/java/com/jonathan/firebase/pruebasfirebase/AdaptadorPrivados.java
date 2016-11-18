package com.jonathan.firebase.pruebasfirebase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jonathan on 29/10/2016.
 */
public class AdaptadorPrivados extends BaseAdapter {


        private Context context;
        private ArrayList<Usuarios> listaPrivado;

        public AdaptadorPrivados(Context context, ArrayList<Usuarios> listaPrivado) {
            this.context = context;
            this.listaPrivado = listaPrivado;
        }

        @Override
        public int getCount() {
            return listaPrivado.size();
        }

        @Override
        public Object getItem(int i) {
            return listaPrivado.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(context, R.layout.contacto, null);
            TextView nombreContacto = (TextView) v.findViewById(R.id.nombreContacto);
            TextView emailContacto = (TextView) v.findViewById(R.id.emailContacto);
            TextView idContacto = (TextView) v.findViewById(R.id.idContacto);
            TextView urlContacto = (TextView) v.findViewById(R.id.urlContacto);
            CircleImageView fotoContacto = (CircleImageView) v.findViewById(R.id.imagenContacto);
            TextView tokenContacto = (TextView) v.findViewById(R.id.tokenContacto);

            String texto = (listaPrivado.get(i).getNombreContacto());
            String email = (listaPrivado.get(i).getEmail());
            String id = (listaPrivado.get(i).getUserID());
            String url = (listaPrivado.get(i).getUrl());
            String token = (listaPrivado.get(i).getToken());

            nombreContacto.setText(texto);
            emailContacto.setText(email);
            idContacto.setText(id);
            urlContacto.setText(url);
            tokenContacto.setText(token);

            if (!url.equals("null")){
                Glide.with(context).load(url).into(fotoContacto);
            }else{
                Glide.with(context).load("http://www.freeiconspng.com/uploads/account-icon-33.png").into(fotoContacto);
            }
            v.setTag(texto);
            return v;
        }
}

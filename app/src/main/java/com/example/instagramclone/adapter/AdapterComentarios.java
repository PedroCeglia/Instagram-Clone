package com.example.instagramclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.models.Comentarios;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolderComentarios> {

    private List<Comentarios> listaDeComentarios = new ArrayList<>();
    private Context c;

    public AdapterComentarios(List<Comentarios> listaDeComentarios, Context c) {
        this.listaDeComentarios = listaDeComentarios;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolderComentarios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent , false);
        return new MyViewHolderComentarios(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderComentarios holder, int position) {

        // Recupera objeto do Tipo comentario da Lista passada no Construtor
        Comentarios comentarios = listaDeComentarios.get(position);
        // Configurando os Text Views
        holder.tvComentario.setText(comentarios.getComentario());
        holder.tvNomeUsuario.setText(comentarios.getNomeUsuario());
        // Configurando foto caso possua uma
        if (comentarios.getFotoUsuario() != null){
            if (!comentarios.getFotoUsuario().isEmpty()){
                Uri url = Uri.parse(comentarios.getFotoUsuario());
                Glide.with(c).load(url).into(holder.civFotoUsuario);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaDeComentarios.size();
    }

    public class MyViewHolderComentarios extends RecyclerView.ViewHolder {

        private CircleImageView civFotoUsuario;
        private TextView tvNomeUsuario;
        private TextView tvComentario;

        public MyViewHolderComentarios(@NonNull View itemView) {
            super(itemView);
            civFotoUsuario = itemView.findViewById(R.id.civcomentarioFotoUsuario);
            tvNomeUsuario = itemView.findViewById(R.id.tvComentarioNomeDoUsuario);
            tvComentario = itemView.findViewById(R.id.tvComentarioComentario);
        }
    }

}

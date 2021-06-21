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
import com.example.instagramclone.models.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListaUsuarios extends RecyclerView.Adapter<AdapterListaUsuarios.MyViewHolderListandoUsuarios> {


    private List<Usuario> listaDeUsuarios;
    private Context c;

    public AdapterListaUsuarios(List<Usuario> listaDeUsuarios, Context c) {
        this.listaDeUsuarios = listaDeUsuarios;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolderListandoUsuarios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_listando_usuarios, parent , false);
        return new MyViewHolderListandoUsuarios(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderListandoUsuarios holder, int position) {

        Usuario usuarioSelecionado = listaDeUsuarios.get(position);

        holder.tvNome.setText(usuarioSelecionado.getNome());

        if (usuarioSelecionado.getFoto() != null){
            if (!usuarioSelecionado.getFoto().isEmpty()){
                Uri url = Uri.parse(usuarioSelecionado.getFoto());
                Glide.with(c).load(url).into(holder.civ);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    public class MyViewHolderListandoUsuarios extends RecyclerView.ViewHolder{

        private CircleImageView civ;
        private TextView tvNome;

        public MyViewHolderListandoUsuarios(@NonNull View itemView) {
            super(itemView);
            civ = itemView.findViewById(R.id.civPerfilListaUsuarios);
            tvNome = itemView.findViewById(R.id.tvAdapterlistaDeUsuarioUsuarioNome);
        }
    }

}

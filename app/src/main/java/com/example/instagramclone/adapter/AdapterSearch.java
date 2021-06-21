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

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    private List<Usuario> listaDeUsuarios = new ArrayList<>();
    private Context context;

    public AdapterSearch(List<Usuario> listaDeUsuarios, Context context) {
        this.listaDeUsuarios = listaDeUsuarios;
        this.context = context;
    }

    public List<Usuario> getListaDeUsuarios() {
        return listaDeUsuarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.layout_adapter_perfil_search,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario user = listaDeUsuarios.get(position);

        // Configurando foto
        if (user.getFoto() == null || user.getFoto().isEmpty()){
            holder.civFotoPerfil.setImageResource(R.drawable.padrao);
        }else {
            Uri url = Uri.parse(user.getFoto());
            Glide.with(context).load(url).into(holder.civFotoPerfil);
        }

        // Configurando nome
        holder.tvNomeUsuario.setText(user.getNome());
    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civFotoPerfil;
        private TextView tvNomeUsuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civFotoPerfil = itemView.findViewById(R.id.civAdapterSearch);
            tvNomeUsuario = itemView.findViewById(R.id.tvAdapterNomeSearch);

        }
    }

}

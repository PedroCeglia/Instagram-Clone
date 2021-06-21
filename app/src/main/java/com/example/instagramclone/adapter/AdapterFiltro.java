package com.example.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterFiltro extends RecyclerView.Adapter<AdapterFiltro.MyViewHolderFiltro> {

    private List<ThumbnailItem> listaDeFiltros = new ArrayList<>();
    private Context c;

    public AdapterFiltro(List<ThumbnailItem> listaDeFiltros, Context c) {
        this.listaDeFiltros = listaDeFiltros;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolderFiltro onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewFiltro = LayoutInflater.from( parent.getContext() ).inflate(R.layout.adapter_filtros_layout,
                parent, false);
        return new MyViewHolderFiltro(viewFiltro);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFiltro holder, int position) {

        ThumbnailItem filtro = listaDeFiltros.get(position);

        holder.ivFiltro.setImageBitmap(filtro.image);
        holder.tvNomeFiltro.setText(filtro.filterName);


    }

    @Override
    public int getItemCount() {
        return listaDeFiltros.size();
    }

    public class MyViewHolderFiltro extends RecyclerView.ViewHolder{

        private TextView tvNomeFiltro;
        private ImageView ivFiltro;

         public MyViewHolderFiltro(@NonNull View itemView) {
             super(itemView);

             tvNomeFiltro = itemView.findViewById(R.id.tvNomedoFiltroAdapter);
             ivFiltro = itemView.findViewById(R.id.ivFiltroAdapter);

         }
     }

}

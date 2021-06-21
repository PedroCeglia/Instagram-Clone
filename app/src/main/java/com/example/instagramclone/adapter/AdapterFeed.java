package com.example.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activitys.ComentarioActivity;
import com.example.instagramclone.activitys.PerfilUsuariosAmigoActivity;
import com.example.instagramclone.activitys.listandousuarios.ListaDeUsuariosActivity;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Comentarios;
import com.example.instagramclone.models.Curtidas;
import com.example.instagramclone.models.Feed;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolderFeed> {

    // Lista
    private List<Feed> listaDeFeed;
    private List<Usuario> listaDeUsuario;
    private Context c;

    public AdapterFeed(List<Feed> listaDeFeed ,List<Usuario> listaDeUsuario2 ,Context c) {
        this.listaDeFeed = listaDeFeed;
        this.listaDeUsuario = listaDeUsuario2;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolderFeed onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new MyViewHolderFeed(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFeed holder, int position) {

        // Recuperando feed da lista
        Feed feed = listaDeFeed.get(position);

        // Recuperando usuario Logado
        Usuario usuario = UsuarioFirebase.getDadosUsuarioLogadoAuth();

        // Configurando TextViews
        holder.tvLegenda.setText(feed.getDescrição());
        holder.tvNomeusuarioLogado.setText(feed.getNomeUsuario());

        // Adicionando ImageView
        Uri urlImagemV = Uri.parse(feed.getFotoPostagem());
        Glide.with(c).load(urlImagemV).into(holder.ivPost);

        // Adicionando Circle Image View se A foto não for nula
        if (feed.getFotoUsuario() != null){
            if (!feed.getFotoUsuario().isEmpty()){
                // Adicionando CIV
                Uri urlCiv = Uri.parse(feed.getFotoUsuario());
                Glide.with(c).load(urlCiv).into(holder.civFotoDePerfil);
            }
        }


        // Recuperando dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("curtidas")
                .child(feed.getIdPostagem());

        Log.i("Feed Fragment", feed.getIdPostagem());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int qtdCurtidas = 0 ;


                // Caso Exista o nó de Quantidade de curtidas
                if (snapshot.hasChild("qntCurtidas")){
                    Curtidas curtidasRec = snapshot.getValue(Curtidas.class);
                    qtdCurtidas = curtidasRec.getQntCurtidas();
                }

                // Verificando se ja foi clicado
                if (snapshot.hasChild(usuario.getId())){
                    holder.likeButton.setLiked(true);
                }
                else {
                    holder.likeButton.setLiked(false);
                }

                // Montar Objeto para o LikeButtom
                Curtidas curtidas = new Curtidas();
                curtidas.setIdPostagem(feed.getIdPostagem());
                curtidas.setUsuario(usuario);
                curtidas.setQntCurtidas(qtdCurtidas);

                // Evento de Clique do Like Button
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        // Curtir
                        curtidas.salvar();
                        holder.tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
                    }
                    @Override
                    public void unLiked(LikeButton likeButton) {
                        // Descurtir
                        curtidas.removerCurtidas();
                        holder.tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
                    }
                });

                holder.tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (listaDeUsuario.size() != 0) {

            Usuario usuarioFeed = listaDeUsuario.get(position);
            holder.tvNomeusuarioLogado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(c, PerfilUsuariosAmigoActivity.class);
                    i.putExtra("usuarioSelecionado", usuarioFeed);
                    c.startActivity(i);

                }
            });
        }

        // Evento de clique exibindo quem curtiu
        holder.tvCurtidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(c, ListaDeUsuariosActivity.class);
                i.putExtra("idPostagem", feed.getIdPostagem());
                c.startActivity(i);
            }
        });

        // Evento de clique no comentario
        holder.ivComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, ComentarioActivity.class);
                i.putExtra("idPostagem", feed.getIdPostagem());
                c.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaDeFeed.size();
    }




    public class MyViewHolderFeed extends RecyclerView.ViewHolder{

        // Widgets
        private TextView tvCurtidas, tvNomeusuarioLogado, tvLegenda, tvAbrirComentarios;
        private ImageView ivPost, ivComentario;
        private CircleImageView civFotoDePerfil;
        private LikeButton likeButton;

        public MyViewHolderFeed(@NonNull View itemView) {
            super(itemView);

            tvCurtidas = itemView.findViewById(R.id.tvAdapterCurtidas);
            tvNomeusuarioLogado = itemView.findViewById(R.id.tvAdapterNomeUsuario);
            tvLegenda = itemView.findViewById(R.id.tvAdapterLegendas);
            tvAbrirComentarios = itemView.findViewById(R.id.tvAdapterVizualizarComentarios);
            ivPost = itemView.findViewById(R.id.ivAdapterPostagemVisualizandoPost);
            ivComentario = itemView.findViewById(R.id.ivAdapterComentario);
            civFotoDePerfil = itemView.findViewById(R.id.civAdapterFotousuarioLogadoVisuPost);
            likeButton = itemView.findViewById(R.id.likeButtonAdapter);
        }
    }

}

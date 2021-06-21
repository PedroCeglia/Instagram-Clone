package com.example.instagramclone.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activitys.listandousuarios.ListaDeUsuariosActivity;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Curtidas;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizandoPostagensActivity extends AppCompatActivity {

    // Widgets
    private TextView tvCurtidas, tvNomeusuarioLogado, tvLegenda, tvAbrirComentarios;
    private ImageView ivPost;
    private CircleImageView civFotoDePerfil;
    private LikeButton likeButton;

    // Models
    private Postagem postagem;
    private Usuario usuarioDaFoto;
    private String idPostagem;


    // Firebase
    private DatabaseReference database;
    private DatabaseReference curtidasRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizando_postagens);

        // Configurações Iniciais
        configuracoesIniciais();

        // Configurando toolbar
        configurandoToolbar();

        // Recuperando Postagem
        recuperandoPostagem();

        //Associando as Postagens aos Widgets
        associandoPostagemAosWidgets();

        // Associando o Usuario da Foto aos Widget
        associandoUsuarioAosWidgets();

        tvCurtidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListaDeUsuariosActivity.class);
                i.putExtra("idPostagem", idPostagem);
                startActivity(i);
            }
        });


    }

    public void configuracoesIniciais(){

        // Associando widgets
        tvCurtidas = findViewById(R.id.tvAdapterCurtidas);
        tvNomeusuarioLogado = findViewById(R.id.tvAdapterNomeUsuario);
        tvLegenda = findViewById(R.id.tvAdapterLegendas);
        tvAbrirComentarios = findViewById(R.id.tvAdapterVizualizarComentarios);
        ivPost = findViewById(R.id.ivAdapterPostagemVisualizandoPost);
        civFotoDePerfil = findViewById(R.id.civAdapterFotousuarioLogadoVisuPost);
        likeButton = findViewById(R.id.likeButtonAdapter);


        // Referenciando Instancias do firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();

    }

    public void configurandoToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Visualizar postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_twotone_highlight_off_24);
    }

    public void recuperandoPostagem(){

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            postagem = (Postagem) bundle.getSerializable("postagem");
            idPostagem = postagem.getIdPostagem();
            usuarioDaFoto = (Usuario) bundle.getSerializable("usuario");

        }
    }

    public void associandoUsuarioAosWidgets(){

        // Recuperando nome do Usuario
        tvNomeusuarioLogado.setText(usuarioDaFoto.getNome());

        // Se a foto do Usuario não estiver Nula nem Vazia
        if (usuarioDaFoto.getFoto() != null){
            if (!usuarioDaFoto.getFoto().isEmpty()){
                Uri url = Uri.parse(usuarioDaFoto.getFoto());
                Glide.with(getApplicationContext()).load(url).into(civFotoDePerfil);
            }
        }
    }

    public void associandoPostagemAosWidgets(){
        tvLegenda.setText(postagem.getDescricao());
        Uri url = Uri.parse(postagem.getFoto());
        Glide.with(getApplicationContext()).load(url).into(ivPost);
    }

    // Recuperando Curtidas
    public void recuperandoCurtidas(){

        // Recuperando usuario Logado
        Usuario usuario = UsuarioFirebase.getDadosUsuarioLogadoAuth();

        curtidasRef = database
                .child("curtidas")
                .child(idPostagem);

        Log.i("Vizualizando Post", idPostagem);
        curtidasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int qtdCurtidas = 0 ;
                // Caso Exista o nó de Quantidade de curtidas
                if (snapshot.hasChild("qntCurtidas")){
                    Curtidas curtidasRec = snapshot.getValue(Curtidas.class);
                    qtdCurtidas = curtidasRec.getQntCurtidas();
                }
                // Verificando se ja foi clicado
                if (snapshot.hasChild(UsuarioFirebase.getIdentificadorUsuario())){
                    Log.i("dando Erro", UsuarioFirebase.getIdentificadorUsuario());
                    likeButton.setLiked(true);
                }
                else {
                    Log.i("dando Erro", UsuarioFirebase.getIdentificadorUsuario());
                    likeButton.setLiked(false);
                }
                // Montar Objeto para o LikeButtom
                Curtidas curtidas = new Curtidas();
                curtidas.setIdPostagem(idPostagem);
                curtidas.setUsuario(usuario);
                curtidas.setQntCurtidas(qtdCurtidas);

                // Evento de Clique do Like Button
                likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        // Curtir
                        curtidas.salvar();
                        tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
                    }
                    @Override
                    public void unLiked(LikeButton likeButton) {
                        // Descurtir
                        curtidas.removerCurtidas();
                        tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
                    }
                });
                tvCurtidas.setText(String.valueOf(curtidas.getQntCurtidas()) + " curtidas");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperandoCurtidas();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
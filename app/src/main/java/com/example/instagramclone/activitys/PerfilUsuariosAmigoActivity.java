package com.example.instagramclone.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activitys.listandousuarios.ListandoSeguidoresActivity;
import com.example.instagramclone.activitys.listandousuarios.ListandoSeguindoActivity;
import com.example.instagramclone.adapter.AdapterGridAmigo;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilUsuariosAmigoActivity extends AppCompatActivity {

    // Models
    private Usuario dadosDoUsuario;
    private Usuario dadosDoUsuarioLogado;
    private List<Postagem> listaDePostagens = new ArrayList<>();
    private List<String> listaDeUrl = new ArrayList<>();

    //Adapter
    private AdapterGridAmigo adapterGridAmigo;

    // Variaveis
    private String stPublicacao, stSeguidores, stSeguindo;
    private int x;
    private boolean y, y2 ;
    private int seguindo, seguidores;
    private int seguidoresUserAmigo, seguindoUserLogado;

    //Widgets
    private CircleImageView civFotoPerfil;
    private TextView tvPublicacao, tvSeguidores , tvSeguindo;
    private Toolbar toolbar;
    private Button btSeguir;
    private GridView gridPerfilAmigo;

    // Banco de Dados Firebase
    private DatabaseReference database;
    private DatabaseReference seguidoresRef;
    private DatabaseReference seguindoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference postagensUsuarioRef;
    private FirebaseUser usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuarios);

        // Configurações Inciais
        configuracoesIniciais();

        tvSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListandoSeguidoresActivity.class);
                i.putExtra("usuario", dadosDoUsuario);
                startActivity(i);
            }
        });

        tvSeguindo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListandoSeguindoActivity.class);
                i.putExtra("usuario", dadosDoUsuario);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Recupera os dados do usuario escolhido
        recuperandoDadosUsuarioescolhido();

        // Recupera Usuario Logado E //Verifica se Seguimos o usuario
        verificaUsuarioLogado();

        // Adicionando Evento de clique ao grid View
        gridPerfilAmigo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Recupera a postagem selecionada e passa a mesma para a outra activity
                Postagem postagem = listaDePostagens.get(position);
                String idPodt = postagem.getIdPostagem();
                Intent i = new Intent(getApplicationContext(), VisualizandoPostagensActivity.class);
                i.putExtra("postagem", postagem);
                i.putExtra("idPostagem", idPodt);
                i.putExtra("usuario", dadosDoUsuario);
                startActivity(i);
            }
        });


        seguidoresUserAmigo = dadosDoUsuario.getSeguidores();
       // seguindoUserLogado = dadosDoUsuarioLogado.getSeguindo();

        botãoClick();

        if (dadosDoUsuario.getId().equals(UsuarioFirebase.getIdentificadorUsuario())){
            btSeguir.setOnClickListener(null);
        }
    }


    public void configuracoesIniciais(){
        // Associando Widgets
        civFotoPerfil = findViewById(R.id.civImagemPerfil2);
        tvPublicacao = findViewById(R.id.tvNumeroPublicacao2);
        tvSeguidores = findViewById(R.id.tvNumeroSeguidores2);
        tvSeguindo = findViewById(R.id.tvNumeroSeguindo2);
        toolbar = findViewById(R.id.toolbar_principal);
        btSeguir = findViewById(R.id.btSeguir);
        gridPerfilAmigo = findViewById(R.id.gridPerfilAmigo);


        // Configurando os Banco de Dados
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        seguidoresRef = database.child("seguidores");
        usuariosRef = database.child("usuarios");
        seguindoRef = database.child("seguindo");
        usuarioLogado = UsuarioFirebase.getUsuarioAtual();
    }




    public void verificaUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getUid());
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recuperar dados do Firebase
                dadosDoUsuarioLogado = snapshot.getValue(Usuario.class);
                seguindoUserLogado = dadosDoUsuarioLogado.getSeguindo();

                // Verifica se seguimos o usuario
                verificaSeSegueUsuarioAmigo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void verificaSeSegueUsuarioAmigo(){

        DatabaseReference seguidorRef = seguidoresRef
                .child(dadosDoUsuario.getId())
                .child(usuarioLogado.getUid());
        // Esse Listener Consulta o Banco de Dados Apenas uma vez
        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Se existe algum dado no Nó Selecionado
                if (snapshot.exists()){
                    // Ja estamos Seguindo
                    Log.i("dadosUsuario",": Seguindo");
                    habiltarBotaoSeguir(true);
                }else{
                    // Não Estamos Seguindo
                    Log.i("dadosUsuario", ": Seguir");
                    habiltarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void habiltarBotaoSeguir( boolean segueUsuario) {
        if (segueUsuario) {
            x = 0;
            y = true;
            y2 = false;
        } else {
            x = 1;
            y = false;
            y2 = true;
        }

        if (x == 0) {
            // Se segueUsuario for Verdadeiro // Caso estejamos seguindo o usuario
            // Caso a Query encontre o nó que escolhemos...
            btSeguir.setText("Seguindo");
        } else {
            // Se segueUsuario for falso // Caso Não estejamos seguindo o usuario
            // Caso a Query Não Encontre o nó que escolhemos...
            btSeguir.setText("Seguir");
        }
        // Adicionar Evento para deSeguir o usuario
    }

    public void teste(boolean b, int i){
        if (b) {
            // Incrmenetar seguindo do usuario logado
            seguindo = seguindoUserLogado + i;
            seguidores = seguidoresUserAmigo + i;
        }else {
            seguindo = seguindoUserLogado;
            seguidores = seguidoresUserAmigo;
        }

        Log.i("teste", "TesteTeste");
    }
    public void botãoClick(){




        Log.i("dados", String.valueOf(seguidoresUserAmigo));
        btSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dados", String.valueOf(seguidoresUserAmigo));
                if (x == 0) {
                    // salvar seguidor
                    btSeguir.setText("Seguir");
                    deletarSeguidor(dadosDoUsuarioLogado, dadosDoUsuario);

                    teste( y, -1);

                    // Criando Hash Map
                    HashMap<String, Object> hashSeguindo = new HashMap<>();
                    hashSeguindo.put("seguindo", seguindo);

                    DatabaseReference usuarioSeguindoAmigo = usuariosRef
                            .child(dadosDoUsuarioLogado.getId());
                    usuarioSeguindoAmigo.updateChildren(hashSeguindo);

                    // Incrmentando Seguidores do Amigo
                    HashMap<String, Object> hashSeguidores = new HashMap<>();
                    hashSeguidores.put("seguidores", seguidores);

                    DatabaseReference dataSeguidoresAmigo = usuariosRef
                            .child(dadosDoUsuario.getId());

                    dataSeguidoresAmigo.updateChildren(hashSeguidores);

                    String stSeguidoresAtualizando = String.valueOf(seguidores);
                    tvSeguidores.setText(stSeguidoresAtualizando);

                    x = 1;
                    } else {

                    btSeguir.setText("Seguindo");
                    salvarSeguidor(dadosDoUsuarioLogado, dadosDoUsuario);

                    teste(y2, 1);
                        // Incrmentando Seguidores


                    // Criando Hash Map
                    HashMap<String, Object> hashSeguindo = new HashMap<>();
                    hashSeguindo.put("seguindo", seguindo);

                    DatabaseReference usuarioSeguindoAmigo = usuariosRef
                            .child(dadosDoUsuarioLogado.getId());
                    usuarioSeguindoAmigo.updateChildren(hashSeguindo);


                    HashMap<String, Object> hashSeguidores = new HashMap<>();
                    hashSeguidores.put("seguidores", seguidores);

                    DatabaseReference dataSeguidoresAmigo = usuariosRef
                            .child(dadosDoUsuario.getId());
                     dataSeguidoresAmigo.updateChildren(hashSeguidores);

                     String stSeguidoresAtualizando = String.valueOf(seguidores);
                     tvSeguidores.setText(stSeguidoresAtualizando);

                     x = 0;
                    }
                }
            });
    }

    private void salvarSeguidor(Usuario usuarioLog , Usuario usuarioQueSeraSeguido){

        // Configurando Hash Map
        HashMap<String , Object> dadosUsuarioLog = new HashMap<>();
        dadosUsuarioLog.put("nome",usuarioLog.getNome());
        dadosUsuarioLog.put("foto",usuarioLog.getFoto());
        dadosUsuarioLog.put("id",usuarioLog.getId());

        // Configurando Hash Map
        HashMap<String , Object> dadosUsuarioAmigo = new HashMap<>();
        dadosUsuarioAmigo.put("nome",usuarioQueSeraSeguido.getNome());
        dadosUsuarioAmigo.put("foto",usuarioQueSeraSeguido.getFoto());
        dadosUsuarioAmigo.put("id",usuarioQueSeraSeguido.getId());

        // Seguidores
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioQueSeraSeguido.getId())
                .child(usuarioLog.getId());
        seguidorRef.setValue(dadosUsuarioLog);

        // Seguindo
        DatabaseReference seguindoRef2 = seguindoRef
                .child(usuarioLog.getId())
                .child(usuarioQueSeraSeguido.getId());

        seguindoRef2.setValue(dadosUsuarioAmigo);
    }

    private void deletarSeguidor(Usuario usuarioLog , Usuario usuarioQueSeraSeguido){

        // Seguidores
        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioQueSeraSeguido.getId())
                .child(usuarioLog.getId());
        seguidorRef.removeValue();

        // Seguindo
        DatabaseReference seguindoRef2 = seguindoRef
                .child(usuarioLog.getId())
                .child(usuarioQueSeraSeguido.getId());
        seguindoRef2.removeValue();

    }

    public void recuperandoDadosUsuarioescolhido(){

        // recuperando dados do usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("usuarioSelecionado")){
                dadosDoUsuario = (Usuario) bundle.getSerializable("usuarioSelecionado");
            }
        }

        // Configurando Toolbar

        toolbar.setTitle(dadosDoUsuario.getNome());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurando Imagem
        if (dadosDoUsuario.getFoto() != null){
            if (!dadosDoUsuario.getFoto().isEmpty()){
                Uri url = Uri.parse(dadosDoUsuario.getFoto());
                Glide.with(getApplicationContext()).load(url).into(civFotoPerfil);
            }
        }

        // Configurando Seguidores Postagens e Seguindo

        //stPublicacao = String.valueOf(dadosDoUsuario.getPublicacoes());
        stSeguidores = String.valueOf(dadosDoUsuario.getSeguidores());
        stSeguindo = String.valueOf(dadosDoUsuario.getSeguindo());

        tvSeguindo.setText(stSeguindo);
        tvSeguidores.setText(stSeguidores);
        //tvPublicacao.setText(stPublicacao);

        // Configurando Referencia postagens Usuario Escolhido
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagens")
                .child(dadosDoUsuario.getId());

        // Inicializa o Universal Image Loader
        inicializarImageLoader();
        // Recupera as Postagens do nosso Banco de Dados
        carregarFotosPostagem();
    }

    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void carregarFotosPostagem(){

        // Recupera as fotos postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Limpando Lista de postagens
                listaDePostagens.clear();
                listaDeUrl.clear();


                // Configurar tamanho do grid
                // recuperamos o tamanho da tela do usuario e dividimos por 3
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoGridPadding = tamanhoGrid - 20;
                int tamanhoImagem = tamanhoGridPadding/3;
                gridPerfilAmigo.setColumnWidth(tamanhoImagem);
                gridPerfilAmigo.setVerticalSpacing(2);
                gridPerfilAmigo.setHorizontalSpacing(2);

                for (DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);

                    listaDePostagens.add(postagem);
                    listaDeUrl.add(postagem.getFoto());
                }
                tvPublicacao.setText(String.valueOf(listaDePostagens.size()));

                // Configurando Adapter para o GridView
                adapterGridAmigo = new AdapterGridAmigo(getApplicationContext(), R.layout.adapter_grid, listaDeUrl);
                gridPerfilAmigo.setAdapter(adapterGridAmigo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    // Método que será chamado quando sairmos da activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
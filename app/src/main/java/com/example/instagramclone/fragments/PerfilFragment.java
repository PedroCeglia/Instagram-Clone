package com.example.instagramclone.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activitys.ConfigurandoPerfilActivity;
import com.example.instagramclone.activitys.listandousuarios.ListaDeUsuariosActivity;
import com.example.instagramclone.activitys.VisualizandoPostagensActivity;
import com.example.instagramclone.activitys.listandousuarios.ListandoSeguidoresActivity;
import com.example.instagramclone.activitys.listandousuarios.ListandoSeguindoActivity;
import com.example.instagramclone.adapter.AdapterGridAmigo;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Widgets
    private Button btEditarPerfil;
    private ProgressBar progressBar;
    private TextView tvPublicacao, tvSeguidores , tvSeguindo, tvNomeUsuario;
    private TextView tvPublicacaotx, tvSeguidorestx , tvSeguindotx;
    private CircleImageView civPerfil;
    private GridView gridView;

    // Models
    private Usuario user;
    private DatabaseReference usuariosRef;

    // Adapter
    private AdapterGridAmigo adapterGridAmigo;

    // Firebase
    private DatabaseReference postagensUsuarioRef;
    private DatabaseReference database;
    private DatabaseReference seguidoresRef;

    // Listener
    private ValueEventListener valueEventListenerRecuperandoUsuario;

    // Variaveis
    private List<Postagem> listaDePostagens = new ArrayList<>();
    private List<Usuario> listaDeUsuariosSemOLogado = new ArrayList<>();
    private List<String> listaDeUrl = new ArrayList<>();
    private String stPublicacao, stSeguidores, stSeguindo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configuraçoes Iniciais
        configuracoesIniciais(view);

        // Abrir edição de perfil
        btEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ConfigurandoPerfilActivity.class);
                startActivity(i);

            }
        });
        Usuario usuer22 = UsuarioFirebase.getDadosUsuarioLogadoAuth();

        tvSeguindotx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListandoSeguindoActivity.class);
                i.putExtra("usuario", usuer22);
                startActivity(i);
            }
        });

        tvSeguindo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListandoSeguindoActivity.class);
                i.putExtra("usuario", usuer22);
                startActivity(i);
            }
        });

        tvSeguidorestx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListandoSeguidoresActivity.class);
                i.putExtra("usuario", usuer22);
                startActivity(i);
            }
        });



        tvSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListandoSeguidoresActivity.class);
                i.putExtra("usuario", usuer22);
                startActivity(i);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }




    @Override
    public void onStart() {
        super.onStart();

        // Recuperando Image, do usuario
        if (!user.getFoto().isEmpty()){
            if (UsuarioFirebase.getUsuarioAtual().getPhotoUrl() != null){
                Uri url = Uri.parse(user.getFoto());
                Glide.with(getActivity())
                        .load(url)
                        .into(civPerfil);
            }
        }
        // Alterando TextView de Nome
        tvNomeUsuario.setText(user.getNome());

        // Recupera os dados do usuario Logado no RealTime Database
        getDadosUsuarioAtualDatabase();

        // Recupera as postagens do Usuario Logado
        postagensRecu();

        // Adicionando Evento de clique ao grid View
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Recupera a postagem selecionada e passa a mesma para a outra activity
                Postagem postagem = listaDePostagens.get(position);
                Intent i = new Intent(getActivity(), VisualizandoPostagensActivity.class);
                i.putExtra("postagem", postagem);
                i.putExtra("usuario", user);
                startActivity(i);
            }
        });

    }

    public void postagensRecu(){

        // Configurando Referencia postagens Usuario Escolhido
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagens")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        // Inicializa o Universal Image Loader
        inicializarImageLoader();
        // Recupera as Postagens do nosso Banco de Dados
        carregarFotosPostagem();
    }

    // Inicializa o Loader Imagens
    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }

    // Recupera as Postagens do firebase e configura elas no gridView
    // configura o tamanho das colunas do gridView
    // Depos de tudo ser Carregado carrega os Widgets
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
                gridView.setColumnWidth(tamanhoImagem);
                gridView.setVerticalSpacing(2);
                gridView.setHorizontalSpacing(2);

                for (DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    listaDePostagens.add(postagem);
                    listaDeUrl.add(postagem.getFoto());


                }
                // Configura o TextView de postagens
                tvPublicacao.setText(String.valueOf(listaDePostagens.size()));

                // Atualizando As Postagens no Banco de Dados
                Usuario atualizandoPostagens = new Usuario();
                atualizandoPostagens.setPublicacoes(listaDePostagens.size());
                atualizandoPostagens.setId(UsuarioFirebase.getIdentificadorUsuario());
                atualizandoPostagens.atualizarPostagens();


                // Configurando Adapter para o GridView
                adapterGridAmigo = new AdapterGridAmigo(getActivity(), R.layout.adapter_grid, listaDeUrl);
                gridView.setAdapter(adapterGridAmigo);

                // Por fim exibe os Widget Novamente
                exibindoWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Exibe os Widgets
    public void exibindoWidgets(){
        progressBar.setVisibility(View.GONE);
        btEditarPerfil.setVisibility(View.VISIBLE);
        civPerfil.setVisibility(View.VISIBLE);
        tvPublicacao.setVisibility(View.VISIBLE);
        tvPublicacaotx.setVisibility(View.VISIBLE);
        tvSeguidores.setVisibility(View.VISIBLE);
        tvSeguidorestx.setVisibility(View.VISIBLE);
        tvSeguindo.setVisibility(View.VISIBLE);
        tvSeguindotx.setVisibility(View.VISIBLE);
        tvNomeUsuario.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);
    }

    // Esconde os Widgets
    public void escondendoWidgets(){
        progressBar.setVisibility(View.VISIBLE);
        btEditarPerfil.setVisibility(View.GONE);
        civPerfil.setVisibility(View.GONE);
        tvPublicacao.setVisibility(View.GONE);
        tvPublicacaotx.setVisibility(View.GONE);
        tvSeguidores.setVisibility(View.GONE);
        tvSeguidorestx.setVisibility(View.GONE);
        tvSeguindo.setVisibility(View.GONE);
        tvSeguindotx.setVisibility(View.GONE);
        tvNomeUsuario.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);

    }

    // recupera os dados do Usuario atual no firebase
    // Altera os text Views de Publicação Seguidores e Seguindo
    public void getDadosUsuarioAtualDatabase(){
        valueEventListenerRecuperandoUsuario = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){

                    Usuario usuario = dados.getValue( Usuario.class );

                    String emailUsuarioAtual = user.getEmail();
                    // Se for o Usuario Logado
                    if ( emailUsuarioAtual.equals( usuario.getEmail() ) ){
                        // Configurando Seguidores Postagens e Seguindo
                        stPublicacao = String.valueOf(usuario.getPublicacoes());
                        stSeguidores = String.valueOf(usuario.getSeguidores());
                        stSeguindo = String.valueOf(usuario.getSeguindo());

                        tvSeguindo.setText(stSeguindo);
                        tvSeguidores.setText(stSeguidores);
                        tvPublicacao.setText(stPublicacao);
                    }
                    else{
                        listaDeUsuariosSemOLogado.add(usuario);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void configuracoesIniciais(View view){
        btEditarPerfil = view.findViewById(R.id.btEditarPerfil);
        civPerfil = view.findViewById(R.id.civImagemPerfil);
        tvPublicacao = view.findViewById(R.id.tvNumeroPublicacao);
        tvPublicacaotx = view.findViewById(R.id.tvPublicacaotx);
        tvSeguidores = view.findViewById(R.id.tvNumeroSeguidores);
        tvSeguidorestx = view.findViewById(R.id.tvSeguidorestx);
        tvSeguindo = view.findViewById(R.id.tvNumeroSeguindo);
        tvSeguindotx = view.findViewById(R.id.tvSeguindotx);
        tvNomeUsuario = view.findViewById(R.id.tvNomeUsuario);
        gridView = view.findViewById(R.id.gridPerfilFrag);
        progressBar = view.findViewById(R.id.progressAberturaPerfilFrag);
        user = UsuarioFirebase.getDadosUsuarioLogadoAuth();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        seguidoresRef = database.child("seguidores");
        usuariosRef = database.child("usuarios");
        escondendoWidgets();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerRecuperandoUsuario);
    }
}
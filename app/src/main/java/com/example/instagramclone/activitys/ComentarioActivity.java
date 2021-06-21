package com.example.instagramclone.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterComentarios;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Comentarios;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentarioActivity extends AppCompatActivity {

   //Listene
    private ValueEventListener valueEventListener;

    // Firebase
    private DatabaseReference database;
    private DatabaseReference comentariosRef;
    private DatabaseReference comentariosRef2;

    // Variaveis
    private String idPostagem;

    // Models
    private List<Comentarios> listaDeComentarios;
    private AdapterComentarios adapterComentarios;
    private Usuario usuario;

    // Widgets
    private EditText etComentario;
    private ImageView ivSendComentario;
    private RecyclerView rvComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        // Configurações Iniciais
        configuracoesIniciais();

        // configurando toolbar;
        configurandoToolbar();

        // Configurando RecyclerView
        configurandoRecyclerView();

        // Recuperando Id da Postagem
        recuperandoPostagem();

        //salvando comentario
        ivSendComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvandoComentario();
            }
        });

    }

    public void recuperandoComentarios(){

        // Recuperando Instancia de Comentarios
        comentariosRef = database
                .child("comentarios")
                .child(idPostagem);
        //Criando Listener
        valueEventListener = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Limpando a lista de comentarios
                listaDeComentarios.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // Recuperando A classe Comentario do Firebase e adicionando a Lista de Comentarios
                    Comentarios comentarios = ds.getValue(Comentarios.class);
                    listaDeComentarios.add(comentarios);
                }
                // Notificando Mudança no Adapter
                adapterComentarios.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // Salva Instancia de Comentario no Firebase
    public void salvandoComentario(){
        // Recupera texto do editText
        String textoCaixaDeTexto = etComentario.getText().toString();
        // Caso o texto não estege vazio ele cria um Objeto do tipo Comentario e associa suas propriedades
        if (!textoCaixaDeTexto.isEmpty() && textoCaixaDeTexto != null){
            Comentarios comentarios = new Comentarios();
            comentarios.setIdPostagem(idPostagem);
            comentarios.setIdUsuario(usuario.getId());
            comentarios.setFotoUsuario(usuario.getFoto());
            comentarios.setNomeUsuario(usuario.getNome());
            comentarios.setComentario(textoCaixaDeTexto);
            comentarios.salvar();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Insira um Comentario ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Recupera o id Da Postagem da Ultima Activity
    public void recuperandoPostagem(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idPostagem = bundle.getString("idPostagem");
        }
    }

    // Configura o RecyclerView dos Comentarios
    public void configurandoRecyclerView(){
        // Configurando RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvComentarios.setHasFixedSize(true);
        rvComentarios.setLayoutManager(layoutManager);
        adapterComentarios = new AdapterComentarios(listaDeComentarios, getApplicationContext());
        rvComentarios.setAdapter(adapterComentarios);
    }

    // Faz As Configurações Iniciais
    public void configuracoesIniciais(){

        // Associando Widgets
        rvComentarios = findViewById(R.id.rvComentarios);
        etComentario = findViewById(R.id.etComentarioEt);
        ivSendComentario = findViewById(R.id.ivSendComentario);

        // Models
        usuario = UsuarioFirebase.getDadosUsuarioLogadoAuth();
        listaDeComentarios = new ArrayList<>();

        // Instancias do Firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
    }

    // Configura nossa toolbar
    public void configurandoToolbar(){
        Toolbar toobar = findViewById(R.id.toolbar_principal);
        toobar.setTitle("Comentarios");
        setSupportActionBar(toobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    // Esse método é chamado quando clicarmos no item a esquerda da toolbar
    @Override
    public boolean onNavigateUp() {
        this.finish();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperandoComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListener);
    }
}
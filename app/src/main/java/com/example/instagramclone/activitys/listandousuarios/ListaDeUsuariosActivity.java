package com.example.instagramclone.activitys.listandousuarios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.PerfilUsuariosAmigoActivity;
import com.example.instagramclone.adapter.AdapterListaUsuarios;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.fragments.PerfilFragment;
import com.example.instagramclone.helper.RecyclerItemClickListener;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaDeUsuariosActivity extends AppCompatActivity {

    private String idPostagem;
    private List<Usuario> listaDeQuemCurtiu;


    private Toolbar toolbar;


    // Firebase
    private DatabaseReference database;
    private DatabaseReference usuariosRef;
    private DatabaseReference curtidasUsuarioRef;

    // Recycler View
    private RecyclerView rv;
    private AdapterListaUsuarios adapter;

    private ValueEventListener vlew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_usuarios);



        // configurações Iniciais
        configuracoesIniciais();

        // recuperando Bundle ( idPostagem; Request;)
        recuperandoBundles();

        // configurando toolbar
        configurandoToolbar();



    }

    public void configurandoToolbar(){
        toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Curtidas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    public void configuracoesIniciais(){

        // Listas de Usuarios
        listaDeQuemCurtiu = new ArrayList<>();

        // Referenciando Instancias do firebase
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = database.child("usuarios");

    }

    public void configurandoRecyclerView(){

        rv = findViewById(R.id.rvListandoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterListaUsuarios(listaDeQuemCurtiu, getApplicationContext());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rv,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario usuarioSelecionado = listaDeQuemCurtiu.get(position);


                                Intent i = new Intent(getApplicationContext(), PerfilUsuariosAmigoActivity.class);
                                i.putExtra("usuarioSelecionado", usuarioSelecionado);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Configurando recyclerView
        configurandoRecyclerView();
        recuperandoTodosOsUsuarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(vlew);
    }

    public void recuperandoBundles(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            idPostagem = bundle.getString("idPostagem");
        }
    }

    public void recuperandoTodosOsUsuarios( ){

        listaDeQuemCurtiu.clear();

        vlew = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario uusseerr = ds.getValue(Usuario.class);
                    recuperandoQuemCurtiu(uusseerr);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recuperandoQuemCurtiu(Usuario userLike){
        curtidasUsuarioRef = database
                .child("curtidas")
                .child(idPostagem)
                .child(userLike.getId());
        curtidasUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    listaDeQuemCurtiu.add(userLike);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
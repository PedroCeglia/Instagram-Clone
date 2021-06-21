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
import android.widget.AdapterView;

import com.example.instagramclone.R;
import com.example.instagramclone.activitys.PerfilUsuariosAmigoActivity;
import com.example.instagramclone.adapter.AdapterListaUsuarios;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.RecyclerItemClickListener;
import com.example.instagramclone.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListandoSeguidoresActivity extends AppCompatActivity {

    private DatabaseReference database;
    private DatabaseReference seguidoresUsuarioRef;

    private List<Usuario> listaDeSeguidores;
    private Usuario usuario;

    private RecyclerView rv;
    private AdapterListaUsuarios adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listando_seguidores);


        database = ConfiguracaoFirebase.getFirebaseDatabase();
        listaDeSeguidores = new ArrayList<>();

        configurandoToolbar();

        recuperandoBundles();

        configurandoRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperandoSeguidores();
    }

    public void configurandoToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Seguidores");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    public void recuperandoBundles(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            usuario = (Usuario) bundle.getSerializable("usuario");
        }
    }

    public void configurandoRecyclerView(){

        rv = findViewById(R.id.rvListandoSeguidores);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterListaUsuarios(listaDeSeguidores, getApplicationContext());

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
                                Usuario usuarioSelecionado = listaDeSeguidores.get(position);

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

    public void recuperandoSeguidores(){

        listaDeSeguidores.clear();

        seguidoresUsuarioRef = database
                .child("seguidores")
                .child(usuario.getId());
        seguidoresUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario user = ds.getValue(Usuario.class);
                    listaDeSeguidores.add(user);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
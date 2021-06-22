package com.example.instagramclone.activitys.listandousuarios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ListandoSeguindoActivity extends AppCompatActivity {

    private DatabaseReference database;
    private DatabaseReference seguindoUsuarioRef;

    private List<Usuario> listaDeSeguindo;
    private Usuario usuario;

    private RecyclerView rv;
    private AdapterListaUsuarios adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listando_seguindo);

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        listaDeSeguindo = new ArrayList<>();

        configurandoToolbar();

        recuperandoBundles();

        configurandoRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperandoSeguindo();
    }

    public void configurandoToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Seguindo");
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


        rv = findViewById(R.id.rvListandoSeguindo);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterListaUsuarios(listaDeSeguindo, getApplicationContext());

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
                                Usuario usuarioSelecionado = listaDeSeguindo.get(position);

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

    public void recuperandoSeguindo(){

        // Limpando Lista
        listaDeSeguindo.clear();

        seguindoUsuarioRef = database
                .child("seguindo")
                .child(usuario.getId());
        seguindoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario user = ds.getValue(Usuario.class);
                    Log.i("Nome do Usuario",user.getNome());
                    listaDeSeguindo.add(user);
                }
                adapter.notifyDataSetChanged();
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
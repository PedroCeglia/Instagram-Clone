package com.example.instagramclone.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.adapter.AdapterFiltro;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.RecyclerItemClickListener;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Postagem;
import com.example.instagramclone.models.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {
    // bloco de inicialização estatica
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    // Models
    private Postagem postagem;
    private Usuario usuarioLogado;

    // Variaveis
    private Bitmap bitmap;
    private Bitmap bitmapFiltro;
    private Bundle bundle;
    private List<ThumbnailItem> listaFiltrosTb = new ArrayList<>();
    private AdapterFiltro adapterFiltro;

    // Widgets
    private ImageView ivImagemEscolhida;
    private Toolbar toolbar;
    private RecyclerView rvFiltro;
    private EditText etDescricao;
    private AlertDialog alertDialog;

    // Firebase
    private DatabaseReference database;
    private DatabaseReference usuarioRef;
    private DatabaseReference seguidoresRef;
    private String idUsuarioLogado;
    private DataSnapshot seguidoresSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // Configurações Iniciais
        configuraçõesIniciais();
    }

    public void configuraçõesIniciais(){

        // Associando os Ids aos Widgets
        ivImagemEscolhida = findViewById(R.id.ivImagemEscolhida);
        toolbar = findViewById(R.id.toolbar_principal);
        rvFiltro = findViewById(R.id.rvFiltro);
        etDescricao = findViewById(R.id.etDescricao);

        abrirDialogCarreagamento("Carregando dados do usuario");

        // Configurações Firebase
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuarioRef = database.child("usuarios").child(idUsuarioLogado);
        seguidoresRef = database.child("seguidores").child(idUsuarioLogado);
        recuperandoDadosParaUmaNovaPostagem();

        // Configurando Toolbar
        configurandoToolbar();

        // Recuperando Imagem
        recuperandoImagem();

        // Configurando Imagem Com Filtro
        bitmapFiltro = bitmap.copy(bitmap.getConfig(), true);


        // Configurando recycler view
        configurandoRecyclerView();

        // recuperando lista de Filtros
        criandoListaDeFiltro();

    }

    public void configurandoToolbar(){
        // Configurando Toolbar
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    public void recuperandoImagem(){
        // Recuperando foto
        bundle = getIntent().getExtras();
        if (bundle != null){
            try {
                byte[] dadosImagem = bundle.getByteArray("dadosImagem");
                bitmap = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
                ivImagemEscolhida.setImageBitmap(bitmap);;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void recuperandoDadosParaUmaNovaPostagem(){
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);

                // Recuperando Seguidores
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        seguidoresSnapshot = snapshot;

                        alertDialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void configurandoRecyclerView(){

        // Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        // Adapter
        adapterFiltro = new AdapterFiltro(listaFiltrosTb , getApplicationContext());
        // Configurando RecyclerView
        rvFiltro.setLayoutManager(layoutManager);
        rvFiltro.setAdapter(adapterFiltro);
        rvFiltro.setHasFixedSize(true);
        rvFiltro.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rvFiltro,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                ThumbnailItem filtro = listaFiltrosTb.get(position);
                                bitmapFiltro = bitmap.copy(bitmap.getConfig(), true);

                                Filter filterExibido = filtro.filter;
                                ivImagemEscolhida.setImageBitmap(filterExibido.processFilter(bitmapFiltro));
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

    public void  criandoListaDeFiltro(){

        // Limpando Filtros
        listaFiltrosTb.clear();
        ThumbnailsManager.clearThumbs();



        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = bitmap;
            item.filter = filter;
            item.filterName = filter.getName();
            ThumbnailsManager.addThumb(item);
        }
        listaFiltrosTb.addAll(ThumbnailsManager.processThumbs(getApplicationContext() ) );
        adapterFiltro.notifyDataSetChanged();
    }

    public void publicarPostagem(){
        postagem = new Postagem();
        postagem.gerandoId();
        postagem.setDescricao(etDescricao.getText().toString());
        postagem.setIdUsuario(idUsuarioLogado);
        // Configurando Imagem
        salvandoERecuperandoFotoNoStorage();
//        finish();

    }

    public void salvandoERecuperandoFotoNoStorage(){

        abrirDialogCarreagamento("Salvando Postagem");
        // Configurando Foto com Filtro
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmapFiltro.compress(Bitmap.CompressFormat.JPEG, 70 , baos);
        byte[] dadosImagem = baos.toByteArray();

        // Salvando Imagem no firebase Storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        StorageReference postRef = storageRef.child("imagens")
                .child("postagens")
                .child(postagem.getIdUsuario())
                .child(postagem.getIdPostagem() + ".jpeg");

        UploadTask uploadTask = postRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Erro ao fazer upload da imagem",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),
                        "Sucesso ao fazer upload da imagem",
                        Toast.LENGTH_SHORT).show();

                postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Configurando Id e Descrição
                        postagem.setFoto(uri.toString());
                        if (postagem.salvar(seguidoresSnapshot)){
                            Toast.makeText(getApplicationContext(),
                                    "Sucesso ao fazer upload da imagem22222222222",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.cancel();
                            finish();

                        }
                    }
                });
            }
        });

    }
    private void abrirDialogCarreagamento(String titulo){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento_publicando);

        alertDialog = alert.create();
        alertDialog.show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_adapter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_salvar_postagem :
                publicarPostagem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
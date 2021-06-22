package com.example.instagramclone.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.example.instagramclone.models.Comentarios;
import com.example.instagramclone.models.Curtidas;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigurandoPerfilActivity extends AppCompatActivity {


    // Firebase
    private StorageReference storageReference;
    private DatabaseReference database;
    private DatabaseReference postagemRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference seguindoRef;
    private DatabaseReference curtindoRef;
    private DatabaseReference comentariosRef;

    // Listener
    private ValueEventListener valueEventListenerRecuperandoUsuario;
    private ValueEventListener valueEventListenerRecuperandoSeguindo;
    private ValueEventListener valueEventListenerLikes;
    private ValueEventListener valueEventListenerFeed;

    // Variaveis
    private List<Usuario> listaDeUsuariosSemOLogado = new ArrayList<>();
    private List<Postagem> listaDePostagensDoUsuario = new ArrayList<>();

    // Models
    private Usuario user;
    // private Usuario usuario;
    // Widgets
    private CircleImageView civEditarPerfil;
    private EditText etEditNome, etEditEmail;

    // request code
    private static final int SELECAO_GALERIA = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurando_perfil);

        // Associando Ids aos widgets // Instanciando models
        configuracoesIniciais();

        // Configurando Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        // Configurando botão de voltar da Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        // Recuperar dados do usuario para os EditText
        recuperandoDadosDoUsuario();

    }



    public void configuracoesIniciais(){
        // Associando Ids aos widgets
        civEditarPerfil = findViewById(R.id.civEditPerfil);
        etEditNome = findViewById(R.id.editEditNomePerfil);
        etEditEmail = findViewById(R.id.editEditEmailPerfil);
        // O usuario não pode alterar o Email
        etEditEmail.setFocusable(false);

        // Instanciando os Models
        user = UsuarioFirebase.getDadosUsuarioLogadoAuth();

        // Referenciando Firebase
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = database.child("usuarios");
        seguidoresRef = database.child("seguidores");
        seguindoRef = database.child("seguindo");
        postagemRef = database.child("postagens").child(UsuarioFirebase.getIdentificadorUsuario());
        comentariosRef = database.child("comentarios");


    }

    public void recuperandoDadosDoUsuario(){
        // Recupera os dados do usuario atual para os edit Text
        etEditNome.setText(user.getNome());
        etEditEmail.setText(user.getEmail());
        // Recuperando Image, do usuario
        if (!user.getFoto().isEmpty()){
            if (UsuarioFirebase.getUsuarioAtual().getPhotoUrl() != null){
                Uri url = Uri.parse(user.getFoto());
                Glide.with(ConfigurandoPerfilActivity.this)
                        .load(url)
                        .into(civEditarPerfil);
            }
        }
    }

    public void atualizandoNomeDoUsuario(View view){
        // recupera novo Nome
        String nomeAtualizado = etEditNome.getText().toString();
        //atualiza nome do perfil
        UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);
        // Atualizar nome no banco de dados
        user.setNome(nomeAtualizado);
        user.atualizar();
        recuperandoTodosOsUsuarios(nomeAtualizado);
        recuperandoListaUsuarios(nomeAtualizado);
        recuperandoUsuarioSeguindo(nomeAtualizado);

        seguidoresRef.removeEventListener(valueEventListenerRecuperandoUsuario);
        seguindoRef.removeEventListener(valueEventListenerRecuperandoSeguindo);
        // Exibindo Toast
        Toast.makeText(
                getApplicationContext(),
                "Dados Alterados Com Sucesso",
                Toast.LENGTH_SHORT
        ).show();

    }

    public void alterarFoto(View view){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        startActivityForResult(i, SELECAO_GALERIA );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {

                switch ( requestCode ){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada );
                        break;
                }

                if ( imagem != null ){

                    civEditarPerfil.setImageBitmap( imagem );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child( identificadorUsuario )
                            .child(user.getId() + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
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

                            imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    atualizaFotoUsuario( uri );
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void atualizaFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if ( retorno ){
            user.setFoto( url.toString() );
            user.atualizar();

            Toast.makeText(getApplicationContext(),
                    "Sua foto foi alterada!",
                    Toast.LENGTH_SHORT).show();

        }
    }

    // funciona
    public void recuperandoListaUsuarios(String nome){
        valueEventListenerRecuperandoUsuario = seguindoRef.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Usuario usuario = ds.getValue(Usuario.class);
                        if (usuario.getId() != user.getId()){
                            // Caso não seje o Usuario Logado o Usuario é adicionado a lista
                            listaDeUsuariosSemOLogado.add(usuario);
                            Log.i("NomeUser", usuario.getNome());
                            Map objeto = new HashMap();
                            objeto.put("/seguidores/" + usuario.getId() + "/" + user.getId(), atualizandoNomeSeguidores(nome));
                            database.updateChildren(objeto);
                        }
                    }
                    Log.i("NomeUser", String.valueOf(listaDeUsuariosSemOLogado.size()));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void atualizandoFeed(Usuario usuario1, String nome){
        // Recuperando Postagens
        //valueEventListenerRecuperandoPostagens =
        valueEventListenerFeed = postagemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Postagem post = ds.getValue(Postagem.class);
                    listaDePostagensDoUsuario.add(post);

                    Map objetoPostLike = new HashMap();
                    objetoPostLike.put("/feed/" + usuario1.getId() + "/" + post.getIdPostagem() , atualizandoNomeFeed(nome, post));
                    database.updateChildren(objetoPostLike);

                    Log.i("info", usuario1.getId());
                    //like(nome, post);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void like(String nome , Postagem post){
        Log.i("FOI?", post.getIdPostagem());
        curtindoRef = database.child("curtidas").child(post.getIdPostagem()).child(user.getId());
        curtindoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.i("FOI?", "fOI");
                if (snapshot.exists()){
                    Log.i("FOI?", "fOI");
                    Curtidas like = new Curtidas();
                    like.setIdPostagem(post.getIdPostagem());
                    like.atualizarNomeFoto(nome, user.getFoto(), user.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizandoLike(String nome, Usuario uusseerr){
        Log.i("FOI?", uusseerr.getId());
        DatabaseReference postRef = database.child("postagens").child(uusseerr.getId()) ;
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Postagem post = ds.getValue(Postagem.class);
                    Log.i("FOI?", "Chamou");
                    like(nome, post);
                    recuperandoComentarios(post, nome);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void recuperandoTodosOsUsuarios(String nome){
        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario uusseerr = ds.getValue(Usuario.class);
                    atualizandoLike(nome, uusseerr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recuperandoUsuarioSeguindo(String nome){

        valueEventListenerRecuperandoSeguindo = seguidoresRef.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Usuario usuario = ds.getValue(Usuario.class);
                        if (usuario.getId() != user.getId()) {
                            Map objeto = new HashMap();
                            objeto.put("/seguindo/" + usuario.getId() + "/" + user.getId(), atualizandoNomeSeguindo(nome, user.getFoto(), user.getId()));
                            database.updateChildren(objeto);
                            atualizandoFeed(usuario, nome);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void recuperandoComentarios(Postagem postagem, String nome){

        comentariosRef.child(postagem.getIdPostagem()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Comentarios comentarios = ds.getValue(Comentarios.class);
                    if (comentarios.getIdUsuario().equals(user.getId())){

                        HashMap<String, Object> usuarioMap = new HashMap<>();
                        usuarioMap.put("nomeUsuario", nome );
                        usuarioMap.put("fotoUsuario", user.getFoto() );


                        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
                        DatabaseReference comentarioRef = database
                                .child("comentarios")
                                .child(comentarios.getIdPostagem())
                                .child(comentarios.getIdComentario()); // idPostagem

                        Map<String, Object> valoresUsuario = usuarioMap;

                        comentarioRef.updateChildren( valoresUsuario );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    public Map<String, Object> atualizandoNomeSeguidores(String nome){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nome", nome );
        usuarioMap.put("foto", user.getFoto() );
        usuarioMap.put("id", user.getId() );
        return usuarioMap;
    }

    public Map<String, Object> atualizandoLikesUsers(String nome){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nomeUsuario", nome );
        usuarioMap.put("fotoUsuario", user.getFoto() );
        //usuarioMap.put("id", user.getId() );
        return usuarioMap;
    }

    public Map<String, Object> atualizandoNomeSeguindo(String nome, String foto, String id){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nome", nome );
        usuarioMap.put("foto", foto );
        usuarioMap.put("id", id );
        return usuarioMap;
    }

    public Map<String, Object> atualizandoNomeFeed(String nome, Postagem post){
        // HashMap que usaremos para salvar referencia
        HashMap<String, Object> dadosSeguidor = new HashMap<>();
        dadosSeguidor.put("nomeUsuario", nome);
        dadosSeguidor.put("fotoUsuario", user.getFoto());
        dadosSeguidor.put("fotoPostagem", post.getFoto());
        dadosSeguidor.put("descrição", post.getDescricao());
        dadosSeguidor.put("idPostagem", post.getIdPostagem());
        return dadosSeguidor;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
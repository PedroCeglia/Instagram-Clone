package com.example.instagramclone.models;

import android.util.Log;

import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.example.instagramclone.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {

    private String idPostagem;
    private String idUsuario;
    private String descricao;
    private String foto;

    // Esse método cria o Id da Postagem
    public Postagem() {

    }

    public void gerandoId(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef.child("postagens");
        String idPostagemSt = postRef.push().getKey();
        setIdPostagem(idPostagemSt);
    }

    // Salva Instancia de Postagens no FireBase
    public boolean salvar(DataSnapshot snapshot){

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogadoAuth();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        objeto.put("/postagens/" + getIdUsuario() + "/" + getIdPostagem(), this);

        for (DataSnapshot ds: snapshot.getChildren()){
            // Identificador da Postagem
            String idSeguidor = ds.getKey();
            // HashMap que usaremos para salvar referencia
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getFoto());
            dadosSeguidor.put("idUsuario", usuarioLogado.getId());
            dadosSeguidor.put("fotoPostagem", getFoto());
            dadosSeguidor.put("descrição", getDescricao());
            dadosSeguidor.put("idPostagem", getIdPostagem());

            objeto.put("/feed/" + idSeguidor + "/" + getIdPostagem(), dadosSeguidor);
        }
        firebaseRef.updateChildren(objeto);
        return true;
    }

    // usamos esse método para atualizar nosso banco de dados
    public void atualizar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postagensRef = database.child("postagens")
                .child( getIdUsuario() ).child(getIdPostagem());
        Map<String, Object> valoresPost = converterParaMap();
        postagensRef.updateChildren( valoresPost );
    }
    // Usamos esse método para atualizar o banco de dados
    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("foto", getFoto() );
        usuarioMap.put("descricao", getDescricao());
        usuarioMap.put("idUsuario", getIdUsuario());
        return usuarioMap;
    }


    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

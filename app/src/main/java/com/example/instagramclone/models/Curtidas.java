package com.example.instagramclone.models;

import com.example.instagramclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class Curtidas {

    private  int qntCurtidas = 0;
    private String idPostagem;
    private Usuario usuario;

    public Curtidas() {
    }

    // Usamos esse método para salvar uma Instancia de Curtidas no Firebase
    // Nele tambem chamamos o método que adicionara "1" ás curtidas
    public void salvar(){
        // usamos o hash Map para criarmos/atualizarmos nosso Banco de Dados do firebase, ele funciona que nem um Dicionario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("fotoUsuario", usuario.getFoto());
        // Referencia do Firebase
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference curtidasRef = database
                .child("curtidas")
                .child(getIdPostagem())
                .child(usuario.getId());
        curtidasRef.setValue(dadosUsuario);
        // Método que adicionara mais uma curtida
        atualizarCurtidas(1);
    }
    public void atualizarNomeFoto(String nome, String foto, String id){
        // usamos o hash Map para criarmos/atualizarmos nosso Banco de Dados do firebase, ele funciona que nem um Dicionario
            HashMap<String, Object> dadosUsuario = new HashMap<>();
            dadosUsuario.put("nomeUsuario", nome);
            dadosUsuario.put("fotoUsuario", foto);
            // Referencia do Firebase
            DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
            DatabaseReference curtidasRef = database
                    .child("curtidas")
                    .child(getIdPostagem())
                    .child(id);
            curtidasRef.setValue(dadosUsuario);
    }

    // Esse método atualiza o valor das curtidas
    // Aumentando ou diminuindo o valor
    public void atualizarCurtidas(int valor){
        // Referencia do firebase
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference curtidasRef = database
                .child("curtidas")
                .child(getIdPostagem())
                .child("qntCurtidas");

        setQntCurtidas(getQntCurtidas() + valor);
        curtidasRef.setValue(getQntCurtidas());
    }

    // Esse método diminui o valor das curtidas
    // E deleta as mesmas do Banco de Dados
    public void removerCurtidas(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference curtidasRef = database
                .child("curtidas")
                .child(getIdPostagem())
                .child(usuario.getId());
        curtidasRef.removeValue();
        atualizarCurtidas(-1);
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getQntCurtidas() {
        return qntCurtidas;
    }

    public void setQntCurtidas(int qntCurtidas) {
        this.qntCurtidas = qntCurtidas;
    }
}

package com.example.appfetin.helper;

import android.net.Uri;
import android.util.Log;

import com.example.appfetin.model.Empresa;
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth autenticacao =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        return Objects.requireNonNull(autenticacao.getCurrentUser()).getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atulizarNomeUsuario(String nome){
        try{
            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar nome do perfil");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atulizarNomeEmpresa(String nomeEmpresa){
        try{
            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nomeEmpresa )
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar nome do perfil");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if(firebaseUser.getPhotoUrl()==null){
            usuario.setCaminhoFoto("");
        }else{
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;

    }

    public static Empresa getDadosEmpresaLogada(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(firebaseUser.getUid());
        empresa.setEmail(firebaseUser.getEmail());
        empresa.setNomeE(firebaseUser.getDisplayName());
        empresa.setIdEmpresa(firebaseUser.getUid());

        if(firebaseUser.getPhotoUrl()==null){
            empresa.setUrlImagem("");
        }else{
            empresa.setUrlImagem(firebaseUser.getPhotoUrl().toString());
        }

        return empresa;

    }

    public static void atulizarFotoUsuario(Uri url){
        try{
            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar a foto do perfil");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.example.appfetin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfetin.R;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView textCadastrar, textCadastrarEmpresa;
    private Button buttonEntrar;
    private EditText campoEmail, campoSenha;

    private static FirebaseAuth autenticacao;
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();

        textCadastrar.setOnClickListener(this::abrirCadastro);
        textCadastrarEmpresa.setOnClickListener(this::abrirCadastroEmpresa);

        buttonEntrar.setOnClickListener(v -> {
            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if(!email.isEmpty()){
                if(!senha.isEmpty()){
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    validarLogin(usuario);
                }else{
                    Toast.makeText(LoginActivity.this, "Informe a senha", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LoginActivity.this, "Informe o email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            recuperarDados();
        }
    }

    private void abrirCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }

    private void abrirCadastroEmpresa(View view){
        startActivity(new Intent(LoginActivity.this, CadastroEmpresaActivity.class));
    }

    private void inicializarComponentes(){
        textCadastrar = findViewById(R.id.textCadastrar);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);
        textCadastrarEmpresa = findViewById(R.id.textCadastrarEmpresa);
    }

    public void validarLogin(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        recuperarDados();
                    }else{
                        String excecao;
                        try{
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "Email e senha não correspondem a um usuário cadastrado!";
                        }catch (FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não está cadastrado.";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar ususário: " + e .getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void recuperarDados(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario u =snapshot.getValue(Usuario.class);
                assert u != null;
                String tipoUsuario = u.getTipo();
                if(tipoUsuario.equals("C")){
                    startActivity(new Intent(getApplicationContext(), ClienteActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.appfetin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appfetin.R;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private Button buttonCadastrar;
    private EditText campoNome, campoEmail, campoSenha, campoTelefone, campoEndereco;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        buttonCadastrar.setOnClickListener(v -> {
            String textoNome = campoNome.getText().toString();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();
            String textoTelefone = campoTelefone.getText().toString();
            String textoEndereco = campoEndereco.getText().toString();

            if(!textoNome.isEmpty()){
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        if(!textoTelefone.isEmpty()){
                            if(!textoEndereco.isEmpty()){
                                usuario = new Usuario();
                                usuario.setNome(textoNome);
                                usuario.setEmail(textoEmail);
                                usuario.setSenha(textoSenha);
                                usuario.setTelefonePessoal(textoTelefone);
                                usuario.setEndereco(textoEndereco);
                                usuario.setTipo("C");
                                cadastrar(usuario);
                            }else{
                                Toast.makeText(CadastroActivity.this, "Informe um endereço!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(CadastroActivity.this, "Informe um telefone!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this, "Informe uma senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "Informe o email!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Informe o nome!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void inicializarComponentes(){
        buttonCadastrar = findViewById(R.id.buttonCadastro);
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        campoTelefone = findViewById(R.id.editCadastroTelefone);
        campoEndereco = findViewById(R.id.editCadastroEndereco);
    }

    public void cadastrar(Usuario u){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(u.getEmail(), u.getSenha()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                try{
                    String idUsuario = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    usuario.setId(idUsuario);
                    usuario.salvar();

                    //salvar dados no profile do firebse
                    UsuarioFirebase.atulizarNomeUsuario(usuario.getNome());

                    Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ClienteActivity.class));

                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                String excecao;
                try{
                    throw Objects.requireNonNull(task.getException());
                }catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte!";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um email válido";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Esse email ja foi cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar ususário: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
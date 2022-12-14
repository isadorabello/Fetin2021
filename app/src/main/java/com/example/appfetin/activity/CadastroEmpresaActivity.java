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
import com.example.appfetin.model.Empresa;
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroEmpresaActivity extends AppCompatActivity {

    private Button buttonCadastrar;
    private EditText campoNome, campoNomeE, campoCategoria, campoTempo, campoPreco,
            campoEmail, campoSenha, campoTelefone, campoEndereco;

    private Usuario usuario;
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_empresa);
        inicializarComponentes();

        buttonCadastrar.setOnClickListener(v -> {
            String textoNome = campoNome.getText().toString();
            String textoNomeE = campoNomeE.getText().toString();
            String textoCategoria = campoCategoria.getText().toString();
            String textoTempo = campoTempo.getText().toString();
            String textoPreco = campoPreco.getText().toString();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();
            String textoTelefone = campoTelefone.getText().toString();
            String textoEndereco = campoEndereco.getText().toString();

            if(!textoNome.isEmpty()){
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        if(!textoTelefone.isEmpty()){
                            if(!textoEndereco.isEmpty()){
                                if(!textoNomeE.isEmpty()){
                                    if(!textoCategoria.isEmpty()){
                                        if(!textoTempo.isEmpty()){
                                            if(!textoPreco.isEmpty()){
                                                empresa = new Empresa();
                                                empresa.setNomeD(textoNome);
                                                empresa.setEmail(textoEmail);
                                                empresa.setSenha(textoSenha);
                                                empresa.setTelefoneC(textoTelefone);
                                                empresa.setTelefoneP("N??o informado");
                                                empresa.setEndereco(textoEndereco);
                                                empresa.setNomeE(textoNomeE);
                                                empresa.setNomeFiltro(textoNomeE);
                                                empresa.setCategoria(textoCategoria);
                                                empresa.setTempoEntrega(textoTempo);
                                                empresa.setPrecoEntrega(textoPreco);
                                                cadastrarEmpresa(empresa);
                                            }else{
                                                Toast.makeText(CadastroEmpresaActivity.this, "Informe o pre??o padr??o de entrega!", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(CadastroEmpresaActivity.this, "Informe o tempo de entrega padr??o!", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(CadastroEmpresaActivity.this, "Informe a categoria do seu estabelecimento!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(CadastroEmpresaActivity.this, "Informe o nome do estabelecimento!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CadastroEmpresaActivity.this, "Informe um endere??o!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(CadastroEmpresaActivity.this, "Informe um telefone!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroEmpresaActivity.this, "Informe uma senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroEmpresaActivity.this, "Informe o email!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroEmpresaActivity.this, "Informe o nome!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cadastrarEmpresa(Empresa emp){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(emp.getEmail(), emp.getSenha()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                try{
                    String idEmpresa = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    emp.setIdEmpresa(idEmpresa);
                    emp.salvar();

                    usuario = new Usuario();
                    usuario.setId(idEmpresa);
                    usuario.setTipo("E");
                    usuario.salvar();

                    UsuarioFirebase.atulizarNomeEmpresa(emp.getNomeE());

                    Toast.makeText(CadastroEmpresaActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),EmpresaActivity.class));

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
                    excecao = "Digite um email v??lido";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Esse email ja foi cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar usus??rio: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroEmpresaActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarComponentes(){
        buttonCadastrar = findViewById(R.id.buttonCadastroEmpresa);
        campoNome = findViewById(R.id.editCadastroNomeDono);
        campoNomeE = findViewById(R.id.editCadastroNomeEmpresa);
        campoCategoria = findViewById(R.id.editCadastroCategoria);
        campoTempo = findViewById(R.id.editCadastroTempo);
        campoPreco = findViewById(R.id.editCadastroPreco);
        campoEmail = findViewById(R.id.editCadastroEmailEmpresa);
        campoSenha = findViewById(R.id.editCadastroSenhaEmpresa);
        campoTelefone = findViewById(R.id.editCadastroTelefoneComercial);
        campoEndereco = findViewById(R.id.editCadastroEnderecoEmpresa);
    }
}
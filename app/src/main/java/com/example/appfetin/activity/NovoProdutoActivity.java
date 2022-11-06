package com.example.appfetin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appfetin.R;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class NovoProdutoActivity extends AppCompatActivity {

    private EditText editNome, editDescricao, editPreco;
    private Button buttonCadastrar;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Adicionar Produto");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        buttonCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String descricao = editDescricao.getText().toString();
            String preco = editPreco.getText().toString();

            AsyncTaskProduto task = new AsyncTaskProduto();
            task.execute(nome, descricao, preco, idUsuarioLogado);
        });
    }

    private void inicializarComponentes(){
        editNome = findViewById(R.id.editNomeProduto);
        editPreco = findViewById(R.id.editPrecoProduto);
        editDescricao = findViewById(R.id.editDescricaoProduto);
        buttonCadastrar = findViewById(R.id.buttonCadastrarProduto);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    /*
    1->Parametro a ser passado para a classe
    2->Tipo de valor que será utilizado para o progresso da tarefa
    3->Retorno após tarefa atualizada
     */

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskProduto extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... strings) {

            Produto produto = new Produto();

            if(!strings[0].isEmpty()){
                if(!strings[1].isEmpty()){
                    if(!strings[2].isEmpty()){
                        produto.setNome(strings[0]);
                        produto.setDescricao(strings[1]);
                        produto.setPreco(Double.parseDouble(strings[2]));
                        produto.setIdUsuario(strings[3]);
                        produto.salvar();

                        finish();
                    }else{
                        Toast.makeText(NovoProdutoActivity.this, "Informe o preço do produto!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NovoProdutoActivity.this, "Informe a descrição do produto!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(NovoProdutoActivity.this, "Informe o nome do produto!", Toast.LENGTH_SHORT).show();
            }

            return "Produto salvo com sucesso!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(NovoProdutoActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
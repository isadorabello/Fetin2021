package com.example.appfetin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.appfetin.R;
import com.example.appfetin.fragments.PedidosEmpresaFragment;
import com.example.appfetin.fragments.PerfilEmpresaFragment;
import com.example.appfetin.fragments.ProdutosEmpresaFragment;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Farma App-Estabelecimento");
        setSupportActionBar(toolbar);

        //configurações de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //configurar o bottomNavigationView
        configuraBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPagerEmpresa, new ProdutosEmpresaFragment()).commit();

    }

    private void configuraBottomNavigation(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationEmpresa);

        //faz as configurações iniciais
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);

        habilitarNavegacao(bottomNavigationViewEx);

    }

    @SuppressLint("NonConstantResourceId")
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(menuItem -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()){
                case R.id.menu_produto2:
                    fragmentTransaction.replace(R.id.viewPagerEmpresa, new ProdutosEmpresaFragment()).commit();
                    return true;
                case R.id.menu_pedidos:
                    fragmentTransaction.replace(R.id.viewPagerEmpresa, new PedidosEmpresaFragment()).commit();
                    return true;
                case R.id.menu_perfil2:
                    fragmentTransaction.replace(R.id.viewPagerEmpresa, new PerfilEmpresaFragment()).commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            case R.id.menu_perfil:
                startActivity(new Intent(getApplicationContext(), EditarEmpresaActivity.class));
                break;
            case R.id.menu_produto:
                startActivity(new Intent(EmpresaActivity.this, NovoProdutoActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
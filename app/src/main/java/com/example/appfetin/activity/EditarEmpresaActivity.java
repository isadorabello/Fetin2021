package com.example.appfetin.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfetin.R;
import com.example.appfetin.helper.ConfiguracaoFirebase;
import com.example.appfetin.helper.UsuarioFirebase;
import com.example.appfetin.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarEmpresaActivity extends AppCompatActivity {

    private EditText editNomeEmpresa, editEmailEmpresa, editCategoria, editTempo,
            editPreco, editNomeDono, editEnderecoEmpresa, editTelefoneCEmpresa, editTelefonePEmpresa;
    private CircleImageView imagePerfilEmpresa;
    private TextView textAlterarFoto;
    private Button buttonSalvar;
    private String idUsuarioEmpresa;

    private Empresa empresaLogada;
    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private final StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empresa);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        idUsuarioEmpresa = UsuarioFirebase.getIdentificadorUsuario();
        empresaLogada = UsuarioFirebase.getDadosEmpresaLogada();

        //recuperar os dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomeEmpresa.setText(usuarioPerfil.getDisplayName());
        editEmailEmpresa.setText(usuarioPerfil.getEmail());

        Uri uri = usuarioPerfil.getPhotoUrl();
        if (uri != null) {
            Glide.with(EditarEmpresaActivity.this).load(uri).into(imagePerfilEmpresa);
        }else{
            imagePerfilEmpresa.setImageResource(R.drawable.perfil);
        }

        recuperarDadosEspecificos();

        buttonSalvar.setOnClickListener(v -> {

            String nomeAtualizado = editNomeDono.getText().toString();
            String nomeEAtualizado = editNomeEmpresa.getText().toString();
            String telefonePAtualizado = editTelefonePEmpresa.getText().toString();
            String telefoneCAtualizado = editTelefoneCEmpresa.getText().toString();
            String enderecoAtualizado = editEnderecoEmpresa.getText().toString();
            String precoAtualizado = editPreco.getText().toString();
            String tempoAtualizado = editTempo.getText().toString();
            String categoriaAtualizada = editCategoria.getText().toString();

            Empresa empresa = UsuarioFirebase.getDadosEmpresaLogada();

            if (!nomeAtualizado.isEmpty()) {
                if (!nomeEAtualizado.isEmpty()) {
                    if (!telefonePAtualizado.isEmpty()) {
                        if (!telefoneCAtualizado.isEmpty()) {
                            if (!enderecoAtualizado.isEmpty()) {
                                if (!precoAtualizado.isEmpty()) {
                                    if (!tempoAtualizado.isEmpty()) {
                                        if (!categoriaAtualizada.isEmpty()) {

                                            //atualizar nome no perfil
                                            UsuarioFirebase.atulizarNomeEmpresa(nomeEAtualizado);

                                            //atualizar nome no banco de dados

                                            empresa.setNomeE(nomeEAtualizado);
                                            empresa.setNomeFiltro(nomeEAtualizado.toUpperCase());
                                            empresa.setPrecoEntrega(precoAtualizado);
                                            empresa.setTempoEntrega(tempoAtualizado);
                                            empresa.setCategoria(categoriaAtualizada);
                                            empresa.setNomeD(nomeAtualizado);
                                            empresa.setTelefoneP(telefonePAtualizado);
                                            empresa.setTelefoneC(telefoneCAtualizado);
                                            empresa.setEndereco(enderecoAtualizado);
                                            empresa.atualizarEmpresa();
                                            finish();

                                        } else {
                                            Toast.makeText(EditarEmpresaActivity.this, "Informe a categoria!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(EditarEmpresaActivity.this, "Informe o tempo de entrega!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(EditarEmpresaActivity.this, "Informe o a taxa de entrega!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EditarEmpresaActivity.this, "Informe o endereço!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditarEmpresaActivity.this, "Informe o telefone comercial!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditarEmpresaActivity.this, "Informe seu telefone pessoal!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarEmpresaActivity.this, "Informe o nome do estabelecimento!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditarEmpresaActivity.this, "Preencha o campo nome!", Toast.LENGTH_SHORT).show();
            }

        });

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bitmap imagem;

                        try {

                            //seleção apenas da galeria
                            assert data != null;
                            Uri localImagemSelecionada = data.getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                            //caso tenha sido escolhida uma imagem
                            if (imagem != null) {
                                imagePerfilEmpresa.setImageBitmap(imagem);
                            }

                            //recuperar dados da imagem no firebase
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            assert imagem != null;
                            imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] dadosImagem = baos.toByteArray();

                            //salvar imagem no firebase
                            final StorageReference imageRef = storageRef.child("imagens").child("perfil").child(idUsuarioEmpresa + ".jpeg");

                            UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                            uploadTask.addOnFailureListener(e -> Toast.makeText(this, "Erro no upload", Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(taskSnapshot -> {
                                    imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                    Uri url = task.getResult();
                                    atualizarFotoEmpresa(url);
                                });
                                Toast.makeText(this, "Sucesso no upload", Toast.LENGTH_SHORT).show();
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        textAlterarFoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager()) != null) {
                someActivityResultLauncher.launch(i);
            }
        });

    }

        private void atualizarFotoEmpresa (Uri url){
            //arualizar foto no perfil
            UsuarioFirebase.atulizarFotoUsuario(url);

            //atualizar foto no firebase
            empresaLogada.setUrlImagem(url.toString());
            empresaLogada.atualizarFotoEmpresa();

            Toast.makeText(this, "Sua foto foi atualizada", Toast.LENGTH_SHORT).show();
        }

        private void inicializarComponentes () {
            editNomeEmpresa = findViewById(R.id.editNomeEmpresa);
            editEmailEmpresa = findViewById(R.id.editEmailEmpresa);
            editEmailEmpresa.setFocusable(false);
            editCategoria = findViewById(R.id.editCategoriaEmpresa);
            editTempo = findViewById(R.id.editTempoEntrega);
            editPreco = findViewById(R.id.editPrecoEntrega);
            editNomeDono = findViewById(R.id.editNomeDono);
            editEnderecoEmpresa = findViewById(R.id.editEnderecoEmpresa);
            editTelefonePEmpresa = findViewById(R.id.editTelefonePEmpresa);
            editTelefoneCEmpresa = findViewById(R.id.editTelefoneCEmpresa);
            imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
            textAlterarFoto = findViewById(R.id.textAlterarFotoE);
            buttonSalvar = findViewById(R.id.buttonSalvarEmpresa);
        }

        @Override
        public boolean onSupportNavigateUp () {
            finish();
            return false;
        }

        private void recuperarDadosEspecificos () {
            DatabaseReference usuarioRef = firebaseRef.child("empresas").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    assert empresa != null;
                    editEnderecoEmpresa.setText(empresa.getEndereco());
                    editTelefonePEmpresa.setText(empresa.getTelefoneP());
                    editTelefoneCEmpresa.setText(empresa.getTelefoneC());
                    editNomeDono.setText(empresa.getNomeD());
                    editCategoria.setText(empresa.getCategoria());
                    editPreco.setText(empresa.getPrecoEntrega());
                    editTempo.setText(empresa.getTempoEntrega());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }
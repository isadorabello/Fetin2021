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
import com.example.appfetin.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarClienteActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editEndereco, editTelefoneP;
    private CircleImageView imagePerfil;
    private TextView textAlterarFoto;
    private Button buttonSalvar;
    private String idUsuario;

    private Usuario usuarioLogado;
    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private final StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //recuperar os dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNome.setText(usuarioPerfil.getDisplayName());
        editEmail.setText(usuarioPerfil.getEmail());

        Uri uri = usuarioPerfil.getPhotoUrl();
        if (uri != null) {
            Glide.with(EditarClienteActivity.this).load(uri).into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.perfil);
        }

        recuperarDadosCliente();

        buttonSalvar.setOnClickListener(v -> {

            String nomeAtualizado = editNome.getText().toString();
            String telefoneAtualizado = editTelefoneP.getText().toString();
            String enderecoAtualizado = editEndereco.getText().toString();

            Usuario usuarioCliente = UsuarioFirebase.getDadosUsuarioLogado();

            if(!nomeAtualizado.isEmpty()){
                if(!telefoneAtualizado.isEmpty()){
                    if(!enderecoAtualizado.isEmpty()){
                        //atualizar nome no perfil
                        UsuarioFirebase.atulizarNomeUsuario(nomeAtualizado);

                        //atualizar nome no banco de dados
                        usuarioCliente.setNome(nomeAtualizado);

                        usuarioCliente.setTelefonePessoal(telefoneAtualizado);
                        usuarioCliente.setEndereco(enderecoAtualizado);
                        usuarioCliente.atualizarCliente();
                        finish();
                    }else{
                        Toast.makeText(EditarClienteActivity.this, "Informe seu endereço!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(EditarClienteActivity.this, "Informe seu telefone!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(EditarClienteActivity.this, "Preencha o campo nome!", Toast.LENGTH_SHORT).show();
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
                                imagePerfil.setImageBitmap(imagem);
                            }

                            //recuperar dados da imagem no firebase
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            assert imagem != null;
                            imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] dadosImagem = baos.toByteArray();

                            //salvar imagem no firebase
                            final StorageReference imageRef = storageRef.child("imagens").child("perfil").child(idUsuario + ".jpeg");

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
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizarFotoCliente();

        Toast.makeText(this, "Sua foto foi atualizada", Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editNome = findViewById(R.id.editNomeCliente);
        editEmail = findViewById(R.id.editEmailCliente);
        editEmail.setFocusable(false);
        editEndereco = findViewById(R.id.editEnderecoCliente);
        editTelefoneP = findViewById(R.id.editTelefoneCliente);
        imagePerfil = findViewById(R.id.imagePerfilCliente);
        textAlterarFoto = findViewById(R.id.textAlterarFotoC);
        buttonSalvar = findViewById(R.id.buttonSalvarCliente);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void recuperarDadosCliente(){
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                assert usuario != null;
                editEndereco.setText(usuario.getEndereco());
                editTelefoneP.setText(usuario.getTelefonePessoal());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
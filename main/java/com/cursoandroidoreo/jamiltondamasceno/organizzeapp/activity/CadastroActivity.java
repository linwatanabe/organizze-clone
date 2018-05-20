package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.R;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config.ConfigFirebase;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.Base64Custom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btCadastrar;

    private FirebaseAuth mAuth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.et_nome);
        campoEmail = findViewById(R.id.et_email);
        campoSenha = findViewById(R.id.et_senha);

        btCadastrar = findViewById(R.id.bt_cadastrar);
        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if (!textoNome.isEmpty()){
                    if (!textoEmail.isEmpty()){
                        if (!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrarUsuario();

                        }else {
                            Toast.makeText(CadastroActivity.this, "Crie uma senha", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(CadastroActivity.this, "Digite seu email", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void cadastrarUsuario() {
        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    String senhaCripto = Base64Custom.codificarBase64(usuario.getSenha());
                    usuario.setSenha(senhaCripto);
                    usuario.salvar();
                    finish();

                }else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite email e senha válidos";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Usuário já cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
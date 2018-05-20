package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.R;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config.ConfigFirebase;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.Base64Custom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.DateCustom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model.Movimentacao;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private EditText campoValor;
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private Movimentacao movimentacao;

    private DatabaseReference mDatabase = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.et_valor);
        campoData = findViewById(R.id.et_data);
        campoCategoria = findViewById(R.id.et_categoria);
        campoDescricao = findViewById(R.id.et_descricao);

//        completar campoData com data atual
        campoData.setText(DateCustom.dataAtual());

//        recupera a receitaTotal do firebaseDatabase
        recuperarReceitaTotal();

    }

    public void salvarReceita(View view) {

        if (validarCamposReceita()){
            movimentacao = new Movimentacao();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            String data = campoData.getText().toString();

            movimentacao.setValor(valorRecuperado);
            movimentacao.setData(data);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setTipo("Receita");

//            salvar e atualizar valor da receitaTotal no firebaseDatabase
            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceitaTotal(receitaAtualizada);

//            salvar uma nova movimentação de receita no firebaseDatabase
            movimentacao.salvar(data);
            finish();
        }

    }

//    tratamento dos dados da receita
    public Boolean validarCamposReceita() {

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if (!textoValor.isEmpty()){
            if (!textoData.isEmpty()){
                if (!textoCategoria.isEmpty()){
                    if (!textoDescricao.isEmpty()){

                        return true;

                    }else {
                        Toast.makeText(this, "Descrição não foi preenchida!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Toast.makeText(this, "Categoria não foi preenchida!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(this, "Data não foi preenchida!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(this, "Valor não foi preenchido!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

//    recupera a receita total do firebaseDatabase
    public void recuperarReceitaTotal() {
        String emailUsuario = mAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference userDatabase = mDatabase.child("usuarios").child(idUsuario);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    atualiza a receita total no firebaseDatabase
    public void atualizarReceitaTotal(Double receita) {
        String emailUsuario = mAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference userDatabase = mDatabase.child("usuarios").child(idUsuario);

        userDatabase.child("receitaTotal").setValue(receita);
    }

}

package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.R;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config.ConfigFirebase;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.Base64Custom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference mDatabase = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference userDatabase;
    private ValueEventListener valueEventListenerUsuario;

    private TextView textoSaudacao, textoSaldo;
    private MaterialCalendarView calendarView;
    private RecyclerView recycler_movimentos;

    private String nome;
    private Double receitaTotal, despesaTotal, resumoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        textoSaudacao = findViewById(R.id.tv_welcome);
        textoSaldo = findViewById(R.id.tv_saldo);
        calendarView = findViewById(R.id.calendarView);
        recycler_movimentos = findViewById(R.id.recycler_movimentos);

        configCalendarView();

    }

    public void recuperarResumo () {

//        recuperar despesaTotal e receitaTotal do firebaseDatabase
        String emailUsuario = mAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        userDatabase = mDatabase.child("usuarios").child(idUsuario);

        valueEventListenerUsuario = userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                nome = usuario.getNome();
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                textoSaudacao.setText("Olá, " + nome);

//                formatar o valor do saldo para R$0,00
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);
                textoSaldo.setText("R$ " + resultadoFormatado);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("evento", "evento foi adicionado");

    }

    public void configCalendarView() {

//        configuração do calendário
        CharSequence meses[] = {
                "Janeiro",
                "Fevereiro",
                "Março",
                "Abril",
                "Maio",
                "Junho",
                "Julho",
                "Agosto",
                "Setembro",
                "Outubro",
                "Novembro",
                "Dezembro",
        };
        calendarView.setTitleMonths(meses);
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                Log.i("data", "mes/ano: " + (date.getMonth()+1) + "/" + date.getYear());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void btSair(View view) {
        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.signOut();
        finish();
    }

    public void btAddReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void btAddDespesa(View view) {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userDatabase.removeEventListener(valueEventListenerUsuario);
        Log.i("evento", "evento foi removido");
    }
}

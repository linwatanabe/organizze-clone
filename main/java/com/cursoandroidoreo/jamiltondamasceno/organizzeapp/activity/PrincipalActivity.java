package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.R;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.adapter.AdapterMovimentacao;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.config.ConfigFirebase;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper.Base64Custom;
import com.cursoandroidoreo.jamiltondamasceno.organizzeapp.model.Movimentacao;
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
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private TextView textoSaudacao, textoSaldo;
    private MaterialCalendarView calendarView;

    private String nome;
    private Double receitaTotal, despesaTotal, resumoUsuario;
    private Double valor;
    private String categoria, descricao;

    private FirebaseAuth mAuth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference mDatabase = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference userDatabase;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoDatabase;
    private String mesAnoSelecionado;

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
        recyclerView = findViewById(R.id.recycler_movimentos);

//        configurar Adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

//        configurar RecylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);

        configCalendarView();
        swipe();

    }

    public void swipe() {

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                ItemTouchHelper.ACTION_STATE_IDLE deixa o drag and drop inativo
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

//        configurar alertDialog
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Tem certeza que deseja excluir essa movimentação da sua conta?");
        alertDialog.setCancelable(false);

//        configurar botões do alertDialog
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position);

                String emailUsuario = mAuth.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsuario);

                movimentacaoDatabase = mDatabase.child("movimentacao").child(idUsuario).child(mesAnoSelecionado);
                movimentacaoDatabase.child(movimentacao.getKey()).removeValue();

                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();
                Toast.makeText(PrincipalActivity.this, "Excluido", Toast.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        alertDialog.create().show();

    }

    public void atualizarSaldo() {

        String emailUsuario = mAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        userDatabase = mDatabase.child("usuarios").child(idUsuario);

        if (movimentacao.getTipo().equals("receita")){
            receitaTotal = receitaTotal - movimentacao.getValor();
            userDatabase.child("receitaTotal").setValue(receitaTotal);
        }

        if (movimentacao.getTipo().equals("despesa")){
            despesaTotal = despesaTotal - movimentacao.getValor();
            userDatabase.child("despesaTotal").setValue(despesaTotal);
        }

    }

    public void recuperarMovimentacoes() {

        String emailUsuario = mAuth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        movimentacaoDatabase = mDatabase.child("movimentacao").child(idUsuario).child(mesAnoSelecionado);

        valueEventListenerMovimentacoes = movimentacaoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){

                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add(movimentacao);

                }
                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        CalendarDay dataAtual = calendarView.getCurrentDate();
//        formata o mês recuperado da dataAtual > "%02d"
//        % = criar formatação
//        0 = caracter substituto (pode ser qualquer caracter)
//        2 = número de caracteres na formatação (pode ser 2, 10, 50 caracteres)
//        d = indicação de dígitos
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth()+1));
        mesAnoSelecionado = String.valueOf(mesSelecionado + "" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth()+1));
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());

//                remove a última atualização e atualiza novamente a lista de movimentações do mês selecionado
                movimentacaoDatabase.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentacoes();
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
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userDatabase.removeEventListener(valueEventListenerUsuario);
        movimentacaoDatabase.removeEventListener(valueEventListenerMovimentacoes);
    }
}
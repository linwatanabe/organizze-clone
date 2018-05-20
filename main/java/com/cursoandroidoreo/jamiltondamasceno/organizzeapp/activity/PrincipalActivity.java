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
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView textoSaudacao, textoSaldo;
    private MaterialCalendarView calendarView;
    private RecyclerView recycler_movimentos;

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
        configCalendarView();

        recycler_movimentos = findViewById(R.id.recycler_movimentos);



//        configuração padrão do floatingActionButton
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
                mAuth = ConfigFirebase.getFirebaseAuth();
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btAddReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void btAddDespesa(View view) {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void btSair(View view) {
        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.signOut();
        finish();
    }

    public void configCalendarView() {

//        configurar limite do calendar
//        calendarView.state().edit()
//                .setMinimumDate(CalendarDay.from(2016, 1, 1))
//                .setMaximumDate(CalendarDay.from(2019, 11, 1))
//                .commit();

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

}

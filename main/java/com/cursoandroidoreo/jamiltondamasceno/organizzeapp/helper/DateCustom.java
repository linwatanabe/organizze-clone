package com.cursoandroidoreo.jamiltondamasceno.organizzeapp.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual() {
        long data = System.currentTimeMillis(); //recupera a data atual
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); //cria uma formatação para data
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String mesAnoDataEscolhida(String data) {

        String retornoData[] = data.split("/");
//        split quebra a String em partes no elemento selecionado "/"
//        a data  23/01/2018 ficaria:
        String dia = retornoData[0]; //dia 23
        String mes = retornoData[1]; //mes 01
        String ano = retornoData[2]; //ano 2018

        String mesAno = mes + ano; //012018
        return mesAno;
    }
}

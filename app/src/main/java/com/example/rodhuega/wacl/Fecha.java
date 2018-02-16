package com.example.rodhuega.wacl;

import java.io.Serializable;

/**
 * Created by pillo on 03/02/2018.
 */

public class Fecha implements Serializable {
    private int ano,dia,hora,minuto;

    public Fecha(int ano, int dia, int hora, int minuto) {
        this.ano=ano;
        this.dia=dia;
        this.hora=hora;
        this.minuto=minuto;
    }

    //gets
    public int getAno() {
        return ano;
    }

    public int getDia() {
        return dia;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    //sets

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}

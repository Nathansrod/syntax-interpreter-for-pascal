package com.syntatic;

public class Valor {
    private int valorInteiro;
    private String valorIdentificador;
    private double valorDecimal;
    
    public Valor(int valorInteiro) {
        this.valorInteiro = valorInteiro;
    }

    public Valor(double valorDecimal){
        this.valorDecimal = valorDecimal;
    }

    public Valor(String valorIdentificador){
        this.valorIdentificador = valorIdentificador;
    }

    public int getValorInteiro() {
        return valorInteiro;
    }
    public void setValorInteiro(int valorInteiro) {
        this.valorInteiro = valorInteiro;
    }
    public String getValorIdentificador() {
        return valorIdentificador;
    }
    public void setValorIdentificador(String valorIdentificador) {
        this.valorIdentificador = valorIdentificador;
    }
    public double getValorDecimal() {
        return valorDecimal;
    }
    public void setValorDecimal(double valorDecimal) {
        this.valorDecimal = valorDecimal;
    }

    @Override
    public String toString() {
        return "[valorInteiro=" + valorInteiro + ", valorIdentificador=" + valorIdentificador + ", valorDecimal="
                + valorDecimal + "]";
    }

    
    
    
}

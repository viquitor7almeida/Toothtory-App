package com.toothtory.domain.entities;

public class ResumoMensal {
    private final String mes;
    private final double receita;
    private final double gastos;
    private final double lucro;

    public ResumoMensal(String mes, double receita, double gastos, double lucro) {
        this.mes = mes;
        this.receita = receita;
        this.gastos = gastos;
        this.lucro = lucro;
    }

    public String getMes() { return mes; }
    public double getReceita() { return receita; }
    public double getGastos() { return gastos; }
    public double getLucro() { return lucro; }
}

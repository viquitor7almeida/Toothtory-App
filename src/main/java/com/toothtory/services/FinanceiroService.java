package com.toothtory.services;

import com.toothtory.domain.entities.ResumoMensal;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FinanceiroService {
    private final ConsultaService consultaService;
    private final GastoService gastoService;

    public FinanceiroService() {
        this.consultaService = new ConsultaService();
        this.gastoService = new GastoService();
    }

    public ResumoMensal resumoMensal(YearMonth mes) {
        double receita = consultaService.totalFaturadoPorMes(mes);
        double gastos = gastoService.totalGastoNoMes(mes);
        return new ResumoMensal(nomeMes(mes), receita, gastos, receita - gastos);
    }

    public List<ResumoMensal> relatorioAnual(int ano) {
        List<ResumoMensal> relatorio = new ArrayList<>();
        for (Month mes : Month.values()) {
            relatorio.add(resumoMensal(YearMonth.of(ano, mes)));
        }
        return relatorio;
    }

    public double lucroNoMes(YearMonth mes) {
        return consultaService.totalFaturadoPorMes(mes) - gastoService.totalGastoNoMes(mes);
    }

    private String nomeMes(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("pt", "BR"));
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }
}

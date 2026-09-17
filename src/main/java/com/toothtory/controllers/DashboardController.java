package com.toothtory.controllers;

import com.toothtory.services.ConsultaService;
import com.toothtory.services.FinanceiroService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.YearMonth;

public class DashboardController {
    @FXML private Label totalPacientesLabel;
    @FXML private Label totalProcedimentosLabel;
    @FXML private Label totalConsultasLabel;
    @FXML private Label faturamentoMesLabel;
    @FXML private Label lucroMesLabel;

    private final PacienteService pacienteService = new PacienteService();
    private final ProcedimentoService procedimentoService = new ProcedimentoService();
    private final ConsultaService consultaService = new ConsultaService();
    private final FinanceiroService financeiroService = new FinanceiroService();

    @FXML
    public void initialize() {
        atualizarIndicadores();
    }

    private void atualizarIndicadores() {
        totalPacientesLabel.setText(String.valueOf(pacienteService.listarTodos().size()));
        totalProcedimentosLabel.setText(String.valueOf(procedimentoService.listarTodos().size()));
        totalConsultasLabel.setText(String.valueOf(consultaService.listarTodas().size()));
        faturamentoMesLabel.setText(String.format("R$ %.2f", consultaService.totalFaturadoNoMes()));
        double lucro = financeiroService.lucroNoMes(YearMonth.now());
        lucroMesLabel.setText(String.format("R$ %.2f", lucro));
        lucroMesLabel.getStyleClass().setAll(lucro < 0 ? "card-valor-vermelho" : "card-valor-verde");
    }
}
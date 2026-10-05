package com.toothtory.controllers;

import com.toothtory.components.Calendario;
import com.toothtory.services.ConsultaService;
import com.toothtory.services.FinanceiroService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.time.YearMonth;

public class DashboardController {
    @FXML private Label totalPacientesLabel;
    @FXML private Label totalProcedimentosLabel;
    @FXML private Label totalConsultasLabel;
    @FXML private Label faturamentoMesLabel;
    @FXML private Label lucroMesLabel;
    @FXML private StackPane slotCalendario;

    private final PacienteService pacienteService = new PacienteService();
    private final ProcedimentoService procedimentoService = new ProcedimentoService();
    private final ConsultaService consultaService = new ConsultaService();
    private final FinanceiroService financeiroService = new FinanceiroService();

    @FXML
    public void initialize() {
        atualizarIndicadores();
        Calendario calendario = new Calendario();
        calendario.setMaxWidth(840);
        slotCalendario.setPadding(new Insets(24, 0, 0, 0));
        slotCalendario.getChildren().add(calendario);
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
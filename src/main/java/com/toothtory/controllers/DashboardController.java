package com.toothtory.controllers;

import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label totalPacientesLabel;
    @FXML private Label totalProcedimentosLabel;
    @FXML private Label totalConsultasLabel;
    @FXML private Label faturamentoMesLabel;

    private final PacienteService pacienteService = new PacienteService();
    private final ProcedimentoService procedimentoService = new ProcedimentoService();
    private final ConsultaService consultaService = new ConsultaService();

    @FXML
    public void initialize() {
        atualizarIndicadores();
    }

    private void atualizarIndicadores() {
        totalPacientesLabel.setText(String.valueOf(pacienteService.listarTodos().size()));
        totalProcedimentosLabel.setText(String.valueOf(procedimentoService.listarTodos().size()));
        totalConsultasLabel.setText(String.valueOf(consultaService.listarTodas().size()));
        faturamentoMesLabel.setText(String.format("R$ %.2f", consultaService.totalFaturadoNoMes()));
    }
}
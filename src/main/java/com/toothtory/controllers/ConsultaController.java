package com.toothtory.controllers;

import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.entities.Procedimento;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsultaController {
    @FXML private TableView<Consulta> tabelaConsultas;
    @FXML private TableColumn<Consulta, Long> colId;
    @FXML private TableColumn<Consulta, String> colPaciente;
    @FXML private TableColumn<Consulta, String> colProcedimento;
    @FXML private TableColumn<Consulta, String> colObservacoes;
    @FXML private TableColumn<Consulta, Double> colValor;
    @FXML private TableColumn<Consulta, LocalDateTime> colDataHora;
    @FXML private ComboBox<Paciente> comboPaciente;
    @FXML private ComboBox<Procedimento> comboProcedimento;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtHora;
    @FXML private TextField txtNomeProcedimentoManual;
    @FXML private TextField txtValorManual;
    @FXML private TextArea txtObservacoes;
    @FXML private RadioButton rbProcedimentoExistente;
    @FXML private RadioButton rbProcedimentoManual;
    @FXML private ToggleGroup grupoProcedimento;

    private final ConsultaService consultaService = new ConsultaService();
    private final PacienteService pacienteService = new PacienteService();
    private final ProcedimentoService procedimentoService = new ProcedimentoService();
    private ObservableList<Consulta> consultasList = FXCollections.observableArrayList();
    private ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private ObservableList<Procedimento> procedimentosList = FXCollections.observableArrayList();
    private Long idEditando = null;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarComboBoxes();
        carregarConsultas();
        configurarToggleGroup();
        configurarStringConverterPaciente();
        configurarStringConverterProcedimento();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colProcedimento.setCellValueFactory(new PropertyValueFactory<>("nomeProcedimento"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorProcedimento"));
        colObservacoes.setCellValueFactory(new PropertyValueFactory<>("observacoes"));

        colPaciente.setCellValueFactory(cellData -> {
            Long pacienteId = cellData.getValue().getPacienteId();
            Paciente p = pacienteService.findById(pacienteId).orElse(null);
            return new SimpleStringProperty(p == null ? "" : p.getNome());
        });

        tabelaConsultas.setItems(consultasList);
    }

    private void carregarComboBoxes() {
        pacientesList.setAll(pacienteService.listarTodos());
        comboPaciente.setItems(pacientesList);
        procedimentosList.setAll(procedimentoService.listarTodos());
        comboProcedimento.setItems(procedimentosList);
    }

    private void configurarToggleGroup() {
        grupoProcedimento = new ToggleGroup();
        rbProcedimentoExistente.setToggleGroup(grupoProcedimento);
        rbProcedimentoManual.setToggleGroup(grupoProcedimento);
        rbProcedimentoExistente.setSelected(true);
        comboProcedimento.setDisable(false);
        txtNomeProcedimentoManual.setDisable(true);
        txtValorManual.setDisable(true);

        grupoProcedimento.selectedToggleProperty().addListener((obs, old, novo) -> {
            boolean manual = novo == rbProcedimentoManual;
            comboProcedimento.setDisable(!manual);
            txtNomeProcedimentoManual.setDisable(!manual);
            txtValorManual.setDisable(!manual);
        });
    }

    private void configurarStringConverterPaciente() {
        comboPaciente.setConverter(new StringConverter<Paciente>() {
            @Override
            public String toString(Paciente paciente) {
                return paciente == null ? "" : paciente.getNome();
            }

            @Override
            public Paciente fromString(String string) {
                return null;
            }
        });
    }

    private void configurarStringConverterProcedimento() {
        comboProcedimento.setConverter(new StringConverter<Procedimento>() {
            @Override
            public String toString(Procedimento procedimento) {
                return procedimento == null ? "" : procedimento.getNome();
            }

            @Override
            public Procedimento fromString(String string) {
                return null;
            }
        });
    }

    private void carregarConsultas() {
        consultasList.setAll(consultaService.listarTodas());
    }

    @FXML
    private void salvarConsulta() {
        try {
            Paciente paciente = comboPaciente.getSelectionModel().getSelectedItem();
            if (paciente == null) throw new IllegalArgumentException("Selecione um paciente");

            if (datePicker.getValue() == null) throw new IllegalArgumentException("Selecione uma data");
            String horaStr = txtHora.getText();
            if (horaStr == null || horaStr.trim().isEmpty()) throw new IllegalArgumentException("Informe a hora no formato HH:mm");
            LocalDateTime dataHora = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm")));
            String observacoes = txtObservacoes.getText();

            if (idEditando != null) {
                Consulta c = consultaService.findById(idEditando).orElse(new Consulta());
                c.setPacienteId(paciente.getId());
                c.setDataHora(dataHora);
                c.setObservacoes(observacoes);
                if (rbProcedimentoExistente.isSelected()) {
                    Procedimento proc = comboProcedimento.getSelectionModel().getSelectedItem();
                    if (proc == null) throw new IllegalArgumentException("Selecione um procedimento");
                    c.setNomeProcedimento(proc.getNome());
                    c.setValorProcedimento(proc.getValor());
                } else {
                    String nomeProc = txtNomeProcedimentoManual.getText();
                    if (nomeProc == null || nomeProc.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do procedimento");
                    double valor = Double.parseDouble(txtValorManual.getText());
                    c.setNomeProcedimento(nomeProc);
                    c.setValorProcedimento(valor);
                }
                consultaService.salvar(c);
                idEditando = null;
            } else {
                if (rbProcedimentoExistente.isSelected()) {
                    Procedimento proc = comboProcedimento.getSelectionModel().getSelectedItem();
                    if (proc == null) throw new IllegalArgumentException("Selecione um procedimento");
                    consultaService.registrarConsultaComProcedimentoExistente(paciente.getId(), dataHora, proc.getId(), observacoes);
                } else {
                    String nomeProc = txtNomeProcedimentoManual.getText();
                    if (nomeProc == null || nomeProc.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do procedimento");
                    double valor = Double.parseDouble(txtValorManual.getText());
                    consultaService.registrarConsultaComProcedimentoManual(paciente.getId(), dataHora, nomeProc, valor, observacoes);
                }
            }
            limparFormulario();
            carregarConsultas();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Consulta registrada.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void limparFormulario() {
        comboPaciente.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        txtHora.clear();
        comboProcedimento.getSelectionModel().clearSelection();
        txtNomeProcedimentoManual.clear();
        txtValorManual.clear();
        txtObservacoes.clear();
        rbProcedimentoExistente.setSelected(true);
        idEditando = null;
    }

    @FXML
    private void editarConsulta() {
        Consulta sel = tabelaConsultas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma consulta.");
            return;
        }
        Paciente p = pacienteService.findById(sel.getPacienteId()).orElse(null);
        if (p != null) comboPaciente.getSelectionModel().select(p);
        datePicker.setValue(sel.getDataHora().toLocalDate());
        txtHora.setText(sel.getDataHora().toLocalTime().toString());
        txtObservacoes.setText(sel.getObservacoes());
        idEditando = sel.getId();

        boolean manual = true;
        for (Procedimento proc : procedimentosList) {
            if (proc.getNome().equals(sel.getNomeProcedimento()) && proc.getValor() == sel.getValorProcedimento()) {
                comboProcedimento.getSelectionModel().select(proc);
                manual = false;
                break;
            }
        }
        if (manual) {
            rbProcedimentoManual.setSelected(true);
            txtNomeProcedimentoManual.setText(sel.getNomeProcedimento());
            txtValorManual.setText(String.valueOf(sel.getValorProcedimento()));
        } else {
            rbProcedimentoExistente.setSelected(true);
        }
    }

    @FXML
    private void excluirConsulta() {
        Consulta sel = tabelaConsultas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma consulta.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Excluir consulta?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            consultaService.excluir(sel.getId());
            carregarConsultas();
            limparFormulario();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
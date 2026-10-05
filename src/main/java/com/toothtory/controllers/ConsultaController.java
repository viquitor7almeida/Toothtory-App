package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Calendario;
import com.toothtory.components.Notificacao;
import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.entities.Procedimento;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConsultaController implements Calendario.Acoes {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @FXML private StackPane slotCalendario;
    @FXML private ScrollPane scrollTela;
    @FXML private TitledPane painelNovaConsulta;
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
    private final ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private final ObservableList<Procedimento> procedimentosList = FXCollections.observableArrayList();

    private Calendario calendario;
    private Long idEditando = null;

    @FXML
    public void initialize() {
        carregarComboBoxes();
        configurarToggleGroup();
        configurarStringConverterPaciente();
        configurarStringConverterProcedimento();
        calendario = new Calendario(this);
        slotCalendario.getChildren().add(calendario);
    }

    @Override
    public void aoClicarDiaVazio(LocalDate dia) {
        String dataTexto = dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (Alerta.confirmar("Nova consulta", "Quer marcar uma nova consulta em " + dataTexto + "?")) {
            abrirFormularioNovoConsulta(dia);
        }
    }

    @Override
    public void aoAgendarDireto(LocalDate dia) {
        abrirFormularioNovoConsulta(dia);
    }

    @Override
    public void aoSelecionarConsulta(Consulta consulta) {
        carregarConsultaNoFormulario(consulta);
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
        atualizarCamposProcedimento(false);

        grupoProcedimento.selectedToggleProperty().addListener((obs, old, novo) -> {
            boolean manual = novo == rbProcedimentoManual;
            atualizarCamposProcedimento(manual);
        });
    }

    private void atualizarCamposProcedimento(boolean manual) {
        comboProcedimento.setDisable(manual);
        txtNomeProcedimentoManual.setDisable(!manual);
        txtValorManual.setDisable(!manual);
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

    private void abrirFormularioNovoConsulta(LocalDate data) {
        limparFormulario();
        datePicker.setValue(data);
        painelNovaConsulta.setExpanded(true);
        rolarAteFormulario();
        comboPaciente.requestFocus();
    }

    private void carregarConsultaNoFormulario(Consulta c) {
        Paciente p = pacienteService.findById(c.getPacienteId()).orElse(null);
        if (p != null) comboPaciente.getSelectionModel().select(p);
        datePicker.setValue(c.getDataHora().toLocalDate());
        txtHora.setText(c.getDataHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtObservacoes.setText(c.getObservacoes());
        idEditando = c.getId();

        boolean manual = true;
        for (Procedimento proc : procedimentosList) {
            if (proc.getNome().equals(c.getNomeProcedimento()) && proc.getValor() == c.getValorProcedimento()) {
                comboProcedimento.getSelectionModel().select(proc);
                manual = false;
                break;
            }
        }
        if (manual) {
            rbProcedimentoManual.setSelected(true);
            txtNomeProcedimentoManual.setText(c.getNomeProcedimento());
            txtValorManual.setText(String.format(PT_BR, "%.2f", c.getValorProcedimento()));
        } else {
            rbProcedimentoExistente.setSelected(true);
        }

        painelNovaConsulta.setExpanded(true);
        rolarAteFormulario();
    }

    private void rolarAteFormulario() {
        Platform.runLater(() -> scrollTela.setVvalue(1.0));
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
                    double valor = lerValor(txtValorManual.getText());
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
                    double valor = lerValor(txtValorManual.getText());
                    consultaService.registrarConsultaComProcedimentoManual(paciente.getId(), dataHora, nomeProc, valor, observacoes);
                }
            }
            limparFormulario();
            calendario.irParaMes(YearMonth.from(dataHora));
            Notificacao.sucesso("Consulta registrada.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
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
    private void excluirConsulta() {
        if (idEditando == null) {
            Notificacao.aviso("Clique em um dia com consulta e depois na consulta para carregá-la.");
            return;
        }
        if (Alerta.confirmar("Confirmar exclusão", "Excluir consulta?")) {
            consultaService.excluir(idEditando);
            idEditando = null;
            limparFormulario();
            calendario.atualizar();
        }
    }

    private double lerValor(String texto) {
        if (texto == null || texto.trim().isEmpty()) throw new IllegalArgumentException("Informe o valor do procedimento");
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor numérico válido");
        }
    }
}

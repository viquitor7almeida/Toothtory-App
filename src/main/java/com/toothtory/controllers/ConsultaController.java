package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import com.toothtory.services.ProcedimentoService;
import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.entities.Procedimento;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConsultaController {

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final String[] DIAS_SEMANA = {"DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB"};

    @FXML private Label labelMesAno;
    @FXML private StackPane areaCalendario;
    @FXML private GridPane gradeDias;
    @FXML private TitledPane painelNovaConsulta;
    @FXML private ScrollPane scrollTela;
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
    private final Map<LocalDate, List<Consulta>> consultasDoMes = new HashMap<>();
    private final PauseTransition atrasoFecharOverlay = new PauseTransition(Duration.millis(250));

    private YearMonth mesAtual = YearMonth.now();
    private VBox overlayAtual;
    private Long idEditando = null;

    @FXML
    public void initialize() {
        carregarComboBoxes();
        configurarToggleGroup();
        configurarStringConverterPaciente();
        configurarStringConverterProcedimento();
        configurarFechamentoOverlay();
        atualizarCalendario();
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

    private void atualizarCalendario() {
        List<Consulta> consultas = consultaService.buscarPorPeriodo(
                mesAtual.atDay(1).atStartOfDay(),
                mesAtual.atEndOfMonth().atTime(23, 59, 59));

        consultasDoMes.clear();
        for (Consulta c : consultas) {
            consultasDoMes.computeIfAbsent(c.getDataHora().toLocalDate(), k -> new ArrayList<>()).add(c);
        }
        for (List<Consulta> lista : consultasDoMes.values()) {
            lista.sort(Comparator.comparing(Consulta::getDataHora));
        }

        labelMesAno.setText(capitalizar(mesAtual.format(DateTimeFormatter.ofPattern("MMMM yyyy", PT_BR))));
        montarGrade();
    }

    private void montarGrade() {
        gradeDias.getChildren().clear();

        for (int i = 0; i < 7; i++) {
            Label diaSemana = new Label(DIAS_SEMANA[i]);
            diaSemana.getStyleClass().add("calendario-semana");
            diaSemana.setMaxWidth(Double.MAX_VALUE);
            diaSemana.setAlignment(Pos.CENTER);
            gradeDias.add(diaSemana, i, 0);
        }

        int deslocamento = mesAtual.atDay(1).getDayOfWeek().getValue() % 7;
        LocalDate primeiroDia = mesAtual.atDay(1).minusDays(deslocamento);

        for (int i = 0; i < 42; i++) {
            LocalDate data = primeiroDia.plusDays(i);
            gradeDias.add(criarCelula(data), i % 7, 1 + i / 7);
        }
    }

    private VBox criarCelula(LocalDate data) {
        VBox celula = new VBox();
        celula.getStyleClass().add("calendario-dia");

        Label numero = new Label(String.valueOf(data.getDayOfMonth()));
        numero.getStyleClass().add("calendario-dia-numero");
        if (data.equals(LocalDate.now())) {
            numero.getStyleClass().add("calendario-hoje-numero");
        }
        celula.getChildren().add(numero);

        boolean temConsultas = consultasDoMes.containsKey(data);
        if (temConsultas) {
            Region espacador = new Region();
            VBox.setVgrow(espacador, Priority.ALWAYS);
            Region bolinha = new Region();
            bolinha.getStyleClass().add("calendario-bolinha");
            bolinha.setMinSize(6, 6);
            bolinha.setMaxSize(6, 6);
            HBox linhaBolinha = new HBox();
            linhaBolinha.setAlignment(Pos.CENTER);
            linhaBolinha.getChildren().add(bolinha);
            celula.getChildren().addAll(espacador, linhaBolinha);
        }

        if (data.getMonth() != mesAtual.getMonth()) {
            celula.getStyleClass().add("calendario-dia-outro-mes");
            return celula;
        }

        celula.setOnMouseClicked(evento -> {
            evento.consume();
            aoClicarDia(data, temConsultas, celula);
        });
        return celula;
    }

    private void aoClicarDia(LocalDate data, boolean temConsultas, Node celula) {
        fecharOverlay();

        if (!temConsultas) {
            String dataTexto = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (Alerta.confirmar("Nova consulta", "Quer marcar uma nova consulta em " + dataTexto + "?")) {
                abrirFormularioNovoConsulta(data);
            }
            return;
        }

        mostrarOverlayDia(data, celula);
    }

    @FXML
    private void mesAnterior() {
        mesAtual = mesAtual.minusMonths(1);
        fecharOverlay();
        atualizarCalendario();
    }

    @FXML
    private void mesSeguinte() {
        mesAtual = mesAtual.plusMonths(1);
        fecharOverlay();
        atualizarCalendario();
    }

    @FXML
    private void irParaHoje() {
        mesAtual = YearMonth.now();
        fecharOverlay();
        atualizarCalendario();
    }

    private void abrirFormularioNovoConsulta(LocalDate data) {
        limparFormulario();
        datePicker.setValue(data);
        painelNovaConsulta.setExpanded(true);
        rolarAteFormulario();
        comboPaciente.requestFocus();
    }

    private void mostrarOverlayDia(LocalDate dia, Node celula) {
        fecharOverlay();

        VBox overlay = new VBox();
        overlay.getStyleClass().add("calendario-hover");
        overlay.setManaged(false);
        overlay.setMinWidth(320);
        overlay.setPrefWidth(320);
        overlay.setMaxWidth(320);

        Label titulo = new Label("Consultas de " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        titulo.getStyleClass().add("calendario-hover-titulo");
        titulo.setMaxWidth(Double.MAX_VALUE);
        overlay.getChildren().add(titulo);

        for (Consulta c : consultasDoMes.getOrDefault(dia, List.of())) {
            VBox item = new VBox();
            item.getStyleClass().add("calendario-hover-item");

            Label linha1 = new Label(c.getDataHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " · " + nomePaciente(c.getPacienteId()));
            linha1.getStyleClass().add("calendario-hover-item-linha1");

            Label linha2 = new Label(c.getNomeProcedimento() + " · " + FORMATO_MOEDA.format(c.getValorProcedimento()));
            linha2.getStyleClass().add("calendario-hover-item-linha2");

            item.getChildren().addAll(linha1, linha2);
            item.setOnMouseClicked(evento -> {
                evento.consume();
                fecharOverlay();
                carregarConsultaNoFormulario(c);
            });
            overlay.getChildren().add(item);
        }

        Button nova = new Button("Nova consulta neste dia");
        nova.getStyleClass().add("button-primario");
        nova.setMaxWidth(Double.MAX_VALUE);
        nova.setOnAction(evento -> {
            fecharOverlay();
            abrirFormularioNovoConsulta(dia);
        });
        overlay.getChildren().add(nova);

        overlay.setOnMouseEntered(evento -> atrasoFecharOverlay.stop());
        overlay.setOnMouseExited(evento -> atrasoFecharOverlay.playFromStart());
        overlay.setOnMouseClicked(evento -> evento.consume());

        areaCalendario.getChildren().add(overlay);
        overlay.applyCss();
        overlay.autosize();
        overlay.layout();

        Bounds limitesCelula = celula.localToScene(celula.getBoundsInLocal());
        Bounds limitesArea = areaCalendario.localToScene(areaCalendario.getBoundsInLocal());

        double x = limitesCelula.getMaxX() - limitesArea.getMinX() + 8;
        double y = limitesCelula.getMinY() - limitesArea.getMinY();

        if (x + overlay.getWidth() > areaCalendario.getWidth() - 6) {
            x = limitesCelula.getMinX() - limitesArea.getMinX() - overlay.getWidth() - 8;
        }
        if (y + overlay.getHeight() > areaCalendario.getHeight() - 6) {
            y = areaCalendario.getHeight() - overlay.getHeight() - 6;
        }
        if (x < 6) x = 6;
        if (y < 6) y = 6;

        overlay.setLayoutX(x);
        overlay.setLayoutY(y);
        overlayAtual = overlay;
    }

    private void configurarFechamentoOverlay() {
        atrasoFecharOverlay.setOnFinished(evento -> fecharOverlay());
        areaCalendario.addEventHandler(MouseEvent.MOUSE_CLICKED, evento -> fecharOverlay());
    }

    private void fecharOverlay() {
        atrasoFecharOverlay.stop();
        if (overlayAtual != null) {
            areaCalendario.getChildren().remove(overlayAtual);
            overlayAtual = null;
        }
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
            fecharOverlay();
            if (!YearMonth.from(dataHora).equals(mesAtual)) {
                mesAtual = YearMonth.from(dataHora);
            }
            atualizarCalendario();
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
            fecharOverlay();
            atualizarCalendario();
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

    private String nomePaciente(Long pacienteId) {
        Paciente p = pacienteService.findById(pacienteId).orElse(null);
        return p == null ? "Paciente removido" : p.getNome();
    }

    private String capitalizar(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}

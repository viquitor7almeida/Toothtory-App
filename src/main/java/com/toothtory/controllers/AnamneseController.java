package com.toothtory.controllers;

import com.toothtory.components.Notificacao;
import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.services.AnamneseService;
import com.toothtory.services.PacienteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnamneseController {

    private static final String HINT_SELECAO = "Selecione um paciente na tabela acima";

    @FXML private ScrollPane scrollTela;
    @FXML private VBox telaAnamnese;
    @FXML private TextField txtBusca;
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colCpf;
    @FXML private TableColumn<Paciente, String> colAnamnese;
    @FXML private TitledPane painelAnamnese;
    @FXML private Label labelPaciente;
    @FXML private RadioButton rbDiabetesSim;
    @FXML private RadioButton rbDiabetesNao;
    @FXML private RadioButton rbHipertensoSim;
    @FXML private RadioButton rbHipertensoNao;
    @FXML private RadioButton rbRimPancreasSim;
    @FXML private RadioButton rbRimPancreasNao;
    @FXML private RadioButton rbPulmonaresSim;
    @FXML private RadioButton rbPulmonaresNao;
    @FXML private TextArea txtObservacoes;

    private final PacienteService pacienteService = new PacienteService();
    private final AnamneseService anamneseService = new AnamneseService();
    private final ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private final Set<Long> pacientesComAnamnese = new HashSet<>();
    private final ToggleGroup grupoDiabetes = new ToggleGroup();
    private final ToggleGroup grupoHipertenso = new ToggleGroup();
    private final ToggleGroup grupoRimPancreas = new ToggleGroup();
    private final ToggleGroup grupoPulmonares = new ToggleGroup();
    private Paciente pacienteSelecionado = null;

    @FXML
    public void initialize() {
        configurarScrollTela();
        configurarColunas();
        tabelaPacientes.setPlaceholder(rotuloVazio("Nenhum paciente cadastrado"));
        carregarPacientes();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscarPacientes());
        tabelaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> aoSelecionarPaciente(novo));
        definirFormularioAtivo(false);
    }

    private void configurarScrollTela() {
        scrollTela.viewportBoundsProperty().addListener((obs, antigo, limites) -> {
            if (limites != null) {
                telaAnamnese.setMinHeight(limites.getHeight());
            }
        });
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        formatarColunaAnamnese(colAnamnese);

        colNome.prefWidthProperty().bind(tabelaPacientes.widthProperty()
                .subtract(20)
                .subtract(colCpf.prefWidthProperty())
                .subtract(colAnamnese.prefWidthProperty()));
        colNome.setResizable(false);

        tabelaPacientes.setItems(pacientesList);
    }

    private void formatarColunaAnamnese(TableColumn<Paciente, String> coluna) {
        coluna.setCellValueFactory(cellData -> new SimpleStringProperty(
                pacientesComAnamnese.contains(cellData.getValue().getId()) ? "OK" : "Pendente"));
        coluna.setCellFactory(c -> new TableCell<Paciente, String>() {
            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio || item == null ? "" : item);
                getStyleClass().removeAll("anamnese-ok", "anamnese-pendente");
                if (!vazio && item != null) {
                    getStyleClass().add("OK".equals(item) ? "anamnese-ok" : "anamnese-pendente");
                }
            }
        });
    }

    private void carregarPacientes() {
        atualizarLista(pacienteService.listarTodos());
    }

    @FXML
    private void buscarPacientes() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarPacientes();
        } else {
            atualizarLista(pacienteService.buscarPorNome(termo));
        }
    }

    private void atualizarLista(List<Paciente> pacientes) {
        pacientesComAnamnese.clear();
        pacientesComAnamnese.addAll(anamneseService.pacientesComAnamnese());
        pacientesList.setAll(pacientes);
    }

    private void aoSelecionarPaciente(Paciente paciente) {
        if (paciente == null) {
            return;
        }
        pacienteSelecionado = paciente;
        painelAnamnese.setText("Anamnese — " + paciente.getNome());
        labelPaciente.setText(paciente.getNome());

        Anamnese anamnese = anamneseService.buscarPorPaciente(paciente.getId()).orElse(null);
        if (anamnese != null) {
            marcarResposta(rbDiabetesSim, rbDiabetesNao, anamnese.isDiabetes());
            marcarResposta(rbHipertensoSim, rbHipertensoNao, anamnese.isHipertenso());
            marcarResposta(rbRimPancreasSim, rbRimPancreasNao, anamnese.isProblemasRimPancreas());
            marcarResposta(rbPulmonaresSim, rbPulmonaresNao, anamnese.isProblemasPulmonares());
            txtObservacoes.setText(anamnese.getObservacoes());
        } else {
            marcarPadraoNao();
            txtObservacoes.clear();
        }

        definirFormularioAtivo(true);
        painelAnamnese.setExpanded(true);
    }

    @FXML
    private void salvarAnamnese() {
        if (pacienteSelecionado == null) {
            Notificacao.aviso("Selecione um paciente.");
            return;
        }
        try {
            Anamnese anamnese = new Anamnese();
            anamnese.setPacienteId(pacienteSelecionado.getId());
            anamnese.setDiabetes(rbDiabetesSim.isSelected());
            anamnese.setHipertenso(rbHipertensoSim.isSelected());
            anamnese.setProblemasRimPancreas(rbRimPancreasSim.isSelected());
            anamnese.setProblemasPulmonares(rbPulmonaresSim.isSelected());
            anamnese.setObservacoes(txtObservacoes.getText());
            anamneseService.salvar(anamnese);
            carregarPacientes();
            tabelaPacientes.getSelectionModel().select(pacienteSelecionado);
            Notificacao.sucesso("Anamnese salva.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
        }
    }

    @FXML
    private void limparFormulario() {
        pacienteSelecionado = null;
        tabelaPacientes.getSelectionModel().clearSelection();
        marcarPadraoNao();
        txtObservacoes.clear();
        painelAnamnese.setText("Anamnese");
        labelPaciente.setText(HINT_SELECAO);
        definirFormularioAtivo(false);
        painelAnamnese.setExpanded(false);
    }

    private void marcarPadraoNao() {
        rbDiabetesNao.setSelected(true);
        rbHipertensoNao.setSelected(true);
        rbRimPancreasNao.setSelected(true);
        rbPulmonaresNao.setSelected(true);
    }

    private void marcarResposta(RadioButton sim, RadioButton nao, boolean valor) {
        (valor ? sim : nao).setSelected(true);
    }

    private void definirFormularioAtivo(boolean ativo) {
        painelAnamnese.setDisable(!ativo);
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}

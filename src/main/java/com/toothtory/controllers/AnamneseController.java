package com.toothtory.controllers;

import com.toothtory.components.Notificacao;
import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.services.AnamneseService;
import com.toothtory.services.PacienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AnamneseController {

    private static final String HINT_SELECAO = "Selecione um paciente na lista acima";

    @FXML private ScrollPane scrollTela;
    @FXML private VBox telaAnamnese;
    @FXML private TextField txtBusca;
    @FXML private ListView<Paciente> listaPacientes;
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
    private final ToggleGroup grupoDiabetes = new ToggleGroup();
    private final ToggleGroup grupoHipertenso = new ToggleGroup();
    private final ToggleGroup grupoRimPancreas = new ToggleGroup();
    private final ToggleGroup grupoPulmonares = new ToggleGroup();
    private Paciente pacienteSelecionado = null;

    @FXML
    public void initialize() {
        configurarScrollTela();
        configurarLista();
        configurarRadios();
        carregarPacientes();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscarPacientes());
        listaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> aoSelecionarPaciente(novo));
        definirFormularioAtivo(false);
    }

    private void configurarScrollTela() {
        scrollTela.viewportBoundsProperty().addListener((obs, antigo, limites) -> {
            if (limites != null) {
                telaAnamnese.setMinHeight(limites.getHeight());
            }
        });
    }

    private void configurarRadios() {
        rbDiabetesSim.setToggleGroup(grupoDiabetes);
        rbDiabetesNao.setToggleGroup(grupoDiabetes);
        rbHipertensoSim.setToggleGroup(grupoHipertenso);
        rbHipertensoNao.setToggleGroup(grupoHipertenso);
        rbRimPancreasSim.setToggleGroup(grupoRimPancreas);
        rbRimPancreasNao.setToggleGroup(grupoRimPancreas);
        rbPulmonaresSim.setToggleGroup(grupoPulmonares);
        rbPulmonaresNao.setToggleGroup(grupoPulmonares);
    }

    private void configurarLista() {
        listaPacientes.setItems(pacientesList);
        listaPacientes.setCellFactory(lista -> new ListCell<Paciente>() {
            private final HBox linha = new HBox(8);
            private final Label nome = new Label();
            private final Label cpf = new Label();

            {
                nome.getStyleClass().add("lista-item-nome");
                cpf.getStyleClass().add("lista-item-cpf");
                linha.getChildren().addAll(nome, cpf);
            }

            @Override
            protected void updateItem(Paciente paciente, boolean vazio) {
                super.updateItem(paciente, vazio);
                if (vazio || paciente == null) {
                    setGraphic(null);
                } else {
                    nome.setText(paciente.getNome());
                    cpf.setText(paciente.getCpf() == null || paciente.getCpf().trim().isEmpty()
                            ? "CPF não informado" : "CPF " + paciente.getCpf());
                    setGraphic(linha);
                }
            }
        });
    }

    private void carregarPacientes() {
        pacientesList.setAll(pacienteService.listarTodos());
    }

    @FXML
    private void buscarPacientes() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarPacientes();
        } else {
            pacientesList.setAll(pacienteService.buscarPorNome(termo));
        }
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
            Notificacao.sucesso("Anamnese salva.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
        }
    }

    @FXML
    private void limparFormulario() {
        pacienteSelecionado = null;
        listaPacientes.getSelectionModel().clearSelection();
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
}

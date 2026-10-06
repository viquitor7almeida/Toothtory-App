package com.toothtory.controllers;

import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.services.AnamneseService;
import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class FichaController {

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String HINT_SELECAO = "Clique em um paciente acima para ver a ficha";

    @FXML private ScrollPane scrollTela;
    @FXML private VBox telaFicha;
    @FXML private TextField txtBusca;
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colCpf;
    @FXML private TableColumn<Paciente, String> colCelular;
    @FXML private TitledPane painelFicha;
    @FXML private Label labelPaciente;
    @FXML private VBox containerFicha;

    private final PacienteService pacienteService = new PacienteService();
    private final AnamneseService anamneseService = new AnamneseService();
    private final ConsultaService consultaService = new ConsultaService();
    private final ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarScrollTela();
        configurarColunas();
        tabelaPacientes.setPlaceholder(rotuloVazio("Nenhum paciente cadastrado"));
        carregarPacientes();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscarPacientes());
        tabelaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                aoSelecionarPaciente(novo);
            }
        });
        definirFichaAtiva(false);
    }

    private void configurarScrollTela() {
        scrollTela.viewportBoundsProperty().addListener((obs, antigo, limites) -> {
            if (limites != null) {
                telaFicha.setMinHeight(limites.getHeight());
            }
        });
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));

        colNome.prefWidthProperty().bind(tabelaPacientes.widthProperty()
                .subtract(20)
                .subtract(colCpf.prefWidthProperty())
                .subtract(colCelular.prefWidthProperty()));
        colNome.setResizable(false);

        tabelaPacientes.setItems(pacientesList);
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
        painelFicha.setText("Ficha — " + paciente.getNome());
        labelPaciente.setText(paciente.getNome());
        containerFicha.getChildren().setAll(
                montarAnamnese(paciente.getId()),
                montarConsultas(paciente.getId()));
        definirFichaAtiva(true);
        painelFicha.setExpanded(true);
    }

    private void definirFichaAtiva(boolean ativa) {
        painelFicha.setDisable(!ativa);
        if (!ativa) {
            painelFicha.setText("Ficha");
            labelPaciente.setText(HINT_SELECAO);
            containerFicha.getChildren().clear();
        }
    }

    private VBox montarAnamnese(Long pacienteId) {
        VBox card = new VBox(8);
        card.getStyleClass().add("ficha-anamnese");

        Label titulo = new Label("Anamnese");
        titulo.getStyleClass().add("ficha-secao");
        card.getChildren().add(titulo);

        Optional<Anamnese> anamnese = anamneseService.buscarPorPaciente(pacienteId);
        if (anamnese.isEmpty()) {
            Label vazio = new Label("Sem anamnese registrada");
            vazio.getStyleClass().add("ficha-vazio");
            card.getChildren().add(vazio);
            return card;
        }

        GridPane respostas = new GridPane();
        respostas.setHgap(16);
        respostas.setVgap(4);
        adicionarResposta(respostas, 0, 0, "Diabetes", anamnese.get().isDiabetes());
        adicionarResposta(respostas, 1, 0, "Hipertensão", anamnese.get().isHipertenso());
        adicionarResposta(respostas, 0, 1, "Rim/Pâncreas", anamnese.get().isProblemasRimPancreas());
        adicionarResposta(respostas, 1, 1, "Pulmonares", anamnese.get().isProblemasPulmonares());
        card.getChildren().add(respostas);

        if (anamnese.get().getObservacoes() != null && !anamnese.get().getObservacoes().trim().isEmpty()) {
            Label obs = new Label("Obs: " + anamnese.get().getObservacoes());
            obs.getStyleClass().add("ficha-vazio");
            obs.setWrapText(true);
            card.getChildren().add(obs);
        }
        return card;
    }

    private void adicionarResposta(GridPane respostas, int coluna, int linha, String rotulo, boolean valor) {
        Label pergunta = new Label(rotulo + ":");
        pergunta.getStyleClass().add("ficha-anamnese-rotulo");
        Label resposta = new Label(valor ? "Sim" : "Não");
        resposta.getStyleClass().add(valor ? "ficha-anamnese-sim" : "ficha-anamnese-nao");
        HBox item = new HBox(6, pergunta, resposta);
        respostas.add(item, coluna, linha);
    }

    private VBox montarConsultas(Long pacienteId) {
        VBox card = new VBox(8);
        card.getStyleClass().add("ficha-consultas");

        Label titulo = new Label("Consultas");
        titulo.getStyleClass().add("ficha-secao");
        card.getChildren().add(titulo);

        List<Consulta> consultas = consultaService.buscarPorPaciente(pacienteId);
        if (consultas.isEmpty()) {
            Label vazio = new Label("Nenhuma consulta registrada");
            vazio.getStyleClass().add("ficha-vazio");
            card.getChildren().add(vazio);
            return card;
        }

        VBox lista = new VBox(6);
        double total = 0;
        for (Consulta c : consultas) {
            total += c.getValorProcedimento();
            lista.getChildren().add(montarItemConsulta(c));
        }

        ScrollPane scroll = new ScrollPane(lista);
        scroll.getStyleClass().add("ficha-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(Math.min(300, 70 + consultas.size() * 62));
        card.getChildren().add(scroll);

        String palavra = consultas.size() == 1 ? "consulta" : "consultas";
        Label rodape = new Label(consultas.size() + " " + palavra + " · Total " + FORMATO_MOEDA.format(total));
        rodape.getStyleClass().add("ficha-total");
        card.getChildren().add(rodape);
        return card;
    }

    private VBox montarItemConsulta(Consulta c) {
        VBox item = new VBox(2);
        item.getStyleClass().add("ficha-consulta-item");

        Label linha1 = new Label(c.getDataHora().format(FORMATO_DATA_HORA) + " · " + c.getNomeProcedimento());
        linha1.getStyleClass().add("ficha-consulta-linha1");

        Label linha2 = new Label(FORMATO_MOEDA.format(c.getValorProcedimento()));
        linha2.getStyleClass().add("ficha-consulta-linha2");

        item.getChildren().addAll(linha1, linha2);
        if (c.getObservacoes() != null && !c.getObservacoes().trim().isEmpty()) {
            Label obs = new Label(c.getObservacoes());
            obs.getStyleClass().add("ficha-vazio");
            obs.setWrapText(true);
            item.getChildren().add(obs);
        }
        return item;
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}

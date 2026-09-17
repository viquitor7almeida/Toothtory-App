package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.services.PacienteService;
import com.toothtory.domain.entities.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PacienteController {
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, Long> colId;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colEmail;
    @FXML private TableColumn<Paciente, String> colCelular;
    @FXML private TextField txtBusca;
    @FXML private TextField txtNome;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCelular;

    private final PacienteService service = new PacienteService();
    private ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private Long idEditando = null;

    @FXML
    public void initialize() {
        configurarColunas();
        tabelaPacientes.setPlaceholder(rotuloVazio("Nenhum paciente cadastrado"));
        carregarPacientes();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscarPacientes());
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));
        tabelaPacientes.setItems(pacientesList);
    }

    private void carregarPacientes() {
        pacientesList.setAll(service.listarTodos());
    }

    @FXML
    private void buscarPacientes() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarPacientes();
        } else {
            pacientesList.setAll(service.buscarPorNome(termo));
        }
    }

    @FXML
    private void limparFormulario() {
        txtNome.clear();
        txtEndereco.clear();
        txtEmail.clear();
        txtCelular.clear();
        idEditando = null;
    }

    @FXML
    private void salvarPaciente() {
        try {
            Paciente p;
            if (idEditando != null) {
                p = service.findById(idEditando).orElse(new Paciente());
                idEditando = null;
            } else {
                p = new Paciente();
            }
            p.setNome(txtNome.getText());
            p.setEndereco(txtEndereco.getText());
            p.setEmail(txtEmail.getText());
            p.setCelular(txtCelular.getText());
            service.salvar(p);
            limparFormulario();
            carregarPacientes();
            Notificacao.sucesso("Paciente salvo com sucesso.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
        }
    }

    @FXML
    private void editarPaciente() {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Notificacao.aviso("Selecione um paciente para editar.");
            return;
        }
        txtNome.setText(selecionado.getNome());
        txtEndereco.setText(selecionado.getEndereco());
        txtEmail.setText(selecionado.getEmail());
        txtCelular.setText(selecionado.getCelular());
        idEditando = selecionado.getId();
    }

    @FXML
    private void excluirPaciente() {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Notificacao.aviso("Selecione um paciente para excluir.");
            return;
        }
        if (Alerta.confirmar("Confirmar exclusão", "Deseja realmente excluir " + selecionado.getNome() + "?")) {
            service.excluir(selecionado.getId());
            carregarPacientes();
            limparFormulario();
            Notificacao.sucesso("Paciente excluído.");
        }
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}
package com.toothtory.controllers;

import com.toothtory.services.PacienteService;
import com.toothtory.domain.entities.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PacienteController {
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, String> colId;
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

    @FXML
    public void initialize() {
        configurarColunas();
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
    }

    @FXML
    private void salvarPaciente() {
        try {
            Paciente p = new Paciente();
            p.setNome(txtNome.getText());
            p.setEndereco(txtEndereco.getText());
            p.setEmail(txtEmail.getText());
            p.setCelular(txtCelular.getText());
            service.salvar(p);
            limparFormulario();
            carregarPacientes();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Paciente salvo com sucesso.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void editarPaciente() {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um paciente para editar.");
            return;
        }
        txtNome.setText(selecionado.getNome());
        txtEndereco.setText(selecionado.getEndereco());
        txtEmail.setText(selecionado.getEmail());
        txtCelular.setText(selecionado.getCelular());
        // Para atualizar, precisamos manter o id. Vamos usar um campo oculto ou salvar com id existente.
        // Simples: ao salvar, se o paciente já existe, o serviço atualiza (baseado em id não nulo)
        selecionado.setNome(txtNome.getText());
        selecionado.setEndereco(txtEndereco.getText());
        selecionado.setEmail(txtEmail.getText());
        selecionado.setCelular(txtCelular.getText());
        try {
            service.salvar(selecionado);
            limparFormulario();
            carregarPacientes();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Paciente atualizado.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void excluirPaciente() {
        Paciente selecionado = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um paciente para excluir.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            service.excluir(selecionado.getId());
            carregarPacientes();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Paciente excluído.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
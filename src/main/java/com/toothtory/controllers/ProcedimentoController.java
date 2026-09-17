package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.services.ProcedimentoService;
import com.toothtory.domain.entities.Procedimento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProcedimentoController {
    @FXML private TableView<Procedimento> tabelaProcedimentos;
    @FXML private TableColumn<Procedimento, Long> colId;
    @FXML private TableColumn<Procedimento, String> colNome;
    @FXML private TableColumn<Procedimento, Double> colValor;
    @FXML private TextField txtBusca;
    @FXML private TextField txtNome;
    @FXML private TextField txtValor;

    private final ProcedimentoService service = new ProcedimentoService();
    private ObservableList<Procedimento> lista = FXCollections.observableArrayList();
    private Long idEditando = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        tabelaProcedimentos.setItems(lista);
        tabelaProcedimentos.setPlaceholder(rotuloVazio("Nenhum procedimento cadastrado"));
        carregarProcedimentos();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscar());
    }

    private void carregarProcedimentos() {
        lista.setAll(service.listarTodos());
    }

    @FXML
    private void buscar() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            carregarProcedimentos();
        } else {
            lista.setAll(service.buscarPorNome(termo));
        }
    }

    @FXML
    private void limpar() {
        txtNome.clear();
        txtValor.clear();
        idEditando = null;
    }

    @FXML
    private void salvar() {
        try {
            Procedimento p;
            if (idEditando != null) {
                p = service.findById(idEditando).orElse(new Procedimento());
                idEditando = null;
            } else {
                p = new Procedimento();
            }
            p.setNome(txtNome.getText());
            p.setValor(Double.parseDouble(txtValor.getText()));
            service.salvar(p);
            limpar();
            carregarProcedimentos();
            Notificacao.sucesso("Procedimento salvo.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Procedimento sel = tabelaProcedimentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Notificacao.aviso("Selecione um procedimento.");
            return;
        }
        txtNome.setText(sel.getNome());
        txtValor.setText(String.valueOf(sel.getValor()));
        idEditando = sel.getId();
    }

    @FXML
    private void excluir() {
        Procedimento sel = tabelaProcedimentos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Notificacao.aviso("Selecione um procedimento.");
            return;
        }
        if (Alerta.confirmar("Confirmar exclusão", "Excluir " + sel.getNome() + "?")) {
            service.excluir(sel.getId());
            carregarProcedimentos();
            limpar();
        }
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}
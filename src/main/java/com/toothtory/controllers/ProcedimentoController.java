package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.services.ProcedimentoService;
import com.toothtory.domain.entities.Procedimento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class ProcedimentoController {
    @FXML private ScrollPane scrollTela;
    @FXML private VBox telaProcedimentos;
    @FXML private TableView<Procedimento> tabelaProcedimentos;
    @FXML private TableColumn<Procedimento, String> colNome;
    @FXML private TableColumn<Procedimento, Double> colValor;
    @FXML private TextField txtBusca;
    @FXML private TextField txtNome;
    @FXML private TextField txtValor;

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(PT_BR);

    private final ProcedimentoService service = new ProcedimentoService();
    private ObservableList<Procedimento> lista = FXCollections.observableArrayList();
    private Long idEditando = null;

    @FXML
    public void initialize() {
        configurarScrollTela();
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        colNome.prefWidthProperty().bind(tabelaProcedimentos.widthProperty()
                .subtract(20)
                .subtract(colValor.widthProperty()));
        colNome.setResizable(false);
        formatarColunaMoeda(colValor);

        tabelaProcedimentos.setItems(lista);
        tabelaProcedimentos.setPlaceholder(rotuloVazio("Nenhum procedimento cadastrado"));
        carregarProcedimentos();
        txtBusca.textProperty().addListener((obs, old, novo) -> buscar());
    }

    private void configurarScrollTela() {
        scrollTela.viewportBoundsProperty().addListener((obs, antigo, limites) -> {
            if (limites != null) {
                telaProcedimentos.setMinHeight(limites.getHeight());
            }
        });
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
            p.setValor(lerValor(txtValor.getText()));
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
        txtValor.setText(String.format(PT_BR, "%.2f", sel.getValor()));
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

    private <S> void formatarColunaMoeda(TableColumn<S, Double> coluna) {
        coluna.setCellFactory(c -> new TableCell<S, Double>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : FORMATO_MOEDA.format(item));
            }
        });
    }

    private double lerValor(String texto) {
        if (texto == null || texto.trim().isEmpty()) throw new IllegalArgumentException("Informe o valor do procedimento");
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor numérico válido");
        }
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}
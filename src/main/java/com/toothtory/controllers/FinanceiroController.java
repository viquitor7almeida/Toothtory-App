package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.domain.entities.Gasto;
import com.toothtory.domain.entities.ResumoMensal;
import com.toothtory.services.FinanceiroService;
import com.toothtory.services.GastoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FinanceiroController {
    @FXML private ComboBox<Integer> comboAno;
    @FXML private ComboBox<String> comboMes;
    @FXML private TableView<ResumoMensal> tabelaResumo;
    @FXML private TableColumn<ResumoMensal, String> colMes;
    @FXML private TableColumn<ResumoMensal, Double> colReceita;
    @FXML private TableColumn<ResumoMensal, Double> colGastos;
    @FXML private TableColumn<ResumoMensal, Double> colLucro;
    @FXML private BarChart<String, Number> graficoMensal;
    @FXML private TableView<Gasto> tabelaGastos;
    @FXML private TableColumn<Gasto, Long> colGastoId;
    @FXML private TableColumn<Gasto, String> colGastoNome;
    @FXML private TableColumn<Gasto, Double> colGastoValor;
    @FXML private TableColumn<Gasto, LocalDate> colGastoData;
    @FXML private TextField txtNomeGasto;
    @FXML private TextField txtValorGasto;
    @FXML private DatePicker datePickerGasto;
    @FXML private Label totalGastosLabel;

    private final FinanceiroService financeiroService = new FinanceiroService();
    private final GastoService gastoService = new GastoService();
    private final ObservableList<ResumoMensal> resumoList = FXCollections.observableArrayList();
    private final ObservableList<Gasto> gastosList = FXCollections.observableArrayList();
    private final List<String> nomesMeses = new ArrayList<>();
    private Long idEditando = null;

    @FXML
    public void initialize() {
        carregarNomesMeses();
        configurarColunas();
        tabelaGastos.setPlaceholder(rotuloVazio("Nenhum gasto declarado neste mês"));
        configurarCombos();
        graficoMensal.setAnimated(false);
        carregarResumoAnual();
        carregarGastos();
    }

    private void carregarNomesMeses() {
        for (Month mes : Month.values()) {
            String nome = mes.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomesMeses.add(nome.substring(0, 1).toUpperCase() + nome.substring(1));
        }
    }

    private void configurarColunas() {
        colMes.setCellValueFactory(new PropertyValueFactory<>("mes"));
        colReceita.setCellValueFactory(new PropertyValueFactory<>("receita"));
        colGastos.setCellValueFactory(new PropertyValueFactory<>("gastos"));
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucro"));

        colGastoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGastoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colGastoValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colGastoData.setCellValueFactory(new PropertyValueFactory<>("data"));

        formatarColunaMoeda(colReceita);
        formatarColunaMoeda(colGastos);
        formatarColunaMoedaSinal(colLucro);
        formatarColunaMoeda(colGastoValor);
        formatarColunaData(colGastoData);

        tabelaResumo.setItems(resumoList);
        tabelaGastos.setItems(gastosList);
    }

    private <S> void formatarColunaMoeda(TableColumn<S, Double> coluna) {
        coluna.setCellFactory(c -> new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("R$ %.2f", item));
            }
        });
    }

    private <S> void formatarColunaMoedaSinal(TableColumn<S, Double> coluna) {
        coluna.setCellFactory(c -> new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    setTextFill(null);
                } else {
                    setText(String.format("R$ %.2f", item));
                    setTextFill(item < 0 ? Color.web("#C47B7B") : Color.web("#7CA982"));
                }
            }
        });
    }

    private <S> void formatarColunaData(TableColumn<S, LocalDate> coluna) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        coluna.setCellFactory(c -> new TableCell<S, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(formato));
            }
        });
    }

    private void configurarCombos() {
        int anoAtual = YearMonth.now().getYear();
        List<Integer> anos = new ArrayList<>();
        for (int a = anoAtual - 5; a <= anoAtual + 5; a++) {
            anos.add(a);
        }
        comboAno.setItems(FXCollections.observableArrayList(anos));
        comboAno.getSelectionModel().select(Integer.valueOf(anoAtual));

        comboMes.setItems(FXCollections.observableArrayList(nomesMeses));
        comboMes.getSelectionModel().select(YearMonth.now().getMonthValue() - 1);

        comboAno.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            carregarResumoAnual();
            carregarGastos();
        });
        comboMes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> carregarGastos());
    }

    private YearMonth mesSelecionado() {
        Integer ano = comboAno.getSelectionModel().getSelectedItem();
        int indiceMes = comboMes.getSelectionModel().getSelectedIndex();
        if (ano == null) {
            ano = YearMonth.now().getYear();
        }
        if (indiceMes < 0) {
            indiceMes = YearMonth.now().getMonthValue() - 1;
        }
        return YearMonth.of(ano, indiceMes + 1);
    }

    private void carregarResumoAnual() {
        Integer ano = comboAno.getSelectionModel().getSelectedItem();
        if (ano == null) {
            return;
        }
        resumoList.setAll(financeiroService.relatorioAnual(ano));
        atualizarGrafico();
    }

    private void atualizarGrafico() {
        XYChart.Series<String, Number> serieReceita = new XYChart.Series<>();
        serieReceita.setName("Receita");
        XYChart.Series<String, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");
        XYChart.Series<String, Number> serieLucro = new XYChart.Series<>();
        serieLucro.setName("Lucro");

        for (ResumoMensal r : resumoList) {
            serieReceita.getData().add(new XYChart.Data<>(r.getMes(), r.getReceita()));
            serieGastos.getData().add(new XYChart.Data<>(r.getMes(), r.getGastos()));
            serieLucro.getData().add(new XYChart.Data<>(r.getMes(), r.getLucro()));
        }
        graficoMensal.getData().setAll(serieReceita, serieGastos, serieLucro);
    }

    private void carregarGastos() {
        YearMonth mes = mesSelecionado();
        gastosList.setAll(gastoService.buscarPorMes(mes));
        double total = gastoService.totalGastoNoMes(mes);
        totalGastosLabel.setText(String.format("Total de gastos em %s/%d: R$ %.2f",
                nomesMeses.get(mes.getMonthValue() - 1), mes.getYear(), total));
    }

    @FXML
    private void salvarGasto() {
        try {
            String nome = txtNomeGasto.getText();
            if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Informe o nome do gasto");

            double valor;
            try {
                valor = Double.parseDouble(txtValorGasto.getText().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Informe um valor numérico válido");
            }

            LocalDate data = datePickerGasto.getValue();
            if (data == null) throw new IllegalArgumentException("Selecione a data do gasto");

            Gasto gasto = idEditando != null ? gastoService.findById(idEditando).orElse(new Gasto()) : new Gasto();
            gasto.setNome(nome);
            gasto.setValor(valor);
            gasto.setData(data);
            gastoService.salvar(gasto);
            idEditando = null;

            limparFormularioGasto();
            comboAno.getSelectionModel().select(Integer.valueOf(data.getYear()));
            comboMes.getSelectionModel().select(data.getMonthValue() - 1);
            carregarResumoAnual();
            carregarGastos();
            Notificacao.sucesso("Gasto salvo.");
        } catch (Exception e) {
            Notificacao.erro(e.getMessage());
        }
    }

    @FXML
    private void limparFormularioGasto() {
        txtNomeGasto.clear();
        txtValorGasto.clear();
        datePickerGasto.setValue(null);
        idEditando = null;
    }

    @FXML
    private void editarGasto() {
        Gasto sel = tabelaGastos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Notificacao.aviso("Selecione um gasto.");
            return;
        }
        txtNomeGasto.setText(sel.getNome());
        txtValorGasto.setText(String.valueOf(sel.getValor()));
        datePickerGasto.setValue(sel.getData());
        idEditando = sel.getId();
    }

    @FXML
    private void excluirGasto() {
        Gasto sel = tabelaGastos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Notificacao.aviso("Selecione um gasto.");
            return;
        }
        if (Alerta.confirmar("Confirmar exclusão", "Excluir gasto?")) {
            gastoService.excluir(sel.getId());
            carregarResumoAnual();
            carregarGastos();
            limparFormularioGasto();
        }
    }

    private Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("tabela-vazia");
        return rotulo;
    }
}

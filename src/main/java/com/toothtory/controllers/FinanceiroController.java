package com.toothtory.controllers;

import com.toothtory.components.Alerta;
import com.toothtory.components.Notificacao;
import com.toothtory.domain.entities.Gasto;
import com.toothtory.domain.entities.ResumoMensal;
import com.toothtory.services.FinanceiroService;
import com.toothtory.services.GastoService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.text.NumberFormat;
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
    @FXML private ScrollPane scrollTela;
    @FXML private VBox telaFinanceiro;
    @FXML private Label infoGraficoLabel;
    @FXML private BarChart<String, Number> graficoMensal;
    @FXML private TableView<Gasto> tabelaGastos;
    @FXML private TableColumn<Gasto, String> colGastoNome;
    @FXML private TableColumn<Gasto, Double> colGastoValor;
    @FXML private TableColumn<Gasto, LocalDate> colGastoData;
    @FXML private TextField txtNomeGasto;
    @FXML private TextField txtValorGasto;
    @FXML private DatePicker datePickerGasto;
    @FXML private Label totalGastosLabel;

    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final FinanceiroService financeiroService = new FinanceiroService();
    private final GastoService gastoService = new GastoService();
    private final ObservableList<ResumoMensal> resumoList = FXCollections.observableArrayList();
    private final ObservableList<Gasto> gastosList = FXCollections.observableArrayList();
    private final List<String> nomesMeses = new ArrayList<>();
    private final PauseTransition atrasoExibirInfo = new PauseTransition(Duration.millis(500));
    private final PauseTransition atrasoOcultarInfo = new PauseTransition(Duration.millis(200));
    private String infoGraficoPendente;
    private Object barraAtual;
    private Long idEditando = null;

    @FXML
    public void initialize() {
        carregarNomesMeses();
        configurarScrollTela();
        configurarColunas();
        tabelaGastos.setPlaceholder(rotuloVazio("Nenhum gasto declarado neste mês"));
        graficoMensal.setAnimated(false);
        configurarCombos();
        configurarInfoGrafico();
        carregarResumoAnual();
        carregarGastos();
    }

    private void configurarScrollTela() {
        scrollTela.viewportBoundsProperty().addListener((obs, antigo, limites) -> {
            if (limites != null) {
                telaFinanceiro.setMinHeight(limites.getHeight());
            }
        });
    }

    private void carregarNomesMeses() {
        for (Month mes : Month.values()) {
            String nome = mes.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomesMeses.add(nome.substring(0, 1).toUpperCase() + nome.substring(1));
        }
    }

    private void configurarColunas() {
        colGastoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colGastoValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colGastoData.setCellValueFactory(new PropertyValueFactory<>("data"));

        formatarColunaMoeda(colGastoValor);
        formatarColunaData(colGastoData);

        colGastoNome.prefWidthProperty().bind(tabelaGastos.widthProperty()
                .subtract(20)
                .subtract(colGastoValor.prefWidthProperty())
                .subtract(colGastoData.prefWidthProperty()));
        colGastoNome.setResizable(false);

        tabelaGastos.setItems(gastosList);
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
        comboMes.setItems(FXCollections.observableArrayList(nomesMeses));
        comboMes.getSelectionModel().select(YearMonth.now().getMonthValue() - 1);

        comboAno.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            carregarResumoAnual();
            carregarGastos();
        });
        comboMes.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> carregarGastos());

        atualizarAnos();
    }

    private void atualizarAnos() {
        Integer anoSelecionado = comboAno.getSelectionModel().getSelectedItem();
        List<Integer> anos = financeiroService.anosComRegistros();
        comboAno.setItems(FXCollections.observableArrayList(anos));
        if (anos.isEmpty()) {
            comboAno.getSelectionModel().clearSelection();
        } else if (anos.contains(anoSelecionado)) {
            comboAno.getSelectionModel().select(anoSelecionado);
        } else {
            comboAno.getSelectionModel().select(0);
        }
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
        boolean algumMesComMovimento = false;
        for (ResumoMensal r : resumoList) {
            if (r.getReceita() != 0 || r.getGastos() != 0 || r.getLucro() != 0) {
                algumMesComMovimento = true;
                break;
            }
        }

        XYChart.Series<String, Number> serieReceita = new XYChart.Series<>();
        serieReceita.setName("Receita");
        XYChart.Series<String, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");
        XYChart.Series<String, Number> serieLucro = new XYChart.Series<>();
        serieLucro.setName("Lucro");

        for (ResumoMensal r : resumoList) {
            boolean mesZerado = r.getReceita() == 0 && r.getGastos() == 0 && r.getLucro() == 0;
            if (mesZerado && algumMesComMovimento) continue;
            serieReceita.getData().add(new XYChart.Data<>(r.getMes(), r.getReceita()));
            serieGastos.getData().add(new XYChart.Data<>(r.getMes(), r.getGastos()));
            serieLucro.getData().add(new XYChart.Data<>(r.getMes(), r.getLucro()));
        }
        instalarTooltips(serieReceita);
        instalarTooltips(serieGastos);
        instalarTooltips(serieLucro);
        graficoMensal.getData().setAll(serieReceita, serieGastos, serieLucro);
    }

    private void configurarInfoGrafico() {
        atrasoExibirInfo.setOnFinished(evento -> {
            if (barraAtual != null && infoGraficoPendente != null) {
                infoGraficoLabel.setText(infoGraficoPendente);
                if (!infoGraficoLabel.getStyleClass().contains("ativo")) {
                    infoGraficoLabel.getStyleClass().add("ativo");
                }
            }
        });
        atrasoOcultarInfo.setOnFinished(evento -> {
            if (barraAtual == null) {
                infoGraficoLabel.setText("Passe o mouse sobre uma barra para ver o valor");
                infoGraficoLabel.getStyleClass().remove("ativo");
            }
        });
    }

    private void instalarTooltips(XYChart.Series<String, Number> serie) {
        for (XYChart.Data<String, Number> dado : serie.getData()) {
            dado.nodeProperty().addListener((obs, antigo, node) -> {
                if (node == null) return;
                String texto = serie.getName() + " — " + dado.getXValue() + ": "
                        + FORMATO_MOEDA.format(dado.getYValue().doubleValue());
                node.addEventHandler(MouseEvent.MOUSE_ENTERED, evento -> {
                    barraAtual = node;
                    infoGraficoPendente = texto;
                    atrasoOcultarInfo.stop();
                    atrasoExibirInfo.playFromStart();
                });
                node.addEventHandler(MouseEvent.MOUSE_EXITED, evento -> {
                    if (barraAtual == node) {
                        barraAtual = null;
                        infoGraficoPendente = null;
                    }
                    atrasoExibirInfo.stop();
                    atrasoOcultarInfo.playFromStart();
                });
            });
        }
    }

    private void carregarGastos() {
        YearMonth mes = mesSelecionado();
        gastosList.setAll(gastoService.buscarPorMes(mes));
        double total = gastoService.totalGastoNoMes(mes);
        totalGastosLabel.setText(String.format("Total de gastos em %s/%d: %s",
                nomesMeses.get(mes.getMonthValue() - 1), mes.getYear(), FORMATO_MOEDA.format(total)));
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
            atualizarAnos();
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
            atualizarAnos();
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

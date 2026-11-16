package com.toothtory.components;

import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModalConsultaPaciente {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATO_VALOR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static void mostrar(Window janelaDono, Paciente paciente, Anamnese anamnese, List<Consulta> consultas) {
        Stage janela = new Stage(StageStyle.TRANSPARENT);
        janela.initModality(Modality.WINDOW_MODAL);
        janela.initOwner(janelaDono);

        VBox painel = new VBox();
        painel.getStyleClass().add("modal-consulta");
        painel.getChildren().add(cabecalho(janela, paciente));
        painel.getChildren().add(divisor());

        VBox conteudo = new VBox(20);
        conteudo.setPadding(new Insets(16, 20, 24, 20));
        conteudo.getChildren().add(secaoAnamnese(anamnese));
        conteudo.getChildren().add(secaoConsultas(consultas));

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.getStyleClass().add("modal-scroll");
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        painel.getChildren().add(scroll);

        painel.getChildren().add(rodape(janela, consultas));
        painel.setPrefSize(632, 592);

        Region backdrop = new Region();
        backdrop.getStyleClass().add("modal-backdrop");
        backdrop.setOnMouseClicked(evento -> janela.close());

        StackPane centro = new StackPane(painel);
        centro.setPadding(new Insets(14));

        StackPane raiz = new StackPane(backdrop, centro);

        Scene cena = new Scene(raiz, Color.TRANSPARENT);
        cena.getStylesheets().add(ModalConsultaPaciente.class.getResource("/styles/toothtory.css").toExternalForm());
        cena.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ESCAPE) {
                janela.close();
            }
        });

        janela.setX(janelaDono.getX());
        janela.setY(janelaDono.getY());
        janela.setWidth(janelaDono.getWidth());
        janela.setHeight(janelaDono.getHeight());
        janela.setScene(cena);
        janela.showAndWait();
    }

    private static HBox cabecalho(Stage janela, Paciente paciente) {
        Label nome = new Label(paciente.getNome());
        nome.getStyleClass().add("modal-titulo-nome");

        Label detalhes = new Label(detalhesPaciente(paciente));
        detalhes.getStyleClass().add("modal-subtitulo");
        detalhes.setWrapText(true);

        VBox textos = new VBox(3, nome, detalhes);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Button fechar = new Button("✕");
        fechar.getStyleClass().add("botao-fechar-overlay");
        fechar.setOnAction(evento -> janela.close());

        HBox cabecalho = new HBox(12, textos, fechar);
        cabecalho.getStyleClass().add("modal-cabecalho");
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        return cabecalho;
    }

    private static String detalhesPaciente(Paciente paciente) {
        return Stream.of(paciente.getCpf(), paciente.getEmail(), paciente.getCelular(), paciente.getEndereco())
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(Collectors.joining("  ·  "));
    }

    private static Region divisor() {
        Region divisor = new Region();
        divisor.getStyleClass().add("modal-divisor");
        return divisor;
    }

    private static VBox secaoAnamnese(Anamnese anamnese) {
        Label titulo = new Label("Anamnese");
        titulo.getStyleClass().add("modal-titulo-secao");
        VBox secao = new VBox(10, titulo);
        if (anamnese == null) {
            secao.getChildren().add(rotuloVazio("Nenhuma anamnese registrada para este paciente."));
            return secao;
        }
        FlowPane chips = new FlowPane(8, 8);
        chips.getChildren().addAll(
                chip("Diabetes", anamnese.isDiabetes()),
                chip("Hipertenso", anamnese.isHipertenso()),
                chip("Rim/Pâncreas", anamnese.isProblemasRimPancreas()),
                chip("Pulmonares", anamnese.isProblemasPulmonares()),
                chip("Hormônio", anamnese.isUsoHormonio()),
                chip("Grávida", anamnese.isGravida()),
                chip("Alergia a medicamento", anamnese.isAlergiaMedicamento()),
                chip("Medicamento contínuo", anamnese.isUsoMedicamentoContinuo()),
                chip("Cardíacos", anamnese.isProblemasCardiacos()),
                chip("Sanguíneos", anamnese.isProblemasSanguineos()));
        secao.getChildren().add(chips);
        String detalhes = detalhesMedicamentosos(anamnese);
        if (!detalhes.isEmpty()) {
            Label linhaDetalhes = new Label(detalhes);
            linhaDetalhes.getStyleClass().add("modal-texto");
            linhaDetalhes.setWrapText(true);
            secao.getChildren().add(linhaDetalhes);
        }
        if (anamnese.getObservacoes() != null && !anamnese.getObservacoes().isBlank()) {
            Label observacoes = new Label(anamnese.getObservacoes());
            observacoes.getStyleClass().add("modal-texto");
            observacoes.setWrapText(true);
            secao.getChildren().add(observacoes);
        }
        secao.getChildren().add(rotuloVazio("Atualizada em " + anamnese.getDataAtualizacao().format(FORMATO_DATA)));
        return secao;
    }

    private static Label chip(String rotulo, boolean positivo) {
        Label chip = new Label(rotulo + ": " + (positivo ? "Sim" : "Não"));
        chip.getStyleClass().addAll("chip-anamnese", positivo ? "chip-sim" : "chip-nao");
        return chip;
    }

    private static String detalhesMedicamentosos(Anamnese anamnese) {
        return Stream.of(
                        detalheValido("Hormônio", anamnese.isUsoHormonio(), anamnese.getHormonioDetalhe()),
                        detalheValido("Alergia", anamnese.isAlergiaMedicamento(), anamnese.getAlergiaMedicamentoDetalhe()),
                        detalheValido("Medicamento contínuo", anamnese.isUsoMedicamentoContinuo(), anamnese.getMedicamentoContinuoDetalhe()))
                .filter(valor -> valor != null)
                .collect(Collectors.joining("  ·  "));
    }

    private static String detalheValido(String rotulo, boolean positivo, String detalhe) {
        if (!positivo || detalhe == null || detalhe.isBlank()) {
            return null;
        }
        return rotulo + ": " + detalhe;
    }

    private static VBox secaoConsultas(List<Consulta> consultas) {
        Label titulo = new Label("Consultas (" + consultas.size() + ")");
        titulo.getStyleClass().add("modal-titulo-secao");
        VBox secao = new VBox(10, titulo);
        if (consultas.isEmpty()) {
            secao.getChildren().add(rotuloVazio("Nenhuma consulta registrada para este paciente."));
            return secao;
        }
        for (Consulta consulta : consultas) {
            secao.getChildren().add(cartaoConsulta(consulta));
        }
        return secao;
    }

    private static VBox cartaoConsulta(Consulta consulta) {
        Label dataProcedimento = new Label(consulta.getDataHora().format(FORMATO_DATA_HORA) + "  ·  " + consulta.getNomeProcedimento());
        dataProcedimento.getStyleClass().add("modal-consulta-linha1");

        Label valor = new Label(FORMATO_VALOR.format(consulta.getValorProcedimento()));
        valor.getStyleClass().add("modal-consulta-valor");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        VBox cartao = new VBox(3);
        cartao.getStyleClass().add("modal-consulta-item");

        if (consulta.getObservacoes() != null && !consulta.getObservacoes().isBlank()) {
            Label observacoes = new Label(consulta.getObservacoes());
            observacoes.getStyleClass().add("modal-consulta-linha2");
            observacoes.setWrapText(true);
            observacoes.setVisible(false);
            observacoes.setManaged(false);

            Label seta = new Label("▸");
            seta.getStyleClass().add("modal-consulta-seta");

            HBox topo = new HBox(10, dataProcedimento, espaco, valor, seta);
            topo.setAlignment(Pos.CENTER_LEFT);
            cartao.getChildren().addAll(topo, observacoes);

            cartao.getStyleClass().add("modal-consulta-item-expansivel");
            cartao.setOnMouseClicked(evento -> {
                boolean expandido = !observacoes.isVisible();
                observacoes.setVisible(expandido);
                observacoes.setManaged(expandido);
                seta.setText(expandido ? "▾" : "▸");
            });
        } else {
            HBox topo = new HBox(10, dataProcedimento, espaco, valor);
            topo.setAlignment(Pos.CENTER_LEFT);
            cartao.getChildren().add(topo);
        }
        return cartao;
    }

    private static HBox rodape(Stage janela, List<Consulta> consultas) {
        double total = consultas.stream().mapToDouble(Consulta::getValorProcedimento).sum();
        Label totalRotulo = new Label("Total faturado: " + FORMATO_VALOR.format(total));
        totalRotulo.getStyleClass().add("total-gasto");

        Button fechar = new Button("Fechar");
        fechar.getStyleClass().add("button-primario");
        fechar.setOnAction(evento -> janela.close());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox rodape = new HBox(10, totalRotulo, espaco, fechar);
        rodape.getStyleClass().add("modal-rodape");
        rodape.setAlignment(Pos.CENTER_LEFT);
        return rodape;
    }

    private static Label rotuloVazio(String texto) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add("ficha-vazio");
        return rotulo;
    }
}

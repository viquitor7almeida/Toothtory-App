package com.toothtory.components;

import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.entities.Paciente;
import com.toothtory.services.ConsultaService;
import com.toothtory.services.PacienteService;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Calendário mensal das consultas.
 *
 * Modo preview (new Calendario()): clique em dia com consulta abre o card com as
 * informações, sem botão de agendamento e sem ações.
 *
 * Modo interativo (new Calendario(acoes)): além do card com botão "Nova consulta
 * neste dia", os cliques são delegados para as ações informadas.
 */
public class Calendario extends VBox {

    public interface Acoes {
        void aoClicarDiaVazio(LocalDate dia);
        void aoAgendarDireto(LocalDate dia);
        void aoSelecionarConsulta(Consulta consulta);
    }

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final String[] DIAS_SEMANA = {"DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB"};

    private final ConsultaService consultaService = new ConsultaService();
    private final PacienteService pacienteService = new PacienteService();
    private final Map<LocalDate, List<Consulta>> consultasDoMes = new HashMap<>();
    private final PauseTransition atrasoFecharOverlay = new PauseTransition(Duration.millis(250));

    private final Acoes acoes;
    private final Label labelMesAno = new Label();
    private final StackPane areaCalendario = new StackPane();
    private final GridPane gradeDias = new GridPane();

    private YearMonth mesAtual = YearMonth.now();
    private VBox overlayAtual;

    public Calendario() {
        this(null);
    }

    public Calendario(Acoes acoes) {
        this.acoes = acoes;
        setSpacing(10);

        montarCabecalho();
        montarAreaCalendario();

        atrasoFecharOverlay.setOnFinished(evento -> fecharOverlay());
        areaCalendario.addEventHandler(MouseEvent.MOUSE_CLICKED, evento -> fecharOverlay());

        atualizar();
    }

    private void montarCabecalho() {
        Button anterior = new Button("‹");
        anterior.getStyleClass().add("botao-navegacao-mes");
        anterior.setMinWidth(34);
        anterior.setOnAction(evento -> irParaMes(mesAtual.minusMonths(1)));

        labelMesAno.getStyleClass().add("calendario-mes-ano");
        labelMesAno.setMinWidth(150);
        labelMesAno.setAlignment(Pos.CENTER);

        Button proximo = new Button("›");
        proximo.getStyleClass().add("botao-navegacao-mes");
        proximo.setMinWidth(34);
        proximo.setOnAction(evento -> irParaMes(mesAtual.plusMonths(1)));

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Button hoje = new Button("Hoje");
        hoje.setOnAction(evento -> irParaMes(YearMonth.now()));

        HBox cabecalho = new HBox(10);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.getChildren().addAll(anterior, labelMesAno, proximo, espacador, hoje);
        getChildren().add(cabecalho);
    }

    private void montarAreaCalendario() {
        gradeDias.setHgap(4);
        gradeDias.setVgap(4);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setHgrow(Priority.ALWAYS);
            gradeDias.getColumnConstraints().add(coluna);
        }
        gradeDias.getRowConstraints().add(new RowConstraints());
        for (int i = 0; i < 6; i++) {
            RowConstraints linha = new RowConstraints();
            linha.setVgrow(Priority.ALWAYS);
            gradeDias.getRowConstraints().add(linha);
        }

        areaCalendario.getStyleClass().add("calendario");
        areaCalendario.setPrefHeight(400);
        areaCalendario.setMinHeight(380);
        areaCalendario.getChildren().add(gradeDias);
        getChildren().add(areaCalendario);
    }

    public void atualizar() {
        List<Consulta> consultas = consultaService.buscarPorPeriodo(
                mesAtual.atDay(1).atStartOfDay(),
                mesAtual.atEndOfMonth().atTime(23, 59, 59));

        consultasDoMes.clear();
        for (Consulta c : consultas) {
            consultasDoMes.computeIfAbsent(c.getDataHora().toLocalDate(), k -> new ArrayList<>()).add(c);
        }
        for (List<Consulta> lista : consultasDoMes.values()) {
            lista.sort(Comparator.comparing(Consulta::getDataHora));
        }

        labelMesAno.setText(capitalizar(mesAtual.format(DateTimeFormatter.ofPattern("MMMM yyyy", PT_BR))));
        montarGrade();
    }

    public void irParaMes(YearMonth mes) {
        mesAtual = mes;
        fecharOverlay();
        atualizar();
    }

    private void montarGrade() {
        gradeDias.getChildren().clear();

        for (int i = 0; i < 7; i++) {
            Label diaSemana = new Label(DIAS_SEMANA[i]);
            diaSemana.getStyleClass().add("calendario-semana");
            diaSemana.setMaxWidth(Double.MAX_VALUE);
            diaSemana.setAlignment(Pos.CENTER);
            gradeDias.add(diaSemana, i, 0);
        }

        int deslocamento = mesAtual.atDay(1).getDayOfWeek().getValue() % 7;
        LocalDate primeiroDia = mesAtual.atDay(1).minusDays(deslocamento);

        for (int i = 0; i < 42; i++) {
            LocalDate data = primeiroDia.plusDays(i);
            gradeDias.add(criarCelula(data), i % 7, 1 + i / 7);
        }
    }

    private VBox criarCelula(LocalDate data) {
        VBox celula = new VBox();
        celula.getStyleClass().add("calendario-dia");

        Label numero = new Label(String.valueOf(data.getDayOfMonth()));
        numero.getStyleClass().add("calendario-dia-numero");
        if (data.equals(LocalDate.now())) {
            numero.getStyleClass().add("calendario-hoje-numero");
        }
        celula.getChildren().add(numero);

        boolean temConsultas = consultasDoMes.containsKey(data);
        if (temConsultas) {
            Region espacador = new Region();
            VBox.setVgrow(espacador, Priority.ALWAYS);
            Region bolinha = new Region();
            bolinha.getStyleClass().add("calendario-bolinha");
            bolinha.setMinSize(6, 6);
            bolinha.setMaxSize(6, 6);
            HBox linhaBolinha = new HBox();
            linhaBolinha.setAlignment(Pos.CENTER);
            linhaBolinha.getChildren().add(bolinha);
            celula.getChildren().addAll(espacador, linhaBolinha);
        }

        if (data.getMonth() != mesAtual.getMonth()) {
            celula.getStyleClass().add("calendario-dia-outro-mes");
            return celula;
        }

        celula.setOnMouseClicked(evento -> {
            evento.consume();
            aoClicarDia(data, temConsultas, celula);
        });
        return celula;
    }

    private void aoClicarDia(LocalDate data, boolean temConsultas, Node celula) {
        fecharOverlay();

        if (!temConsultas) {
            if (acoes != null) {
                acoes.aoClicarDiaVazio(data);
            }
            return;
        }

        mostrarOverlayDia(data, celula);
    }

    private void mostrarOverlayDia(LocalDate dia, Node celula) {
        fecharOverlay();

        VBox overlay = new VBox();
        overlay.getStyleClass().add("calendario-hover");
        overlay.setManaged(false);
        overlay.setMinWidth(320);
        overlay.setPrefWidth(320);
        overlay.setMaxWidth(320);

        Label titulo = new Label("Consultas de " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        titulo.getStyleClass().add("calendario-hover-titulo");
        titulo.setMaxWidth(Double.MAX_VALUE);
        overlay.getChildren().add(titulo);

        for (Consulta c : consultasDoMes.getOrDefault(dia, List.of())) {
            VBox item = new VBox();
            item.getStyleClass().add("calendario-hover-item");

            Label linha1 = new Label(c.getDataHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " · " + nomePaciente(c.getPacienteId()));
            linha1.getStyleClass().add("calendario-hover-item-linha1");

            Label linha2 = new Label(c.getNomeProcedimento() + " · " + FORMATO_MOEDA.format(c.getValorProcedimento()));
            linha2.getStyleClass().add("calendario-hover-item-linha2");

            item.getChildren().addAll(linha1, linha2);
            item.setOnMouseClicked(evento -> {
                evento.consume();
                if (acoes != null) {
                    fecharOverlay();
                    acoes.aoSelecionarConsulta(c);
                }
            });
            overlay.getChildren().add(item);
        }

        if (acoes != null) {
            Button nova = new Button("Nova consulta neste dia");
            nova.getStyleClass().add("button-primario");
            nova.setMaxWidth(Double.MAX_VALUE);
            nova.setOnAction(evento -> {
                fecharOverlay();
                acoes.aoAgendarDireto(dia);
            });
            overlay.getChildren().add(nova);
        }

        overlay.setOnMouseEntered(evento -> atrasoFecharOverlay.stop());
        overlay.setOnMouseExited(evento -> atrasoFecharOverlay.playFromStart());
        overlay.setOnMouseClicked(evento -> evento.consume());

        areaCalendario.getChildren().add(overlay);
        overlay.applyCss();
        overlay.autosize();
        overlay.layout();

        Bounds limitesCelula = celula.localToScene(celula.getBoundsInLocal());
        Bounds limitesArea = areaCalendario.localToScene(areaCalendario.getBoundsInLocal());

        double x = limitesCelula.getMaxX() - limitesArea.getMinX() + 8;
        double y = limitesCelula.getMinY() - limitesArea.getMinY();

        if (x + overlay.getWidth() > areaCalendario.getWidth() - 6) {
            x = limitesCelula.getMinX() - limitesArea.getMinX() - overlay.getWidth() - 8;
        }
        if (y + overlay.getHeight() > areaCalendario.getHeight() - 6) {
            y = areaCalendario.getHeight() - overlay.getHeight() - 6;
        }
        if (x < 6) x = 6;
        if (y < 6) y = 6;

        overlay.setLayoutX(x);
        overlay.setLayoutY(y);
        overlayAtual = overlay;
    }

    private void fecharOverlay() {
        atrasoFecharOverlay.stop();
        if (overlayAtual != null) {
            areaCalendario.getChildren().remove(overlayAtual);
            overlayAtual = null;
        }
    }

    private String nomePaciente(Long pacienteId) {
        Paciente p = pacienteService.findById(pacienteId).orElse(null);
        return p == null ? "Paciente removido" : p.getNome();
    }

    private String capitalizar(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}

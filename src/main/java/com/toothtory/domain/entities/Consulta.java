package com.toothtory.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Consulta {
    private String id;
    private String pacienteId;
    private LocalDateTime dataHora;
    private String nomeProcedimento;
    private double valorProcedimento;
    private String observacoes;
    private LocalDateTime dataCriacao;

    public Consulta() {
        this.id = UUID.randomUUID().toString();
        this.dataCriacao = LocalDateTime.now();
    }

    public Consulta(String id, String pacienteId, LocalDateTime dataHora, String nomeProcedimento, double valorProcedimento, String observacoes, LocalDateTime dataCriacao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.dataHora = dataHora;
        this.nomeProcedimento = nomeProcedimento;
        this.valorProcedimento = valorProcedimento;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getNomeProcedimento() { return nomeProcedimento; }
    public void setNomeProcedimento(String nomeProcedimento) { this.nomeProcedimento = nomeProcedimento; }
    public double getValorProcedimento() { return valorProcedimento; }
    public void setValorProcedimento(double valorProcedimento) { this.valorProcedimento = valorProcedimento; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
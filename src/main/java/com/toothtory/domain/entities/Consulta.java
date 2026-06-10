package com.toothtory.domain.entities;

import java.time.LocalDateTime;

public class Consulta {
    private Long id;
    private Long pacienteId;
    private LocalDateTime dataHora;
    private String nomeProcedimento;
    private double valorProcedimento;
    private String observacoes;
    private LocalDateTime dataCriacao;

    public Consulta() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Consulta(Long id, Long pacienteId, LocalDateTime dataHora, String nomeProcedimento, double valorProcedimento, String observacoes, LocalDateTime dataCriacao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.dataHora = dataHora;
        this.nomeProcedimento = nomeProcedimento;
        this.valorProcedimento = valorProcedimento;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
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
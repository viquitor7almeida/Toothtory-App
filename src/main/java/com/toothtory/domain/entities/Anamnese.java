package com.toothtory.domain.entities;

import java.time.LocalDateTime;

public class Anamnese {
    private Long id;
    private Long pacienteId;
    private boolean diabetes;
    private boolean hipertenso;
    private boolean problemasRimPancreas;
    private boolean problemasPulmonares;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Anamnese() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public boolean isDiabetes() { return diabetes; }
    public void setDiabetes(boolean diabetes) { this.diabetes = diabetes; }
    public boolean isHipertenso() { return hipertenso; }
    public void setHipertenso(boolean hipertenso) { this.hipertenso = hipertenso; }
    public boolean isProblemasRimPancreas() { return problemasRimPancreas; }
    public void setProblemasRimPancreas(boolean problemasRimPancreas) { this.problemasRimPancreas = problemasRimPancreas; }
    public boolean isProblemasPulmonares() { return problemasPulmonares; }
    public void setProblemasPulmonares(boolean problemasPulmonares) { this.problemasPulmonares = problemasPulmonares; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}

package com.toothtory.domain.entities;

import java.time.LocalDateTime;

public class Anamnese {
    private Long id;
    private Long pacienteId;
    private boolean diabetes;
    private boolean hipertenso;
    private boolean problemasRimPancreas;
    private boolean problemasPulmonares;
    private boolean usoHormonio;
    private String hormonioDetalhe;
    private boolean gravida;
    private boolean alergiaMedicamento;
    private String alergiaMedicamentoDetalhe;
    private boolean usoMedicamentoContinuo;
    private String medicamentoContinuoDetalhe;
    private boolean problemasCardiacos;
    private boolean problemasSanguineos;
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
    public boolean isUsoHormonio() { return usoHormonio; }
    public void setUsoHormonio(boolean usoHormonio) { this.usoHormonio = usoHormonio; }
    public String getHormonioDetalhe() { return hormonioDetalhe; }
    public void setHormonioDetalhe(String hormonioDetalhe) { this.hormonioDetalhe = hormonioDetalhe; }
    public boolean isGravida() { return gravida; }
    public void setGravida(boolean gravida) { this.gravida = gravida; }
    public boolean isAlergiaMedicamento() { return alergiaMedicamento; }
    public void setAlergiaMedicamento(boolean alergiaMedicamento) { this.alergiaMedicamento = alergiaMedicamento; }
    public String getAlergiaMedicamentoDetalhe() { return alergiaMedicamentoDetalhe; }
    public void setAlergiaMedicamentoDetalhe(String alergiaMedicamentoDetalhe) { this.alergiaMedicamentoDetalhe = alergiaMedicamentoDetalhe; }
    public boolean isUsoMedicamentoContinuo() { return usoMedicamentoContinuo; }
    public void setUsoMedicamentoContinuo(boolean usoMedicamentoContinuo) { this.usoMedicamentoContinuo = usoMedicamentoContinuo; }
    public String getMedicamentoContinuoDetalhe() { return medicamentoContinuoDetalhe; }
    public void setMedicamentoContinuoDetalhe(String medicamentoContinuoDetalhe) { this.medicamentoContinuoDetalhe = medicamentoContinuoDetalhe; }
    public boolean isProblemasCardiacos() { return problemasCardiacos; }
    public void setProblemasCardiacos(boolean problemasCardiacos) { this.problemasCardiacos = problemasCardiacos; }
    public boolean isProblemasSanguineos() { return problemasSanguineos; }
    public void setProblemasSanguineos(boolean problemasSanguineos) { this.problemasSanguineos = problemasSanguineos; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}

package com.toothtory.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Gasto {
    private Long id;
    private String nome;
    private double valor;
    private LocalDate data;
    private LocalDateTime dataCriacao;

    public Gasto() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Gasto(Long id, String nome, double valor, LocalDate data, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.data = data;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}

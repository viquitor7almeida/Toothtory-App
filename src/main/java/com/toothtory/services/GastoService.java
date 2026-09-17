package com.toothtory.services;

import com.toothtory.domain.entities.Gasto;
import com.toothtory.domain.repositories.GastoRepository;
import com.toothtory.infra.repositories.GastoRepositoryImpl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class GastoService {
    private final GastoRepository repository;

    public GastoService() {
        this.repository = new GastoRepositoryImpl();
    }

    public void salvar(Gasto gasto) {
        validarGasto(gasto);
        if (gasto.getId() == null) {
            repository.save(gasto);
        } else {
            repository.update(gasto);
        }
    }

    public void excluir(Long id) {
        repository.delete(id);
    }

    public Optional<Gasto> findById(Long id) {
        return repository.findById(id);
    }

    public List<Gasto> listarTodos() {
        return repository.findAll();
    }

    public List<Gasto> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repository.findByPeriodo(inicio, fim);
    }

    public List<Gasto> buscarPorMes(YearMonth mes) {
        return repository.findByPeriodo(mes.atDay(1), mes.atEndOfMonth());
    }

    public double totalGastoNoMes(YearMonth mes) {
        return repository.somaValorPorPeriodo(mes.atDay(1), mes.atEndOfMonth());
    }

    private void validarGasto(Gasto gasto) {
        if (gasto.getNome() == null || gasto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do gasto é obrigatório");
        }
        if (gasto.getValor() <= 0) {
            throw new IllegalArgumentException("Valor do gasto deve ser positivo");
        }
        if (gasto.getData() == null) {
            throw new IllegalArgumentException("Data do gasto é obrigatória");
        }
    }
}

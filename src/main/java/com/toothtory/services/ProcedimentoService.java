package com.toothtory.services;

import com.toothtory.domain.entities.Procedimento;
import com.toothtory.domain.repositories.ProcedimentoRepository;
import com.toothtory.infra.repositories.ProcedimentoRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ProcedimentoService {
    private final ProcedimentoRepository repository;

    public ProcedimentoService() {
        this.repository = new ProcedimentoRepositoryImpl();
    }

    public void salvar(Procedimento procedimento) {
        validarProcedimento(procedimento);
        if (procedimento.getId() == null || findById(procedimento.getId()).isEmpty()) {
            repository.save(procedimento);
        } else {
            repository.update(procedimento);
        }
    }

    public void excluir(String id) {
        repository.delete(id);
    }

    public Optional<Procedimento> findById(String id) {
        return repository.findById(id);
    }

    public List<Procedimento> listarTodos() {
        return repository.findAll();
    }

    public List<Procedimento> buscarPorNome(String nome) {
        return repository.searchByName(nome);
    }

    private void validarProcedimento(Procedimento procedimento) {
        if (procedimento.getNome() == null || procedimento.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do procedimento é obrigatório");
        }
        if (procedimento.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }
}
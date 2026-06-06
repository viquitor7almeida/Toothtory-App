package com.toothtory.services;

import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.repositories.PacienteRepository;
import com.toothtory.infra.repositories.PacienteRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class PacienteService {
    private final PacienteRepository repository;

    public PacienteService() {
        this.repository = new PacienteRepositoryImpl();
    }

    public void salvar(Paciente paciente) {
        validarPaciente(paciente);
        if (paciente.getId() == null || findById(paciente.getId()).isEmpty()) {
            repository.save(paciente);
        } else {
            repository.update(paciente);
        }
    }

    public void excluir(String id) {
        repository.delete(id);
    }

    public Optional<Paciente> findById(String id) {
        return repository.findById(id);
    }

    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    public List<Paciente> buscarPorNome(String nome) {
        return repository.searchByName(nome);
    }

    private void validarPaciente(Paciente paciente) {
        if (paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }
}
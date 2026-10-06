package com.toothtory.services;

import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.repositories.AnamneseRepository;
import com.toothtory.infra.repositories.AnamneseRepositoryImpl;

import java.util.Optional;

public class AnamneseService {
    private final AnamneseRepository repository;

    public AnamneseService() {
        this.repository = new AnamneseRepositoryImpl();
    }

    public void salvar(Anamnese anamnese) {
        if (anamnese.getPacienteId() == null) {
            throw new IllegalArgumentException("Paciente é obrigatório");
        }
        Optional<Anamnese> existente = repository.findByPacienteId(anamnese.getPacienteId());
        if (existente.isPresent()) {
            repository.update(anamnese);
        } else {
            repository.save(anamnese);
        }
    }

    public Optional<Anamnese> buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }
}

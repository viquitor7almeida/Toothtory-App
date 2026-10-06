package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Anamnese;

import java.util.List;
import java.util.Optional;

public interface AnamneseRepository {
    void save(Anamnese anamnese);
    void update(Anamnese anamnese);
    Optional<Anamnese> findByPacienteId(Long pacienteId);
    List<Anamnese> findAll();
}

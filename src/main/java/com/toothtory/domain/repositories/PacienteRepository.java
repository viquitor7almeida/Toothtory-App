package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository {
    void save(Paciente paciente);
    void update(Paciente paciente);
    void delete(String id);
    Optional<Paciente> findById(String id);
    List<Paciente> findAll();
    List<Paciente> searchByName(String nome);
}
package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Consulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository {
    void save(Consulta consulta);
    void update(Consulta consulta);
    void delete(String id);
    Optional<Consulta> findById(String id);
    List<Consulta> findAll();
    List<Consulta> findByPacienteId(String pacienteId);
    List<Consulta> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
    double somaValorPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
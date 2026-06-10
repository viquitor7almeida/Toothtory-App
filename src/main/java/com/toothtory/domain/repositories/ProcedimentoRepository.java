package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Procedimento;
import java.util.List;
import java.util.Optional;

public interface ProcedimentoRepository {
    void save(Procedimento procedimento);
    void update(Procedimento procedimento);
    void delete(Long id);
    Optional<Procedimento> findById(Long id);
    List<Procedimento> findAll();
    List<Procedimento> searchByName(String nome);
}
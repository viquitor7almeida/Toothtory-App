package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Procedimento;
import java.util.List;
import java.util.Optional;

public interface ProcedimentoRepository {
    void save(Procedimento procedimento);
    void update(Procedimento procedimento);
    void delete(String id);
    Optional<Procedimento> findById(String id);
    List<Procedimento> findAll();
    List<Procedimento> searchByName(String nome);
}
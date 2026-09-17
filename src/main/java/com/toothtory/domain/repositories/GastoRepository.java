package com.toothtory.domain.repositories;

import com.toothtory.domain.entities.Gasto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GastoRepository {
    void save(Gasto gasto);
    void update(Gasto gasto);
    void delete(Long id);
    Optional<Gasto> findById(Long id);
    List<Gasto> findAll();
    List<Gasto> findByPeriodo(LocalDate inicio, LocalDate fim);
    double somaValorPorPeriodo(LocalDate inicio, LocalDate fim);
}

package com.toothtory.infra.repositories;

import com.toothtory.domain.entities.Anamnese;
import com.toothtory.domain.repositories.AnamneseRepository;
import com.toothtory.infra.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnamneseRepositoryImpl implements AnamneseRepository {
    @Override
    public void save(Anamnese anamnese) {
        String sql = "INSERT INTO anamnese (pacienteId, diabetes, hipertenso, problemasRimPancreas, problemasPulmonares, observacoes, dataCriacao, dataAtualizacao) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, anamnese.getPacienteId());
            stmt.setInt(2, anamnese.isDiabetes() ? 1 : 0);
            stmt.setInt(3, anamnese.isHipertenso() ? 1 : 0);
            stmt.setInt(4, anamnese.isProblemasRimPancreas() ? 1 : 0);
            stmt.setInt(5, anamnese.isProblemasPulmonares() ? 1 : 0);
            stmt.setString(6, anamnese.getObservacoes());
            stmt.setString(7, anamnese.getDataCriacao().toString());
            stmt.setString(8, anamnese.getDataAtualizacao().toString());
            stmt.executeUpdate();
            try (Statement keysStmt = conn.createStatement();
                 ResultSet rs = keysStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    anamnese.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Anamnese anamnese) {
        String sql = "UPDATE anamnese SET diabetes=?, hipertenso=?, problemasRimPancreas=?, problemasPulmonares=?, observacoes=?, dataAtualizacao=? WHERE pacienteId=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anamnese.isDiabetes() ? 1 : 0);
            stmt.setInt(2, anamnese.isHipertenso() ? 1 : 0);
            stmt.setInt(3, anamnese.isProblemasRimPancreas() ? 1 : 0);
            stmt.setInt(4, anamnese.isProblemasPulmonares() ? 1 : 0);
            stmt.setString(5, anamnese.getObservacoes());
            stmt.setString(6, LocalDateTime.now().toString());
            stmt.setLong(7, anamnese.getPacienteId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Anamnese> findByPacienteId(Long pacienteId) {
        String sql = "SELECT * FROM anamnese WHERE pacienteId=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pacienteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractAnamnese(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Anamnese> findAll() {
        List<Anamnese> anamneses = new ArrayList<>();
        String sql = "SELECT * FROM anamnese";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                anamneses.add(extractAnamnese(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anamneses;
    }

    private Anamnese extractAnamnese(ResultSet rs) throws SQLException {
        Anamnese a = new Anamnese();
        a.setId(rs.getLong("id"));
        a.setPacienteId(rs.getLong("pacienteId"));
        a.setDiabetes(rs.getInt("diabetes") == 1);
        a.setHipertenso(rs.getInt("hipertenso") == 1);
        a.setProblemasRimPancreas(rs.getInt("problemasRimPancreas") == 1);
        a.setProblemasPulmonares(rs.getInt("problemasPulmonares") == 1);
        a.setObservacoes(rs.getString("observacoes"));
        a.setDataCriacao(LocalDateTime.parse(rs.getString("dataCriacao")));
        a.setDataAtualizacao(LocalDateTime.parse(rs.getString("dataAtualizacao")));
        return a;
    }
}

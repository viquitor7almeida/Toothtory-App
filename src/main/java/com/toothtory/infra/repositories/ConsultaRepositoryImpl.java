package com.toothtory.infra.repositories;

import com.toothtory.domain.entities.Consulta;
import com.toothtory.domain.repositories.ConsultaRepository;
import com.toothtory.infra.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepositoryImpl implements ConsultaRepository {
    @Override
    public void save(Consulta consulta) {
        String sql = "INSERT INTO consultas (pacienteId, dataHora, nomeProcedimento, valorProcedimento, observacoes, dataCriacao) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, consulta.getPacienteId());
            stmt.setString(2, consulta.getDataHora().toString());
            stmt.setString(3, consulta.getNomeProcedimento());
            stmt.setDouble(4, consulta.getValorProcedimento());
            stmt.setString(5, consulta.getObservacoes());
            stmt.setString(6, consulta.getDataCriacao().toString());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                consulta.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Consulta consulta) {
        String sql = "UPDATE consultas SET pacienteId=?, dataHora=?, nomeProcedimento=?, valorProcedimento=?, observacoes=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, consulta.getPacienteId());
            stmt.setString(2, consulta.getDataHora().toString());
            stmt.setString(3, consulta.getNomeProcedimento());
            stmt.setDouble(4, consulta.getValorProcedimento());
            stmt.setString(5, consulta.getObservacoes());
            stmt.setLong(6, consulta.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM consultas WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Consulta> findById(Long id) {
        String sql = "SELECT * FROM consultas WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractConsulta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Consulta> findAll() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM consultas ORDER BY dataHora DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                consultas.add(extractConsulta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    @Override
    public List<Consulta> findByPacienteId(Long pacienteId) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM consultas WHERE pacienteId=? ORDER BY dataHora DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pacienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                consultas.add(extractConsulta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    @Override
    public List<Consulta> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM consultas WHERE dataHora BETWEEN ? AND ? ORDER BY dataHora DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inicio.toString());
            stmt.setString(2, fim.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                consultas.add(extractConsulta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    @Override
    public double somaValorPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT SUM(valorProcedimento) FROM consultas WHERE dataHora BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inicio.toString());
            stmt.setString(2, fim.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Consulta extractConsulta(ResultSet rs) throws SQLException {
        Consulta c = new Consulta();
        c.setId(rs.getLong("id"));
        c.setPacienteId(rs.getLong("pacienteId"));
        c.setDataHora(LocalDateTime.parse(rs.getString("dataHora")));
        c.setNomeProcedimento(rs.getString("nomeProcedimento"));
        c.setValorProcedimento(rs.getDouble("valorProcedimento"));
        c.setObservacoes(rs.getString("observacoes"));
        c.setDataCriacao(LocalDateTime.parse(rs.getString("dataCriacao")));
        return c;
    }
}
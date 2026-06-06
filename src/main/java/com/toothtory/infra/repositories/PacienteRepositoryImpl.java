package com.toothtory.infra.repositories;

import com.toothtory.domain.entities.Paciente;
import com.toothtory.domain.repositories.PacienteRepository;
import com.toothtory.infra.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepositoryImpl implements PacienteRepository {
    @Override
    public void save(Paciente paciente) {
        String sql = "INSERT INTO pacientes (id, nome, endereco, email, celular, dataCriacao, dataAtualizacao) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getId());
            stmt.setString(2, paciente.getNome());
            stmt.setString(3, paciente.getEndereco());
            stmt.setString(4, paciente.getEmail());
            stmt.setString(5, paciente.getCelular());
            stmt.setString(6, paciente.getDataCriacao().toString());
            stmt.setString(7, paciente.getDataAtualizacao().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Paciente paciente) {
        String sql = "UPDATE pacientes SET nome=?, endereco=?, email=?, celular=?, dataAtualizacao=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getEndereco());
            stmt.setString(3, paciente.getEmail());
            stmt.setString(4, paciente.getCelular());
            stmt.setString(5, LocalDateTime.now().toString());
            stmt.setString(6, paciente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM pacientes WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Paciente> findById(String id) {
        String sql = "SELECT * FROM pacientes WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractPaciente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Paciente> findAll() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pacientes.add(extractPaciente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    @Override
    public List<Paciente> searchByName(String nome) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE nome LIKE ? ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pacientes.add(extractPaciente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    private Paciente extractPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getString("id"));
        p.setNome(rs.getString("nome"));
        p.setEndereco(rs.getString("endereco"));
        p.setEmail(rs.getString("email"));
        p.setCelular(rs.getString("celular"));
        p.setDataCriacao(LocalDateTime.parse(rs.getString("dataCriacao")));
        p.setDataAtualizacao(LocalDateTime.parse(rs.getString("dataAtualizacao")));
        return p;
    }
}
package com.toothtory.infra.repositories;

import com.toothtory.domain.entities.Procedimento;
import com.toothtory.domain.repositories.ProcedimentoRepository;
import com.toothtory.infra.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcedimentoRepositoryImpl implements ProcedimentoRepository {
    @Override
    public void save(Procedimento procedimento) {
        String sql = "INSERT INTO procedimentos (id, nome, valor, dataCriacao, dataAtualizacao) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, procedimento.getId());
            stmt.setString(2, procedimento.getNome());
            stmt.setDouble(3, procedimento.getValor());
            stmt.setString(4, procedimento.getDataCriacao().toString());
            stmt.setString(5, procedimento.getDataAtualizacao().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Procedimento procedimento) {
        String sql = "UPDATE procedimentos SET nome=?, valor=?, dataAtualizacao=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, procedimento.getNome());
            stmt.setDouble(2, procedimento.getValor());
            stmt.setString(3, LocalDateTime.now().toString());
            stmt.setString(4, procedimento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM procedimentos WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Procedimento> findById(String id) {
        String sql = "SELECT * FROM procedimentos WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractProcedimento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Procedimento> findAll() {
        List<Procedimento> procedimentos = new ArrayList<>();
        String sql = "SELECT * FROM procedimentos ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                procedimentos.add(extractProcedimento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procedimentos;
    }

    @Override
    public List<Procedimento> searchByName(String nome) {
        List<Procedimento> procedimentos = new ArrayList<>();
        String sql = "SELECT * FROM procedimentos WHERE nome LIKE ? ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                procedimentos.add(extractProcedimento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procedimentos;
    }

    private Procedimento extractProcedimento(ResultSet rs) throws SQLException {
        Procedimento p = new Procedimento();
        p.setId(rs.getString("id"));
        p.setNome(rs.getString("nome"));
        p.setValor(rs.getDouble("valor"));
        p.setDataCriacao(LocalDateTime.parse(rs.getString("dataCriacao")));
        p.setDataAtualizacao(LocalDateTime.parse(rs.getString("dataAtualizacao")));
        return p;
    }
}
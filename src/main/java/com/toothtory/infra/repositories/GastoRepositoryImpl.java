package com.toothtory.infra.repositories;

import com.toothtory.domain.entities.Gasto;
import com.toothtory.domain.repositories.GastoRepository;
import com.toothtory.infra.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GastoRepositoryImpl implements GastoRepository {
    @Override
    public void save(Gasto gasto) {
        String sql = "INSERT INTO gastos (nome, valor, data, dataCriacao) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gasto.getNome());
            stmt.setDouble(2, gasto.getValor());
            stmt.setString(3, gasto.getData().toString());
            stmt.setString(4, gasto.getDataCriacao().toString());
            stmt.executeUpdate();
            try (Statement keysStmt = conn.createStatement();
                 ResultSet rs = keysStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    gasto.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Gasto gasto) {
        String sql = "UPDATE gastos SET nome=?, valor=?, data=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gasto.getNome());
            stmt.setDouble(2, gasto.getValor());
            stmt.setString(3, gasto.getData().toString());
            stmt.setLong(4, gasto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM gastos WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Gasto> findById(Long id) {
        String sql = "SELECT * FROM gastos WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractGasto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Gasto> findAll() {
        List<Gasto> gastos = new ArrayList<>();
        String sql = "SELECT * FROM gastos ORDER BY data DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                gastos.add(extractGasto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gastos;
    }

    @Override
    public List<Gasto> findByPeriodo(LocalDate inicio, LocalDate fim) {
        List<Gasto> gastos = new ArrayList<>();
        String sql = "SELECT * FROM gastos WHERE data BETWEEN ? AND ? ORDER BY data DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inicio.toString());
            stmt.setString(2, fim.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gastos.add(extractGasto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gastos;
    }

    @Override
    public double somaValorPorPeriodo(LocalDate inicio, LocalDate fim) {
        String sql = "SELECT SUM(valor) FROM gastos WHERE data BETWEEN ? AND ?";
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

    private Gasto extractGasto(ResultSet rs) throws SQLException {
        Gasto g = new Gasto();
        g.setId(rs.getLong("id"));
        g.setNome(rs.getString("nome"));
        g.setValor(rs.getDouble("valor"));
        g.setData(LocalDate.parse(rs.getString("data")));
        g.setDataCriacao(LocalDateTime.parse(rs.getString("dataCriacao")));
        return g;
    }
}

package com.toothtory.infra.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String createPacientes = """
            CREATE TABLE IF NOT EXISTS pacientes (
                id TEXT PRIMARY KEY,
                nome TEXT NOT NULL,
                endereco TEXT,
                email TEXT,
                celular TEXT,
                dataCriacao TEXT NOT NULL,
                dataAtualizacao TEXT NOT NULL
            )
        """;
        String createProcedimentos = """
            CREATE TABLE IF NOT EXISTS procedimentos (
                id TEXT PRIMARY KEY,
                nome TEXT NOT NULL,
                valor REAL NOT NULL,
                dataCriacao TEXT NOT NULL,
                dataAtualizacao TEXT NOT NULL
            )
        """;
        String createConsultas = """
            CREATE TABLE IF NOT EXISTS consultas (
                id TEXT PRIMARY KEY,
                pacienteId TEXT NOT NULL,
                dataHora TEXT NOT NULL,
                nomeProcedimento TEXT NOT NULL,
                valorProcedimento REAL NOT NULL,
                observacoes TEXT,
                dataCriacao TEXT NOT NULL,
                FOREIGN KEY (pacienteId) REFERENCES pacientes(id) ON DELETE CASCADE
            )
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPacientes);
            stmt.execute(createProcedimentos);
            stmt.execute(createConsultas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
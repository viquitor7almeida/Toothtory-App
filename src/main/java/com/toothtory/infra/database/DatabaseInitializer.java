package com.toothtory.infra.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String createPacientes = """
            CREATE TABLE IF NOT EXISTS pacientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf TEXT,
                endereco TEXT,
                email TEXT,
                celular TEXT,
                dataCriacao TEXT NOT NULL,
                dataAtualizacao TEXT NOT NULL
            )
        """;
        String createProcedimentos = """
            CREATE TABLE IF NOT EXISTS procedimentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                valor REAL NOT NULL,
                dataCriacao TEXT NOT NULL,
                dataAtualizacao TEXT NOT NULL
            )
        """;
        String createConsultas = """
            CREATE TABLE IF NOT EXISTS consultas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pacienteId INTEGER NOT NULL,
                dataHora TEXT NOT NULL,
                nomeProcedimento TEXT NOT NULL,
                valorProcedimento REAL NOT NULL,
                observacoes TEXT,
                dataCriacao TEXT NOT NULL,
                FOREIGN KEY (pacienteId) REFERENCES pacientes(id) ON DELETE CASCADE
            )
        """;
        String createGastos = """
            CREATE TABLE IF NOT EXISTS gastos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                valor REAL NOT NULL,
                data TEXT NOT NULL,
                dataCriacao TEXT NOT NULL
            )
        """;
        String createAnamnese = """
            CREATE TABLE IF NOT EXISTS anamnese (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pacienteId INTEGER NOT NULL UNIQUE,
                diabetes INTEGER NOT NULL DEFAULT 0,
                hipertenso INTEGER NOT NULL DEFAULT 0,
                problemasRimPancreas INTEGER NOT NULL DEFAULT 0,
                problemasPulmonares INTEGER NOT NULL DEFAULT 0,
                observacoes TEXT,
                dataCriacao TEXT NOT NULL,
                dataAtualizacao TEXT NOT NULL,
                FOREIGN KEY (pacienteId) REFERENCES pacientes(id) ON DELETE CASCADE
            )
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPacientes);
            stmt.execute(createProcedimentos);
            stmt.execute(createConsultas);
            stmt.execute(createGastos);
            stmt.execute(createAnamnese);
            migrarColuna(stmt, "ALTER TABLE pacientes ADD COLUMN cpf TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void migrarColuna(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            // coluna já existe — migração já aplicada, ignora
        }
    }
}

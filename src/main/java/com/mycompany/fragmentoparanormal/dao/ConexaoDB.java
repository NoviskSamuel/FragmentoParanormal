package com.mycompany.fragmentoparanormal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerenciador de conexão com o banco de dados PostgreSQL
 */
public class ConexaoDB {
    private static Connection conexao;
    private static final String URL = "jdbc:postgresql://localhost:5432/fragmentoparanormal";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    /**
     * Obtém a conexão com o banco de dados
     * @return Connection ativa com o banco
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL não encontrado", e);
            }
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                conexao = null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}

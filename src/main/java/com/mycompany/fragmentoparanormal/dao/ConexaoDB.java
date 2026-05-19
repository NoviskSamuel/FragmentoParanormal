package com.mycompany.fragmentoparanormal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerenciador de conexão com o banco de dados PostgreSQL
 */
public class ConexaoDB {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/fragmento_paranormal";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";
    
    private static Connection conexao;
    
    /**
     * Obtém uma conexão com o banco de dados.
     * Se a conexão estiver fechada, cria uma nova.
     * 
     * @return Connection ativa com o banco de dados
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConexao() throws SQLException {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado", e);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.mycompany.fragmentoparanormal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    private static final String URL    = "jdbc:postgresql://localhost:5432/fragmento_paranormal";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "sua_senha";

    private static Connection conexao;

    private ConexaoDB() {}

    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        }
        return conexao;
    }

    public static void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
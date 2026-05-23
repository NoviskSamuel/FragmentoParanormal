package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static Connection conexao;

    private static final String URL     = "jdbc:postgresql://localhost:5432/fragmentoparanormal";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "password";

    private ConexaoDB() {}

    public static synchronized Connection getConexao() throws SQLException {
        if (conexao == null || !conexao.isValid(2)) {
            fecharSilenciosamente();
            try {
                Class.forName("org.postgresql.Driver");
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                conexao.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL não encontrado no classpath.", e);
            }
        }
        return conexao;
    }

    public static synchronized void fecharConexao() {
        fecharSilenciosamente();
    }

    private static void fecharSilenciosamente() {
        if (conexao != null) {
            try { conexao.close(); } catch (SQLException ignored) {}
            conexao = null;
        }
    }
}
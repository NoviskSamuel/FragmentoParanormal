package Dao;

import java.sql.*;

public class UpgradeDAO {

    public void registrarUpgrade(int personagemId, String atributo, int pontos) throws SQLException {
        String sql = """
            INSERT INTO upgrade (personagem_id, atributo, pontos_gastos)
            VALUES (?,?,?)
            ON CONFLICT DO NOTHING
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setString(2, atributo);
            ps.setInt(3, pontos);
            ps.executeUpdate();
        }
    }

    public int totalPontosGastos(int personagemId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(pontos_gastos),0) FROM upgrade WHERE personagem_id=?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
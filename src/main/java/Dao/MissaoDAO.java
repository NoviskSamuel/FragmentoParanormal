package Dao;

import Model.Missao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a classe Missao
 */
public class MissaoDAO {

    public List<Missao> listarMissoes() throws SQLException {
        List<Missao> lista = new ArrayList<>();
        String sql = "SELECT * FROM missao ORDER BY nivel_minimo";
        try (Statement st = ConexaoDB.getConexao().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void salvarProgresso(int personagemId, Missao m) throws SQLException {
        String check = "SELECT id FROM progresso_missao WHERE personagem_id=? AND missao_id=?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(check)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, m.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String upd = "UPDATE progresso_missao SET progresso_atual=?, sala_atual=?, concluida=?, vezes_retornou=?, fugiu=? WHERE personagem_id=? AND missao_id=?";
                try (PreparedStatement u = ConexaoDB.getConexao().prepareStatement(upd)) {
                    u.setInt(1, m.getProgressoAtual());
                    u.setInt(2, m.getSalaAtual());
                    u.setBoolean(3, m.isConcluida());
                    u.setInt(4, m.getVezesRetornou());
                    u.setBoolean(5, m.isFugiu());
                    u.setInt(6, personagemId);
                    u.setInt(7, m.getId());
                    u.executeUpdate();
                }
            } else {
                String ins = "INSERT INTO progresso_missao (personagem_id, missao_id, progresso_atual, sala_atual, concluida, vezes_retornou, fugiu) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement i = ConexaoDB.getConexao().prepareStatement(ins)) {
                    i.setInt(1, personagemId);
                    i.setInt(2, m.getId());
                    i.setInt(3, m.getProgressoAtual());
                    i.setInt(4, m.getSalaAtual());
                    i.setBoolean(5, m.isConcluida());
                    i.setInt(6, m.getVezesRetornou());
                    i.setBoolean(7, m.isFugiu());
                    i.executeUpdate();
                }
            }
        }
    }

    public Missao carregarProgresso(int personagemId, int missaoId) throws SQLException {
        String sql = "SELECT m.*, pm.progresso_atual, pm.sala_atual, pm.concluida, pm.vezes_retornou, pm.fugiu FROM missao m LEFT JOIN progresso_missao pm ON m.id = pm.missao_id AND pm.personagem_id = ? WHERE m.id = ?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, missaoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Missao m = mapear(rs);
                m.setProgressoAtual(rs.getInt("progresso_atual"));
                m.setSalaAtual(rs.getInt("sala_atual"));
                m.setConcluida(rs.getBoolean("concluida"));
                m.setVezesRetornou(rs.getInt("vezes_retornou"));
                m.setFugiu(rs.getBoolean("fugiu"));
                return m;
            }
        }
        return null;
    }

    private Missao mapear(ResultSet rs) throws SQLException {
        Missao m = new Missao();
        m.setId(rs.getInt("id"));
        m.setTitulo(rs.getString("titulo"));
        m.setDescricao(rs.getString("descricao"));
        m.setObjetivo(rs.getString("objetivo"));
        m.setTotalObjetivo(rs.getInt("total_objetivo"));
        m.setNivelMinimo(rs.getInt("nivel_minimo"));
        m.setTotalSalas(rs.getInt("total_salas"));
        return m;
    }

    public List<Missao> listarDisponíveis(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void atualizarProgresso(Missao missao) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

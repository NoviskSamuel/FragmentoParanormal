package Dao;

import Model.Missao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MissaoDAO {

    public List<Missao> listarDisponiveis(int nivelJogador, int personagemId) throws SQLException {
        List<Missao> lista = new ArrayList<>();
        String sql = """
            SELECT m.*,
                   COALESCE(pm.progresso_atual, 0) AS progresso_atual,
                   COALESCE(pm.sala_atual,      0) AS sala_atual,
                   COALESCE(pm.concluida,   false) AS concluida,
                   COALESCE(pm.vezes_retornou, 0)  AS vezes_retornou,
                   COALESCE(pm.fugiu,       false)  AS fugiu
            FROM missao m
            LEFT JOIN progresso_missao pm
              ON m.id = pm.missao_id AND pm.personagem_id = ?
            WHERE m.nivel_minimo <= ?
            ORDER BY m.nivel_minimo
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, nivelJogador + 2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Missao m = mapear(rs);
                    m.setProgressoAtual(rs.getInt("progresso_atual"));
            m.setSalaAtual(rs.getInt("sala_atual"));
            m.setConcluida(rs.getBoolean("concluida"));
            m.setVezesRetornou(rs.getInt("vezes_retornou"));
            m.setFugiu(rs.getBoolean("fugiu"));
            m.restaurarPaginasDoProgresso();
            lista.add(m);
                }
            }
        }
        return lista;
    }

    public List<Missao> listarTodas() throws SQLException {
        List<Missao> lista = new ArrayList<>();
        String sql = "SELECT * FROM missao ORDER BY nivel_minimo";
        try (Statement st = ConexaoDB.getConexao().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Missao> listarTodas(int personagemId) throws SQLException {
        List<Missao> lista = new ArrayList<>();
        String sql = """
            SELECT m.*,
                   COALESCE(pm.progresso_atual, 0) AS progresso_atual,
                   COALESCE(pm.sala_atual,      0) AS sala_atual,
                   COALESCE(pm.concluida,   false) AS concluida,
                   COALESCE(pm.vezes_retornou, 0)  AS vezes_retornou,
                   COALESCE(pm.fugiu,       false)  AS fugiu
            FROM missao m
            LEFT JOIN progresso_missao pm
              ON m.id = pm.missao_id AND pm.personagem_id = ?
            ORDER BY m.nivel_minimo
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Missao m = mapear(rs);
                    m.setProgressoAtual(rs.getInt("progresso_atual"));
                    m.setSalaAtual(rs.getInt("sala_atual"));
                    m.setConcluida(rs.getBoolean("concluida"));
                    m.setVezesRetornou(rs.getInt("vezes_retornou"));
                    m.setFugiu(rs.getBoolean("fugiu"));
                    m.restaurarPaginasDoProgresso();
                    lista.add(m);
                }
            }
        }
        return lista;
    }

    public Missao carregarProgresso(int personagemId, int missaoId) throws SQLException {
        String sql = """
            SELECT m.*, pm.progresso_atual, pm.sala_atual, pm.concluida,
                   pm.vezes_retornou, pm.fugiu
            FROM missao m
            LEFT JOIN progresso_missao pm
              ON m.id = pm.missao_id AND pm.personagem_id = ?
            WHERE m.id = ?
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, missaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Missao m = mapear(rs);
                    m.setProgressoAtual(rs.getInt("progresso_atual"));
                    m.setSalaAtual(rs.getInt("sala_atual"));
                    m.setConcluida(rs.getBoolean("concluida"));
                    m.setVezesRetornou(rs.getInt("vezes_retornou"));
                    m.setFugiu(rs.getBoolean("fugiu"));
                    m.restaurarPaginasDoProgresso();
                    return m;
                }
            }
        }
        return null;
    }

    public void salvarProgresso(int personagemId, Missao m) throws SQLException {
        String sql = """
            INSERT INTO progresso_missao
              (personagem_id, missao_id, progresso_atual, sala_atual,
               concluida, vezes_retornou, fugiu)
            VALUES (?,?,?,?,?,?,?)
            ON CONFLICT (personagem_id, missao_id)
            DO UPDATE SET
              progresso_atual = EXCLUDED.progresso_atual,
              sala_atual      = EXCLUDED.sala_atual,
              concluida       = EXCLUDED.concluida,
              vezes_retornou  = EXCLUDED.vezes_retornou,
              fugiu           = EXCLUDED.fugiu
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1,     personagemId);
            ps.setInt(2,     m.getId());
            ps.setInt(3,     m.getProgressoAtual());
            ps.setInt(4,     m.getSalaAtual());
            ps.setBoolean(5, m.isConcluida());
            ps.setInt(6,     m.getVezesRetornou());
            ps.setBoolean(7, m.isFugiu());
            ps.executeUpdate();
        }
    }

    public void atualizarProgresso(Missao m, int personagemId) throws SQLException {
        salvarProgresso(personagemId, m);
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
}
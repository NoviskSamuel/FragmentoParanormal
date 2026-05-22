package com.mycompany.fragmentoparanormal.dao;

import com.mycompany.fragmentoparanormal.model.Ranking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {

    public List<Ranking> buscarRanking() throws SQLException {
        List<Ranking> lista = new ArrayList<>();
        String sql = """
            SELECT p.nome, p.nivel, p.moedas,
                   COALESCE(m.titulo, 'Nenhuma') AS missao_atual,
                   COALESCE(
                     (SELECT SUM(inimigos_mortos) FROM historico_partida WHERE personagem_id = p.id), 0
                   ) AS inimigos_abatidos
            FROM personagem p
            LEFT JOIN progresso_missao pm ON pm.personagem_id = p.id AND pm.concluida = FALSE
            LEFT JOIN missao m ON m.id = pm.missao_id
            ORDER BY p.nivel DESC, p.moedas DESC
            LIMIT 20
            """;
        try (Statement st = ConexaoDB.getConexao().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int pos = 1;
            while (rs.next()) {
                Ranking r = new Ranking(
                    rs.getString("nome"),
                    rs.getInt("nivel"),
                    rs.getString("missao_atual"),
                    rs.getInt("moedas"),
                    rs.getInt("inimigos_abatidos")
                );
                r.setPosicao(pos++);
                lista.add(r);
            }
        }
        return lista;
    }

    public void registrarPartida(int personagemId, int missaoId,
                                  String resultado, int inimigos, int moedas) throws SQLException {
        String sql = """
            INSERT INTO historico_partida
            (personagem_id, missao_id, resultado, inimigos_mortos, moedas_ganhas)
            VALUES (?,?,?,?,?)
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, missaoId);
            ps.setString(3, resultado);
            ps.setInt(4, inimigos);
            ps.setInt(5, moedas);
            ps.executeUpdate();
        }
    }
}
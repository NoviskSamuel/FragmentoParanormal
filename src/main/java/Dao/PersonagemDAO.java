package Dao;

import Model.Personagem;
import Util.Classe;
import Util.Elemento;
import Util.Genero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonagemDAO {

    public void salvar(Personagem p) throws SQLException {
        String sql = """
            INSERT INTO personagem
            (nome, nivel, classe, genero, elemento, imagem_path,
             xp_atual, xp_proximo_nivel, forca, poder_paranormal,
             investigacao, vida_maxima, vida_atual, moedas)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  p.getNome());
            ps.setInt(2,     p.getNivel());
            ps.setString(3,  p.getClasse().name());
            ps.setString(4,  p.getGenero().name());
            ps.setString(5,  p.getElemento().name());
            ps.setString(6,  p.getImagemPath());
            ps.setInt(7,     p.getXpAtual());
            ps.setInt(8,     p.getXpProximoNivel());
            ps.setInt(9,     p.getForca());
            ps.setInt(10,    p.getPoderParanormal());
            ps.setInt(11,    p.getInvestigacao());
            ps.setInt(12,    p.getVidaMaxima());
            ps.setInt(13,    p.getVidaAtual());
            ps.setInt(14,    p.getMoedas());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    public void atualizar(Personagem p) throws SQLException {
        String sql = """
            UPDATE personagem SET
              nivel=?, xp_atual=?, xp_proximo_nivel=?, forca=?,
              poder_paranormal=?, investigacao=?, vida_maxima=?,
              vida_atual=?, moedas=?, imagem_path=?
            WHERE id=?
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1,    p.getNivel());
            ps.setInt(2,    p.getXpAtual());
            ps.setInt(3,    p.getXpProximoNivel());
            ps.setInt(4,    p.getForca());
            ps.setInt(5,    p.getPoderParanormal());
            ps.setInt(6,    p.getInvestigacao());
            ps.setInt(7,    p.getVidaMaxima());
            ps.setInt(8,    p.getVidaAtual());
            ps.setInt(9,    p.getMoedas());
            ps.setString(10, p.getImagemPath());
            ps.setInt(11,   p.getId());
            ps.executeUpdate();
        }
    }

    public List<Personagem> listarTodos() throws SQLException {
        List<Personagem> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem ORDER BY nivel DESC";
        try (Statement st = ConexaoDB.getConexao().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Personagem buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM personagem WHERE id = ?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Personagem buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM personagem WHERE nome = ?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM personagem WHERE id = ?";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Personagem mapear(ResultSet rs) throws SQLException {
        Personagem p = new Personagem();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setNivel(rs.getInt("nivel"));
        p.setClasse(Classe.valueOf(rs.getString("classe")));
        p.setGenero(Genero.valueOf(rs.getString("genero")));
        p.setElemento(Elemento.valueOf(rs.getString("elemento")));
        p.setImagemPath(rs.getString("imagem_path"));
        p.setXpAtual(rs.getInt("xp_atual"));
        p.setXpProximoNivel(rs.getInt("xp_proximo_nivel"));
        p.setForca(rs.getInt("forca"));
        p.setPoderParanormal(rs.getInt("poder_paranormal"));
        p.setInvestigacao(rs.getInt("investigacao"));
        p.setVidaMaxima(rs.getInt("vida_maxima"));
        p.setVidaAtual(rs.getInt("vida_atual"));
        p.setMoedas(rs.getInt("moedas"));
        return p;
    }
}
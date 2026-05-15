package com.mycompany.fragmentoparanormal.dao;

import br.edu.fragmento.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public void adicionarItem(int personagemId, int itemId) throws SQLException {
        String sql = "INSERT INTO inventario (personagem_id, item_id) VALUES (?,?)";
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    public void equiparItem(int personagemId, int itemId) throws SQLException {
        // Desequipa tudo antes
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(
                "UPDATE inventario SET equipado=FALSE WHERE personagem_id=?")) {
            ps.setInt(1, personagemId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(
                "UPDATE inventario SET equipado=TRUE WHERE personagem_id=? AND item_id=?")) {
            ps.setInt(1, personagemId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    public List<Item> listarItens(int personagemId) throws SQLException {
        List<Item> lista = new ArrayList<>();
        String sql = """
            SELECT i.*, inv.equipado FROM item i
            JOIN inventario inv ON inv.item_id = i.id
            WHERE inv.personagem_id = ?
            """;
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(sql)) {
            ps.setInt(1, personagemId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setNome(rs.getString("nome"));
                item.setTipo(rs.getString("tipo"));
                item.setValor(rs.getInt("valor"));
                item.setDescricao(rs.getString("descricao"));
                item.setImagemPath(rs.getString("imagem_path"));
                lista.add(item);
            }
        }
        return lista;
    }

    public void limparInventario(int personagemId) throws SQLException {
        try (PreparedStatement ps = ConexaoDB.getConexao().prepareStatement(
                "DELETE FROM inventario WHERE personagem_id=?")) {
            ps.setInt(1, personagemId);
            ps.executeUpdate();
        }
    }
}

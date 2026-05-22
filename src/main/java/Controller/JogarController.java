package Controller;

import Dao.PersonagemDAO;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class JogarController {

    @FXML private ComboBox<String> comboPersonagens;
    @FXML private Label labelErro;

    @FXML
    public void initialize() {
        carregarPersonagens();
    }

    private void carregarPersonagens() {
        try {
            PersonagemDAO dao = new PersonagemDAO();
            List<Personagem> lista = dao.listarTodos();
            comboPersonagens.getItems().clear();
            for (Personagem p : lista) {
                comboPersonagens.getItems().add(p.getNome());
            }
            if (!comboPersonagens.getItems().isEmpty()) {
                comboPersonagens.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            labelErro.setText("Erro ao carregar personagens.");
        }
    }

    @FXML
    private void onConfirmarExistente() {
        String nomeEscolhido = comboPersonagens.getValue();
        if (nomeEscolhido == null || nomeEscolhido.isBlank()) {
            labelErro.setText("Selecione um personagem.");
            return;
        }
        try {
            PersonagemDAO dao = new PersonagemDAO();
            Personagem p = dao.buscarPorNome(nomeEscolhido);
            if (p == null) {
                labelErro.setText("Personagem não encontrado.");
                return;
            }
            ScreenManager.getInstance().setPersonagemAtivo(p);
            ScreenManager.getInstance().ir(ScreenManager.TELA_CONFIRMACAO);
        } catch (SQLException e) {
            labelErro.setText("Erro ao carregar personagem.");
        }
    }

    @FXML
    private void onNovoJogador() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_NOVO_JOGADOR);
    }

    @FXML
    private void onSair() {
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL);
    }
}
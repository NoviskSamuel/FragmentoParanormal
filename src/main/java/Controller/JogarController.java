package Controller;

import Dao.PersonagemDAO;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
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
            List<Personagem> lista = new PersonagemDAO().listarTodos();
            comboPersonagens.getItems().clear();
            for (Personagem p : lista) {
                comboPersonagens.getItems().add(p.getNome() + " (Nível " + p.getNivel() + ")");
            }
            if (!comboPersonagens.getItems().isEmpty()) {
                comboPersonagens.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            labelErro.setText("Erro ao carregar personagens: " + e.getMessage());
        }
    }

    @FXML
    private void onConfirmarExistente() {
        int idx = comboPersonagens.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            labelErro.setText("Selecione um personagem.");
            return;
        }

        String entrada = comboPersonagens.getValue();
        String nome = entrada.contains(" (") ? entrada.substring(0, entrada.lastIndexOf(" (")) : entrada;

        try {
            Personagem p = new PersonagemDAO().buscarPorNome(nome);
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

    @FXML private void onNovoJogador() { ScreenManager.getInstance().ir(ScreenManager.TELA_NOVO_JOGADOR); }
    @FXML private void onSair()        { ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL); }
}
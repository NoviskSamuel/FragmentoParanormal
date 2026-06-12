package Controller;

import Dao.MissaoDAO;
import Model.Missao;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class MissoesController {

    @FXML private ListView<String> listaMissoes;
    @FXML private Label labelTitulo;
    @FXML private Label labelDescricao;
    @FXML private Label labelObjetivo;
    @FXML private Label labelNivelMin;
    @FXML private Label labelProgresso;
    @FXML private Label labelErro;
    @FXML private Button btnConfirmar;

    private List<Missao> missoes;
    private final Personagem jogador = ScreenManager.getInstance().getPersonagemAtivo();

    @FXML
    public void initialize() {
        carregarMissoes();
        listaMissoes.getSelectionModel().selectedIndexProperty()
            .addListener((obs, o, n) -> {
                int i = n.intValue();
                if (i >= 0 && i < missoes.size()) exibirDetalhe(missoes.get(i));
            });
    }

    private void carregarMissoes() {
        try {
            int nivel = jogador != null ? jogador.getNivel() : 1;
            missoes = new MissaoDAO().listarDisponiveis(nivel);
            listaMissoes.getItems().clear();
            for (Missao m : missoes) {
                String tag = m.isConcluida() ? " ✓" : "";
                listaMissoes.getItems().add(m.getTitulo() + tag);
            }
            if (!missoes.isEmpty()) listaMissoes.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            labelErro.setText("Erro ao carregar missões: " + e.getMessage());
        }
    }

    private void exibirDetalhe(Missao m) {
        labelTitulo.setText(m.getTitulo());
        labelDescricao.setText(m.getDescricao());
        labelObjetivo.setText("Objetivo: " + m.getObjetivo());
        labelNivelMin.setText("Nível mínimo: " + m.getNivelMinimo());
        labelProgresso.setText("Progresso: " + m.getProgressoTexto());
        labelErro.setText("");
    }

    @FXML
    private void onConfirmar() {
        int idx = listaMissoes.getSelectionModel().getSelectedIndex();
        if (idx < 0 || idx >= missoes.size()) {
            labelErro.setText("Selecione uma missão.");
            return;
        }
        Missao escolhida = missoes.get(idx);
        if (jogador != null && jogador.getNivel() < escolhida.getNivelMinimo()) {
            labelErro.setText("Nível insuficiente! Esta missão exige nível " + escolhida.getNivelMinimo() + ".");
            return;
        }

        try {
            if (jogador != null) {
                Missao comProgresso = new MissaoDAO().carregarProgresso(jogador.getId(), escolhida.getId());
                if (comProgresso != null) escolhida = comProgresso;
            }
        } catch (SQLException ignored) {}

        ScreenManager.getInstance().setMissaoAtiva(escolhida);
        ScreenManager.getInstance().ir(ScreenManager.TELA_MISSAO);
    }

    @FXML private void onSair() { ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL); }
}
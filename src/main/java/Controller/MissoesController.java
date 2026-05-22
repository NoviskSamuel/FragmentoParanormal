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
    @FXML private Label labelErro;
    @FXML private Button btnConfirmar;

    private List<Missao> missoes;

    @FXML
    public void initialize() {
        Personagem p = ScreenManager.getInstance().getPersonagemAtivo();
        carregarMissoes(p);

        listaMissoes.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            int idx = n.intValue();
            if (idx >= 0 && idx < missoes.size()) exibirDetalhe(missoes.get(idx));
        });
    }

    private void carregarMissoes(Personagem p) {
        MissaoDAO dao = new MissaoDAO();
        missoes = dao.listarDisponíveis(p != null ? p.getNivel() : 1);
        listaMissoes.getItems().clear();
        for (Missao m : missoes) {
            listaMissoes.getItems().add(
                    m.getTitulo() + (m.isConcluida() ? " ✓" : "")
            );
        }
        if (!missoes.isEmpty()) {
            listaMissoes.getSelectionModel().selectFirst();
        }
    }

    private void exibirDetalhe(Missao m) {
        labelTitulo.setText(m.getTitulo());
        labelDescricao.setText(m.getDescricao());
        labelObjetivo.setText("Objetivo: " + m.getObjetivo()
            + " (" + m.getProgressoAtual() + "/" + m.getTotalObjetivo() + ")");
        labelNivelMin.setText("Nível mínimo: " + m.getNivelMinimo());
    }

    @FXML
    private void onConfirmar() {
        int idx = listaMissoes.getSelectionModel().getSelectedIndex();
        if (idx < 0) { labelErro.setText("Selecione uma missão."); return; }

        Missao escolhida = missoes.get(idx);
        Personagem p = ScreenManager.getInstance().getPersonagemAtivo();
        if (p != null && p.getNivel() < escolhida.getNivelMinimo()) {
            labelErro.setText("Nível insuficiente para esta missão.");
            return;
        }

        ScreenManager.getInstance().setMissaoAtiva(escolhida);
        ScreenManager.getInstance().ir(ScreenManager.TELA_MISSAO);
    }

    @FXML
    private void onSair() {
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL);
    }
}
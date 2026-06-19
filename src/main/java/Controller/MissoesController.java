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
    private Personagem jogador;

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        carregarMissoes();
        listaMissoes.getSelectionModel().selectedIndexProperty()
            .addListener((obs, o, n) -> {
                int i = n.intValue();
                if (i >= 0 && i < missoes.size()) exibirDetalhe(missoes.get(i));
            });
    }

    private void carregarMissoes() {
        try {
            int nivel        = jogador != null ? jogador.getNivel() : 1;
            int personagemId = jogador != null ? jogador.getId()    : 0;
            missoes = new MissaoDAO().listarTodas(personagemId);
            listaMissoes.getItems().clear();
            for (Missao m : missoes) {
                boolean bloqueada = m.getNivelMinimo() > nivel;
                String tag = m.isConcluida() ? " ✓" : bloqueada ? " 🔒 (Nv." + m.getNivelMinimo() + ")" : "";
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
        int nivelAtual = jogador != null ? jogador.getNivel() : 1;
        if (escolhida.getNivelMinimo() > nivelAtual) {
            labelErro.setText("🔒 Missão bloqueada! Exige nível " + escolhida.getNivelMinimo() + ".");
            return;
        }
        if (escolhida.isConcluida()) {
            labelErro.setText("Missão já concluída! Você não pode refazê-la.");
            return;
        }
        ScreenManager.getInstance().setMissaoAtiva(escolhida);
        ScreenManager.getInstance().ir(ScreenManager.TELA_MISSAO);
    }
    
    @FXML
private void onLoja() {
    System.out.println("Abrindo a loja...");
    ScreenManager.getInstance().ir(ScreenManager.TELA_LOJA);
}

    @FXML
    private void onSair() {
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL);
    }
}
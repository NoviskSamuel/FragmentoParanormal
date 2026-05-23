package Controller;

import Dao.RankingDAO;
import Model.Ranking;
import Util.ScreenManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class RankingController {

    @FXML private TableView<Ranking> tabelaRanking;
    @FXML private TableColumn<Ranking, Integer> colPosicao;
    @FXML private TableColumn<Ranking, String>  colNome;
    @FXML private TableColumn<Ranking, Integer> colNivel;
    @FXML private TableColumn<Ranking, Integer> colMoedas;
    @FXML private TableColumn<Ranking, Integer> colInimigos;
    @FXML private Label labelErro;

    @FXML
    public void initialize() {
        colPosicao.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPosicao()).asObject());
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeJogador()));
        colNivel.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNivel()).asObject());
        colMoedas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMoedasTotais()).asObject());
        colInimigos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getInimigosAbatidos()).asObject());
        carregarRanking();
    }

    private void carregarRanking() {
        try {
            List<Ranking> lista = new RankingDAO().buscarRanking();
            tabelaRanking.getItems().setAll(lista);
        } catch (SQLException e) {
            labelErro.setText("Erro ao carregar ranking: " + e.getMessage());
        }
    }

    @FXML private void onVoltar() { ScreenManager.getInstance().voltar(); }
}
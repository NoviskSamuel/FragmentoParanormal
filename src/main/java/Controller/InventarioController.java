package Controller;

import Model.Item;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class InventarioController {

    @FXML private ListView<String> listaItens;
    @FXML private Label labelDescItem;

    private Personagem jogador;

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        if (jogador == null) return;
        for (Item item : jogador.getInventario().getItens()) {
            listaItens.getItems().add(item.getNome() + " [" + item.getTipo() + "]");
        }
        listaItens.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            int i = n.intValue();
            if (i >= 0 && i < jogador.getInventario().getItens().size()) {
                Item sel = jogador.getInventario().getItens().get(i);
                labelDescItem.setText(sel.getDescricao());
            }
        });
    }

    @FXML
    private void onEquipar() {
        int i = listaItens.getSelectionModel().getSelectedIndex();
        if (i >= 0 && i < jogador.getInventario().getItens().size()) {
            Item sel = jogador.getInventario().getItens().get(i);
            jogador.getInventario().equiparItem(sel);
            labelDescItem.setText("✓ Equipado: " + sel.getNome());
        }
    }

    @FXML
    private void onUsarPocao() {
        int i = listaItens.getSelectionModel().getSelectedIndex();
        if (i >= 0 && i < jogador.getInventario().getItens().size()) {
            Item sel = jogador.getInventario().getItens().get(i);
            if (sel.isPocao()) {
                jogador.curar(sel.getValor());
                jogador.getInventario().removerItem(sel);
                listaItens.getItems().remove(i);
                labelDescItem.setText("✓ Poção usada. Vida restaurada!");
            } else {
                labelDescItem.setText("Este item não é uma poção.");
            }
        }
    }

    @FXML private void onVoltar() { ScreenManager.getInstance().voltar(); }
}